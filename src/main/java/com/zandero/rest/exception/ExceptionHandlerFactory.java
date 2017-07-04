package com.zandero.rest.exception;

import com.zandero.rest.data.ClassFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class ExceptionHandlerFactory extends ClassFactory<ExceptionHandler> {

	private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerFactory.class);

	private Map<Class<? extends Throwable>, Class<? extends ExceptionHandler>> exceptionTypes;

	@Override
	protected void init() {

		exceptionTypes = new LinkedHashMap<>();
		exceptionTypes.put(IllegalArgumentException.class, GenericExceptionHandler.class);
		exceptionTypes.put(WebApplicationException.class, WebApplicationExceptionHandler.class);
	}

	public Class<? extends ExceptionHandler> get(Class<?> type) {

		for (Class<? extends Throwable> clazz: exceptionTypes.keySet()) {

			if (clazz.isAssignableFrom(type)) {
				return exceptionTypes.get(clazz);
			}
		}

		return null;
	}

	public ExceptionHandler getFailureHandler(Class<? extends ExceptionHandler> handler, Class<? extends ExceptionHandler> defaultHandler) {

		// get and cache
		if (handler == null) {
			return getFailureHandler(defaultHandler, GenericExceptionHandler.class);
		}

		try {
			return super.getClassInstance(handler);
		} catch (ClassFactoryException e) {

			log.error(e.getMessage());
			return getFailureHandler(defaultHandler, GenericExceptionHandler.class);
		}
	}
}
