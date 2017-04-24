package com.zandero.rest.data;

import com.zandero.rest.reader.GenericBodyReader;
import com.zandero.rest.reader.HttpRequestBodyReader;
import com.zandero.utils.Assert;
import com.zandero.utils.UrlUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
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
		Assert.notNull(bodyReader, "Missing request body reader!");

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
							args[parameter.getIndex()] = bodyReader.read(value, methodArguments[parameter.getIndex()]);
							break;

						case context:
							args[parameter.getIndex()] = provideContext(method.getParameterTypes()[parameter.getIndex()], context);
							break;

						default:
							args[parameter.getIndex()] = GenericBodyReader.stringToPrimitiveType(value, dataType);
							break;
					}

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

	private static String getValue(RouteDefinition definition, MethodParameter parameter, RoutingContext context) {

		switch (parameter.getType()) {
			case path:

				if (definition.pathIsRegEx()) { // RegEx is special, params values are given by index
					return getParam(context.request(), parameter.getPathIndex());
				}

				return context.request().getParam(parameter.getName());

			case query:
				Map<String, String> query = UrlUtils.getQuery(context.request().query());
				return query.get(parameter.getName());

			case form:
				return context.request().getFormAttribute(parameter.getName());

			case header:
				return context.request().getHeader(parameter.getName());

			case body:
				return context.getBodyAsString();

			default:
				return null;
		}
	}

	/**
	 * Provides vertx context of desired type if possible
	 *
	 * @param type    context type
	 * @param context to extract value from
	 * @return found context or null if not found
	 */
	private static Object provideContext(Class<?> type, RoutingContext context) {

		if (type == null) {
			return null;
		}

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


		// TODO: add possibility to register some custom context object to be then provided as method parameter

		return null;
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
}
