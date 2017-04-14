package com.zandero.rest.data;

import com.zandero.utils.Assert;
import com.zandero.utils.JsonUtils;
import com.zandero.utils.UrlUtils;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.InvocationTargetException;
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

				case context:
					// TODO: depends on the context type given ... being request, response, ...

					break;
			}

			if (parameter.getIndex() > args.length) {
				// TODO: throw exception
			}

			args[parameter.getIndex()] = convert(parameter.getDataType(), value, definition.getDefaultValue());
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


	/*public Object extractValue(Class<?> dataType, String value)
	{
		Assert.notNull(value, "Can't extract value from null!");

		if (paramConverter != null)
		{
			return paramConverter.fromString(strVal);
		}
		if (converter != null)
		{
			return converter.fromString(strVal);
		}
		else if (unmarshaller != null)
		{
			return unmarshaller.fromString(strVal);
		}
		else if (delegate != null)
		{
			return delegate.fromString(strVal);
		}
		else if (constructor != null)
		{
			try
			{
				return constructor.newInstance(strVal);
			}
			catch (InstantiationException e)
			{
				throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), e);
			}
			catch (IllegalAccessException e)
			{
				throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), e);
			}
			catch (InvocationTargetException e)
			{
				Throwable targetException = e.getTargetException();
				if (targetException instanceof WebApplicationException)
				{
					throw ((WebApplicationException)targetException);
				}
				throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), targetException);
			}
		}
		else if (valueOf != null)
		{
			try
			{
				return valueOf.invoke(null, strVal);
			}
			catch (IllegalAccessException e)
			{
				throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), e);
			}
			catch (InvocationTargetException e)
			{
				Throwable targetException = e.getTargetException();
				if (targetException instanceof WebApplicationException)
				{
					throw ((WebApplicationException)targetException);
				}
				throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), targetException);
			}
		}

		return null;
	}*/

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

		return null;
	}
}
