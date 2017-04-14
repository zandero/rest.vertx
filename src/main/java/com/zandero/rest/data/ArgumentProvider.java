package com.zandero.rest.data;

import com.zandero.utils.Assert;
import com.zandero.utils.JsonUtils;
import com.zandero.utils.UrlUtils;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class ArgumentProvider {

	// TODO: move out of here ...
	public static Object[] getArguments(RouteDefinition definition, RoutingContext context) {

		// get parameters and extract from request their values
		List<MethodParameter> params = definition.getParameters(); // returned sorted by index

		Map<String, String> query = UrlUtils.getQuery(context.request().query());

		Object[] args = new Object[params.size()];

		for (MethodParameter parameter : params) {
			// get values
			String value = null;
			switch (parameter.getType()) {
				case path:
					value = context.request().getParam(parameter.getName());
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
					// TODO: depends on the context type given ... being request, response, ...

					break;
			}

			if (parameter.getIndex() > args.length) {
				// TODO: throw exception
			}

			args[parameter.getIndex()] = convert(parameter.getDataType(), value, parameter.getDefaultValue());
		}

		return args;
	}

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

		try {

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
				if (value.length() != 0) {
					return value.charAt(0);
				}
				// ToDo throw
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
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Invalid argument type provided, expected: " + dataType.getName() + " but got: " + value.getClass().getName() + ": " + value);
		}

		return null;
	}
}
