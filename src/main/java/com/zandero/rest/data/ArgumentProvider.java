package com.zandero.rest.data;

import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.context.ContextProviders;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ContextException;
import com.zandero.rest.reader.ReaderFactory;
import com.zandero.rest.reader.ValueReader;
import com.zandero.utils.Assert;
import com.zandero.utils.extra.UrlUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * Extracts arguments to be provided for given method from definition and current context (request)
 */
public class ArgumentProvider {

	private final static Logger log = LoggerFactory.getLogger(ArgumentProvider.class);

	public static Object[] getArguments(Method method,
	                                    RouteDefinition definition,
	                                    RoutingContext context,
	                                    ReaderFactory readers,
	                                    ContextProviders providers) {

		Assert.notNull(method, "Missing method to provide arguments for!");
		Assert.notNull(definition, "Missing route definition!");
		Assert.notNull(context, "Missing vert.x routing context!");

		Class<?>[] methodArguments = method.getParameterTypes();

		if (methodArguments.length == 0) {
			return null;    // no arguments needed ...
		}

		// get parameters and extract from request their values
		List<MethodParameter> params = definition.getParameters(); // returned sorted by index

		Object[] args = new Object[methodArguments.length];

		for (MethodParameter parameter : params) {

			// get value
			String value = getValue(definition, parameter, context, parameter.getDefaultValue());

			// set if we have a place to set it ... otherwise ignore
			if (parameter.getIndex() < args.length) {

				Class<?> dataType = parameter.getDataType();
				if (dataType == null) {
					dataType = methodArguments[parameter.getIndex()];
				}

				try {
					switch (parameter.getType()) {

						case context:

							// check if providers need to be called to assure context
							ContextProvider provider = providers.get(dataType);
							if (provider != null) {
								Object result = provider.provide(context.request());
								if (result != null) {
									context.data().put(getContextKey(dataType), result);
								}
							}

							args[parameter.getIndex()] = provideContext(definition, method.getParameterTypes()[parameter.getIndex()], parameter.getDefaultValue(), context);
							break;

						default:

							ValueReader valueReader = getValueReader(parameter, definition, readers);
							args[parameter.getIndex()] = valueReader.read(value, dataType);
							break;
					}
				} catch (ContextException e) {
					throw new IllegalArgumentException(e.getMessage());
				} catch (Exception e) {

					MethodParameter paramDefinition = definition.findParameter(parameter.getIndex());
					String providedType = value != null ? value.getClass().getSimpleName() : "null";
					String expectedType = method.getParameterTypes()[parameter.getIndex()].getTypeName();

					if (paramDefinition != null) {
						throw new IllegalArgumentException("Invalid parameter type for: " + paramDefinition + " for: " + definition.getPath() +
						                                   ", expected: " + expectedType + ", but got: " + providedType);
					}

					throw new IllegalArgumentException("Invalid parameter type for " + (parameter.getIndex() + 1) + " argument for: " + method +
					                                   " expected: " + expectedType + ", but got: " + providedType);
				}
			}
		}

		// parameter check ...
		for (int index = 0; index < args.length; index++) {
			Parameter param = method.getParameters()[index];
			if (args[index] == null && param.getType().isPrimitive()) {

				MethodParameter paramDefinition = definition.findParameter(index);
				if (paramDefinition != null) {
					throw new IllegalArgumentException("Missing " + paramDefinition + " for: " + definition.getPath());
				}

				throw new IllegalArgumentException("Missing " + (index + 1) + " argument for: " + method +
				                                   " expected: " + param.getType() + ", but: null was provided!");
			}
		}

		return args;
	}

