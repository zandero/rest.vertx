package com.zandero.rest.data;

import com.zandero.rest.exception.ContextException;
import com.zandero.rest.reader.GenericBodyReader;
import com.zandero.rest.reader.HttpRequestBodyReader;
import com.zandero.utils.Assert;
import com.zandero.utils.UrlUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * Extracts arguments to be provided for given method from definition and current context (request)
 */
public class ArgumentProvider {

	public static Object[] getArguments(Method method, RouteDefinition definition, RoutingContext context, HttpRequestBodyReader bodyReader) {

		Assert.notNull(method, "Missing method to provide arguments for!");
		Assert.notNull(definition, "Missing route definition!");
		Assert.notNull(context, "Missing vert.x routing context!");

		Class<?>[] methodArguments = method.getParameterTypes();

		if (methodArguments.length == 0) {
			return null;    // no arguments needed ...
		}

		// get parameters and extract from request their values
		List<MethodParameter> params = definition.getParameters(); // returned sorted by index

		//Map<String, String> query = UrlUtils.getQuery(context.request().query());

		Object[] args = new Object[methodArguments.length];

		for (MethodParameter parameter : params) {

			// get value
			String value = getValue(definition, parameter, context);

			if (value == null) {
				value = parameter.getDefaultValue();
			}

			// set if we have a place to set it ... otherwise ignore
			if (parameter.getIndex() < args.length) {

				Class<?> dataType = parameter.getDataType();
				if (dataType == null) {
					dataType = methodArguments[parameter.getIndex()];
				}

				try {

					switch (parameter.getType()) {

						case body:
							Assert.notNull(bodyReader, "Missing request body reader!");
							args[parameter.getIndex()] = bodyReader.read(value, methodArguments[parameter.getIndex()]);
							break;

						case context:
							args[parameter.getIndex()] = provideContext(definition, method.getParameterTypes()[parameter.getIndex()], context);
							break;

						default:
							args[parameter.getIndex()] = GenericBodyReader.stringToPrimitiveType(value, dataType);
							break;
					}

				}
				catch (ContextException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
				catch (Exception e) {

					MethodParameter paramDefinition = definition.findParameter(parameter.getIndex());
					String providedType = value != null ? value.getClass().getSimpleName() : "null";
					String expectedType = method.getParameterTypes()[parameter.getIndex()].getTypeName();

					if (paramDefinition != null) {
						throw new IllegalArgumentException(
							"Invalid parameter type for: " + paramDefinition + " for: " + definition.getPath() + ", expected: " + expectedType + ", but got: " + providedType);
					}

					throw new IllegalArgumentException(
						"Invalid parameter type for " + (parameter.getIndex() + 1) + " argument for: " + method + " expected: " + expectedType + ", but got: " + providedType);
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

				throw new IllegalArgumentException("Missing " + (index + 1) + " argument for: " + method + " expected: " + param.getType() + ", but: null was provided!");
			}
		}

		return args;
	}

	private static String getValue(RouteDefinition definition, MethodParameter param, RoutingContext context) {

		switch (param.getType()) {
			case path:

				if (definition.pathIsRegEx()) { // RegEx is special, params values are given by index
					return getParam(context.request(), param.getPathIndex());
				}

				return context.request().getParam(param.getName());

			case query:
				Map<String, String> query = UrlUtils.getQuery(context.request().query());
				return query.get(param.getName());

			case cookie:
				Cookie cookie = context.getCookie(param.getName());
				return cookie == null ? null : cookie.getValue();

			case form:
				String formParam = context.request().getFormAttribute(param.getName());
				if (formParam == null) { // retry ... with params
					formParam = context.request().getParam(param.getName());
				}
				return formParam;

			case header:
				return context.request().getHeader(param.getName());

			case body:
				return context.getBodyAsString();

			default:
				return null;
		}
	}

	/**
	 * Provides vertx context of desired type if possible
	 *
	 * @param definition     route definition
	 * @param type           context type
	 * @param context        to extract value from
	 * @return found context or null if not found
	 */
	private static Object provideContext(RouteDefinition definition, Class<?> type, RoutingContext context) throws ContextException {

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
		if (context.data() != null) {
			for (Object item : context.data().values()) {
				if (type.isInstance(item)) {
					return item;
				}
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

	public static String getContextKey(Object object) {

		Assert.notNull(object, "Expected object but got null!");
		return "RestRouter-" + Integer.toString(object.hashCode());
	}
}
