package com.zandero.rest.data;

import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.reader.ValueReader;
import com.zandero.utils.*;
import com.zandero.utils.extra.ValidatingUtils;

/**
 *
 */
public class MethodParameter {

    /**
     * Query or Path type
     */
    private ParameterType type;

    /**
     * parameter to search for in method annotations {@code @PathParam} {@code @QueryParam}
     */
    private String name;

    /**
     * index matching method argument index 0..N-1
     */
    private int index = -1;

    /**
     * Absolute index in path (if path parameter)
     */
    private int pathIndex = -1;

    /**
     * Index of argument in reg ex if not given as @PathParam()
     */
    private int regExIndex = -1;

    /**
     * type of parameter expected by method
     */
    private Class<?> dataType;

    /**
     * default value of parameter in case not given on call
     */
    private String defaultValue;

    /**
     * Return raw value (if applicable)
     */
    private boolean raw = false;

    /**
     * path is a regular expression
     */
    private String regularExpression;

    /**
     * String to type converter
     */
    private Class<? extends ValueReader> reader;

    /**
     * Request to type converter
     */
    private Class<? extends ContextProvider> contextProvider;


    public MethodParameter(ParameterType parameterType, String paramName) {

        Assert.notNull(parameterType, "Missing parameter type!");
        Assert.notNullOrEmptyTrimmed(paramName, "Missing parameter name!");

        type = parameterType;
        name = StringUtils.trim(paramName);
    }

    public MethodParameter(ParameterType parameterType, String paramName, Class<?> argumentType, int argumentIndex) {

        this(parameterType, paramName);

        Assert.isTrue(argumentIndex >= 0, "Can't set negative argument index!");
        argument(argumentType, argumentIndex);
    }

    public void argument(Class<?> argumentType, int argumentIndex) {

        Assert.isTrue(argumentIndex >= 0, "Can't set negative argument index!");
        Assert.notNull(argumentType, "Missing argument type!");

        dataType = argumentType;
        index = argumentIndex;

    }

    public void join(MethodParameter joining) {

        if (ParameterType.unknown.equals(type) &&
                !ParameterType.unknown.equals(joining.type)) {
            setType(joining.getType());
            setName(joining.name);
        }

        if (reader == null) {
            reader = joining.reader;
        }

        if (contextProvider == null) {
            contextProvider = joining.contextProvider;
        }

        if (index == -1) {
            index = joining.index;
        }

        if (pathIndex == -1) {
            pathIndex = joining.pathIndex;
        }

        if (regExIndex == -1) {
            regExIndex = joining.regExIndex;
        }

        if (regularExpression == null) {
            regularExpression = joining.regularExpression;
        }

        if (defaultValue == null) {
            defaultValue = joining.defaultValue;
        }

        if (dataType == null) {
            dataType = joining.dataType;
        }
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType value) {
        type = value;
    }

    public String getName() {

        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public int getIndex() {
        return index;
    }

    public int getRegExIndex() {
        return regExIndex;
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

    public void setRegEx(String value, int index) {
        value = StringUtils.trimToNull(value);

        if (value != null) {
            Assert.isTrue(ValidatingUtils.isRegEx(value), "Invalid regular expression: '" + value + "'!");
        }

        Assert.isTrue(index >= 0, "Can't set negative regular expression index!");

        regularExpression = value;
        regExIndex = index;
    }

    public void setRaw() {
        raw = true;
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

    public void setContextProvider(Class<? extends ContextProvider> provider) {
        contextProvider = provider;
    }

    public Class<? extends ContextProvider> getContextProvider() {
        return contextProvider;
    }

    public boolean isBody() {
        return ParameterType.body.equals(type);
    }

    public boolean isUsedAsArgument() {
        return index >= 0;
    }

    public boolean sameAs(MethodParameter additionalParam) {

        return (index >= 0 && additionalParam.index == index ||
                    StringUtils.equals(name, additionalParam.name));
    }

    public boolean isRaw() {
        return raw;
    }

    @Override
    public String toString() {
        if (ParameterType.body.equals(type)) {
            return type.description;
        }

        return type.description + "(\"" + name + "\")";
    }
}
