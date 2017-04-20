package com.zandero.rest.data;

import com.zandero.utils.Assert;
import com.zandero.utils.JsonUtils;
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
 *
 */
public class ArgumentProvider {

	public static Object[] getArguments(Method method, RouteDefinition definition, RoutingContext context) {

		Assert.notNull(method, "Missing method to provide arguments for!");
		Assert.notNull(definition, "Missing route definition!");
		Assert.notNull(context, "Missing vert.x routing context!");

		Class<?>[] methodArguments = method.getParameterTypes();

		if (methodArguments.length == 0) {
			return null;    // no arguments needed ...
		}

		// get parameters and extract from request their values
		List<MethodParameter> params = definition.getParameters(); // returned sorted by index

		Map<String, String> query = UrlUtils.getQuery(context.request().query());

		Object[] args = new Object[methodArguments.length];

		for (MethodParameter parameter : params) {
			// get values
			String value = null;
			switch (parameter.getType()) {
				case path:

					if (definition.pathIsRegEx()) { // params values are given by index
						value = getParam(context.request(), parameter.getPathIndex());
					}
					else {
						value = context.request().getParam(parameter.getName());
					}
					break;

				case query:
					value = query.get(parameter.getName());
					break;

				case form:
					value = context.request().getFormAttribute(parameter.getName());
					break;

				case header:
					value = context.request().getHeader(parameter.getName());
					break;

				case body:
					value = context.getBodyAsString();
					break;

				case context:

					args[parameter.getIndex()] = provideContext(method.getParameterTypes()[parameter.getIndex()], context);
					continue;
			}

			// set if we have a place to set it ... otherwise ignore
			if (parameter.getIndex() < args.length) {

				Class<?> dataType = parameter.getDataType();
				if (dataType == null) {
					dataType = methodArguments[parameter.getIndex()];
				}

				try {
					args[parameter.getIndex()] = convert(dataType, value, parameter.getDefaultValue());
				}
				catch (Exception e) {

					MethodParameter paramDefinition = definition.findParameter(parameter.getIndex());
					String providedType = value != null ? value.getClass().getSimpleName() : "null";
					String expectedType = method.getParameterTypes()[parameter.getIndex()].getTypeName();

					if (paramDefinition != null) {
						throw new IllegalArgumentException("Invalid parameter type for: " + paramDefinition + " for: " + definition.getPath() + ", expected: " + expectedType + ", but got: " + providedType);
					}

					throw new IllegalArgumentException("Invalid parameter type for " + (parameter.getIndex() + 1) + " argument for: " + method + " expected: " + expectedType + ", but got: " + providedType);
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

		return null;
	}

	private static String getParam(HttpServerRequest request, int index) {

		String param = request.getParam("param" + index);
		if (param == null) { // failed to get directly ... try from request path

			List<MethodParameter> params = PathConverter.extract(request.path());

			String[] items = request.path().split("/");
			if (index < items.length) {
				return items[index];
			}
		}

		return null;
	}

	/**
	 * Tries to convert given String argument to specific type as expeced by the method being called
	 *
	 * @param dataType     to convert argument into
	 * @param value        argument value
	 * @param defaultValue argument default value in case not given
	 * @return transformed argument into dataType type
	 */
	static Object convert(Class<?> dataType, String value, String defaultValue) {

		if (value == null) {
			if (defaultValue == null) {
				return null;
			}

			value = defaultValue;
		}

		// Try converting to primitive type if possible
		Object converted = stringToPrimitiveType(dataType, value);
		if (converted != null) {
			return converted;
		}

		// Try converting from JSON to object
		try {
			return JsonUtils.fromJson(value, dataType);
		}
		catch (IllegalArgumentException e) {
			// TODO: What now?
		}

		return null;
	}

	static Object stringToPrimitiveType(Class<?> dataType, String value) {

		Assert.notNull(value, "Can't convert null to primitive type!");

		if (dataType.equals(String.class)) {
			return value;
		}

		// primitive types need to be cast differently
		if (dataType.isAssignableFrom(boolean.class) ||
			dataType.isAssignableFrom(Boolean.class)) {
			return Boolean.valueOf(value);
		}

		if (dataType.isAssignableFrom(byte.class) ||
			dataType.isAssignableFrom(Byte.class)) {
			return Byte.valueOf(value);
		}

		if (dataType.isAssignableFrom(char.class) ||
			dataType.isAssignableFrom(Character.class)) {

			Assert.isTrue(value.length() != 0, "Invalid!");
			return value.charAt(0);
		}

		if (dataType.isAssignableFrom(short.class) ||
			dataType.isAssignableFrom(Short.class)) {
			return Short.valueOf(value);
		}

		if (dataType.isAssignableFrom(int.class) ||
			dataType.isAssignableFrom(Integer.class)) {
			return Integer.valueOf(value);
		}

		if (dataType.isAssignableFrom(long.class) ||
			dataType.isAssignableFrom(Long.class)) {
			return Long.valueOf(value);
		}

		if (dataType.isAssignableFrom(float.class) ||
			dataType.isAssignableFrom(Float.class)) {
			return Float.valueOf(value);
		}

		if (dataType.isAssignableFrom(double.class) ||
			dataType.isAssignableFrom(Double.class)) {
			return Double.valueOf(value);
		}

		return null;
	}
}
