package com.zandero.rest.data;

import com.zandero.rest.reader.ValueReader;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;
import com.zandero.utils.extra.ValidatingUtils;

/**
 *
 */
public class MethodParameter {

	/**
	 * Query or Path type
	 */
	private final ParameterType type;

	/**
	 * parameter to search for in method annotations {@code @PathParam} {@code @QueryParam}
	 */
	private final String name;

	/**
	 * index matching method argument index
	 */
	private int index;

	/**
	 * Absolute index in path (if path parameter)
	 */
	private int pathIndex = -1;

	/**
	 * type of parameter expected by method
	 */
	private Class<?> dataType;

	/**
	 * default value of parameter in case not given on call
	 */
	private String defaultValue;

	/**
	 * path is a regular expression
	 */
	private String regularExpression;

	/**
	 * String to type converter
	 */
	private Class<? extends ValueReader> reader;


	public MethodParameter(ParameterType parameterType, String paramName) {

		Assert.notNull(parameterType, "Missing parameter type!");
		Assert.notNullOrEmptyTrimmed(paramName, "Missing parameter name!");

		type = parameterType;
		name = StringUtils.trim(paramName);
	}

	public MethodParameter(ParameterType parameterType, String paramName, int argumentIndex) {

		this(parameterType, paramName);
		index = argumentIndex;
	}

	public MethodParameter(ParameterType parameterType, String paramName, Class<?> argumentType, int argumentIndex) {

		this(parameterType, paramName);

		Assert.isTrue(argumentIndex >= 0, "Can't set negative argument index!");
		argument(argumentType, argumentIndex);
	}

	public MethodParameter argument(Class<?> argumentType) {

		Assert.notNull(argumentType, "Missing argument type!");

		dataType = argumentType;
		return this;
	}

	public MethodParameter argument(Class<?> argumentType, int argumentIndex) {

		Assert.isTrue(argumentIndex >= 0, "Can't set negative argument index!");
		Assert.notNull(argumentType, "Missing argument type!");

		dataType = argumentType;
		index = argumentIndex;

		return this;
	}

	public ParameterType getType() {

		return type;
	}

	public String getName() {

		return name;
	}

	public int getIndex() {

		return index;
	}

	public Class<?> getDataType() {

		return dataType;
	}

	public String getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(String value) {

		defaultValue = StringUtils.trimToNull(value);
	}

	public String getRegEx() {

		return regularExpression;
	}

	public void setRegEx(String value) {

		value = StringUtils.trimToNull(value);

		if (value != null) {
			Assert.isTrue(ValidatingUtils.isRegEx(value), "Invalid regular expression: '" + value + "'!");
		}

		regularExpression = value;
	}

	public boolean isRegEx() {

		return regularExpression != null;
	}

	public void setPathIndex(int value) {

		pathIndex = value;
	}

	public int getPathIndex() {

		return pathIndex;
	}

	public void setValueReader(Class<? extends ValueReader> valueReader) {

		reader = valueReader;
	}

	public Class<? extends ValueReader> getReader() {

		return reader;
	}

	@Override
	public String toString() {

		if (ParameterType.body.equals(type)) {
			return type.getDescription();
		}

		return type.getDescription() + "(\"" + name + "\")";
	}
}
