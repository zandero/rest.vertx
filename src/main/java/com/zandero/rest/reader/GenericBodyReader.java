package com.zandero.rest.reader;

import com.zandero.utils.Assert;

/**
 *
 */
public class GenericBodyReader implements HttpRequestBodyReader {

	@Override
	public Object read(String value, Class<?> type) {

		if (type.isPrimitive()) {
			Object out = stringToPrimitiveType(value, type);
			return out == null ? value : out;
		}

		return value;
	}

	public static Object stringToPrimitiveType(String value, Class<?> dataType) {

		if (value == null) {
			return null;
		}

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

			Assert.isTrue(value.length() != 0, "Expected Character but got: null");
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
