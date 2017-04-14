package com.zandero.rest.data;

import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;

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
	 * Type of parameter expected by method
	 */
	private Class<?> dataType;


	public MethodParameter(ParameterType parameterType, String paramName) {

		Assert.notNull(parameterType, "Missing parameter type!");
		Assert.notNullOrEmptyTrimmed(paramName, "Missing parameter name!");

		type = parameterType;
		name = StringUtils.trim(paramName);
	}

	public MethodParameter(ParameterType parameterType, String paramName, Class<?> argumentType, int argumentIndex) {

		this(parameterType, paramName);
		argument(argumentType, argumentIndex);
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
}