	private static String getValue(RouteDefinition definition, MethodParameter param, RoutingContext context, String defaultValue) {

		String value = getValue(definition, param, context);

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	private static String getValue(RouteDefinition definition, MethodParameter param, RoutingContext context) {

		switch (param.getType()) {
			case path:

				String path;
				if (definition.pathIsRegEx()) { // RegEx is special, params values are given by index
					path = getParam(context.request(), param.getPathIndex());
				}
				else {
					path = context.request().getParam(param.getName());
				}

				// if @MatrixParams are present ... those need to be removed
				path = removeMatrixFromPath(path, definition);
				return path;

			case query:
				Map<String, String> query = UrlUtils.getQuery(context.request().query());
				return query.get(param.getName());

			case cookie:
				Cookie cookie = context.getCookie(param.getName());
				return cookie == null ? null : cookie.getValue();

			case form:
				return context.request().getFormAttribute(param.getName());

			case matrix:
				return getMatrixParam(context.request(), param.getName());

			case header:
				return context.request().getHeader(param.getName());

			case body:
				return context.getBodyAsString();

			default:
				return null;
		}
	}

	private static ValueReader getValueReader(MethodParameter parameter, RouteDefinition definition, ReaderFactory readers) throws ClassFactoryException {

		// get associated reader set in parameter
		if (parameter.isBody()) {
			return readers.get(parameter, definition.getReader(), definition.getConsumes());
		}
		else {
			return readers.get(parameter, definition.getReader());
		}

	}

	/**
	 * Provides vertx context of desired type if possible
	 *
	 * @param definition   route definition
	 * @param type         context type
	 * @param defaultValue default value if given
	 * @param context      to extract value from
	 * @return found context or null if not found
	 */
	private static Object provideContext(RouteDefinition definition, Class<?> type, String defaultValue, RoutingContext context)
	throws ContextException {

		if (type == null) {
			return null;
		}

		// vert.x context
		if (type.isAssignableFrom(HttpServerResponse.class)) {
			return context.response();
		}

		if (type.isAssignableFrom(HttpServerRequest.class)) {
			return context.request();
		}

		if (type.isAssignableFrom(RoutingContext.class)) {
			return context;
		}

		if (type.isAssignableFrom(Vertx.class)) {
			return context.vertx();
		}

		if (type.isAssignableFrom(User.class)) {
			return context.user();
		}

		// internal context
		if (type.isAssignableFrom(RouteDefinition.class)) {
			return definition;
		}

		// browse through context storage
		if (context.data() != null && context.data().size() > 0) {

			Object item = context.data().get(getContextKey(type));
			if (item != null) { // found in storage ... return
				return item;
			}
		}

		if (defaultValue != null) {
			// check if type has constructor that can be used with defaultValue ...
			// and create Context type on the fly constructed with defaultValue
			try {
				return ClassFactory.constructType(type, defaultValue);
			} catch (ClassFactoryException e) {
				throw new ContextException("Can't provide @Context of type: " + type + ". " + e.getMessage());
			}
		}

		// Given Context can not be resolved ... throw exception
		throw new ContextException("Can't provide @Context of type: " + type);
	}

	private static String getParam(HttpServerRequest request, int index) {

		String param = request.getParam("param" + index);
		if (param == null) { // failed to get directly ... try from request path

			String[] items = request.path().split("/");
			if (index < items.length) { // simplistic way to find param value from path by index
				return items[index];
			}
		}

		return null;
	}

	/**
	 * Removes matrix params from path
	 * @param path to clean up
	 * @param definition to check if matrix params are present
	 * @return cleaned up path
	 */
	private static String removeMatrixFromPath(String path, RouteDefinition definition) {

		// simple removal ... we don't care what matrix attributes were given
		if (definition.hasMatrixParams()) {
			int index = path.indexOf(";");
			if (index > 0) {
				return path.substring(0, index);
			}
		}

		return path;
	}

	private static String getMatrixParam(HttpServerRequest request, String name) {

		// get URL ... and find ;name=value pair
		String url = request.uri();
		String[] items = url.split(";");
		for (String item: items) {
			String[] nameValue = item.split("=");
			if (nameValue.length == 2 && nameValue[0].equals(name)) {
				return nameValue[1];
			}
		}

		return null;
	}

	public static String getContextKey(Object object) {

		Assert.notNull(object, "Expected object but got null!");
		return getContextKey(object.getClass());
	}

	private static String getContextKey(Class clazz) {
		Assert.notNull(clazz, "Missing class!");
		return "RestRouter-" + clazz.getName();
	}

}
