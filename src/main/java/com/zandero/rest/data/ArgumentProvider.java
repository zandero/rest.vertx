package com.zandero.rest.data;

import com.zandero.rest.bean.BeanProvider;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.context.ContextProviderFactory;
import com.zandero.rest.exception.ContextException;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.reader.ReaderFactory;
import com.zandero.rest.reader.ValueReader;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;
import com.zandero.utils.extra.UrlUtils;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * Extracts arguments to be provided for given method from definition and current context (request)
 */
public class ArgumentProvider {

	private final static Logger log = LoggerFactory.getLogger(ArgumentProvider.class);

	@SuppressWarnings("unchecked")
	public static Object[] getArguments(Method method,
	                                    RouteDefinition definition,
	                                    RoutingContext context,
	                                    ReaderFactory readers,
	                                    ContextProviderFactory providerFactory,
	                                    InjectionProvider injectionProvider,
										BeanProvider beanProvider) throws Throwable {

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

			if (!parameter.isUsedAsArgument()) {
				continue;
			}

			// set if we have a place to set it ... otherwise ignore
			if (parameter.getIndex() < args.length) {

				// get value
				String value = getValue(definition, parameter, context, parameter.getDefaultValue());

				Class<?> dataType = parameter.getDataType();
				if (dataType == null) {
					dataType = methodArguments[parameter.getIndex()];
				}

				try {
					switch (parameter.getType()) {

						case bean :

                            if (beanProvider != null) {
                                Object result = beanProvider.provide(dataType, context, injectionProvider);
                                args[parameter.getIndex()] = result;
                            }

							break;

						case context:

							// check if providers need to be called to assure context
							ContextProvider provider = providerFactory.get(dataType, parameter.getContextProvider(), injectionProvider, context, null);
							if (provider != null) {
								Object result = provider.provide(context.request());
								if (result != null) {
									context.data().put(ContextProviderFactory.getContextDataKey(dataType), result);
								}
							}

							args[parameter.getIndex()] = ContextProviderFactory.provideContext(method.getParameterTypes()[parameter.getIndex()],
							                                                                   parameter.getDefaultValue(),
							                                                                   context);
							break;

						default:

							ValueReader valueReader = getValueReader(injectionProvider, parameter, definition, context, readers);
							args[parameter.getIndex()] = valueReader.read(value, dataType);
							break;
					}
				}
				catch (Throwable e) {

					if (e instanceof ContextException) {
						log.error(e.getMessage());
						throw new IllegalArgumentException(e.getMessage());
					}

					if (e instanceof IllegalArgumentException) {

						MethodParameter paramDefinition = definition.findParameter(parameter.getIndex());
						String expectedType = method.getParameterTypes()[parameter.getIndex()].getTypeName();

						String error;
						if (paramDefinition != null) {
							error =
								"Invalid parameter type for: " + paramDefinition + " for: " + definition.getPath() + ", expected: " + expectedType;
						} else {
							error =
								"Invalid parameter type for " + (parameter.getIndex() + 1) + " argument for: " + method + " expected: " +
								expectedType;
						}

						if (value == null) {
							error = error + ", but got: null";
						}

						error = error + " -> " + e.getMessage();
						log.error(error);
					}
					else {
						log.error(e.getMessage());
					}

					throw e;
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

	public static String getValue(RouteDefinition definition, MethodParameter param, RoutingContext context, String defaultValue) {

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
				if (definition != null && definition.pathIsRegEx()) { // RegEx is special, params values are given by index
					path = getParam(context.mountPoint(), context.request(), param.getPathIndex());
				} else {
					path = context.request().getParam(param.getName());
				}

				// if @MatrixParams are present ... those need to be removed
				if (definition != null) {
					path = removeMatrixFromPath(path, definition);
				}
				return path;

			case query:
				Map<String, String> query = UrlUtils.getQuery(context.request().query());
				String value = query.get(param.getName());

				// user specified @Raw annotation ... provide as it is
				if (param.isRaw()) {
					return value;
				}

				// by default decode
				if (!StringUtils.isNullOrEmptyTrimmed(value)) {
					try {
						return URLDecoder.decode(value, "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						log.warn("Failed to decode query: " + value, e);
					}
				}

				return value;

			case cookie:
				Cookie cookie = context.request().getCookie(param.getName());
				return cookie == null ? null : cookie.getValue();

			case form:
				return context.request().getFormAttribute(param.getName());

			case matrix:
				return getMatrixParam(context.request().path(), param.getName());

			case header:
				return context.request().getHeader(param.getName());

			case body:
				return StringUtils.trimToNull(context.getBodyAsString());

			default:
				return null;
		}
	}

	private static ValueReader getValueReader(InjectionProvider provider,
	                                          MethodParameter parameter,
	                                          RouteDefinition definition,
	                                          RoutingContext context,
	                                          ReaderFactory readers) {

		// get associated reader set in parameter
		MediaType[] consumes = parameter.isBody() ? definition.getConsumes() : null;
		return readers.get(parameter, provider, context, consumes);
	}

	/**
	 * Parse param from path
	 * @param mountPoint prefix
	 * @param request http request
	 * @param index param index
	 * @return found param or null if none found
	 */
	private static String getParam(String mountPoint, HttpServerRequest request, int index) {

		String param = request.getParam("param" + index); // default mount of params without name param0, param1 ...
		if (param == null) { // failed to get directly ... try from request path

			String path = removeMountPoint(mountPoint, request.path());

			String[] items = path.split("/");
			if (index >= 0 && index < items.length) { // simplistic way to find param value from path by index
				return items[index];
			}
		}

		return null;
	}

	/**
	 * Removes path prefix from whole path
	 * @param mountPoint prefix
	 * @param path whole path
	 * @return left over path
	 */
	private static String removeMountPoint(String mountPoint, String path) {

		if (StringUtils.isNullOrEmptyTrimmed(mountPoint)) {
			return path;
		}

		return path.substring(mountPoint.length());
	}

	/**
	 * Removes matrix params from path
	 *
	 * @param path       to clean up
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

	/**
	 * @param path to extract matrix parameter from (URL)
	 * @param name    of desired matrix parameter
	 * @return found parameter value or null if none found
	 */
	// TODO: this might be slow at times ... pre-parse matrix into hash map ... and store
	private static String getMatrixParam(String path, String name) {

		// get URL ... and find ;name=value pair
		String[] items = path.split(";");
		for (String item : items) {
			String[] nameValue = item.split("=");
			if (nameValue.length == 2 && nameValue[0].equals(name)) {
				return nameValue[1];
			}
		}

		return null;
	}
}
