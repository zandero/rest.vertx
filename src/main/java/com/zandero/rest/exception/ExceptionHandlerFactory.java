package com.zandero.rest.exception;

import com.zandero.rest.data.ClassFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ExceptionHandlerFactory extends ClassFactory<ExceptionHandler> {

	private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerFactory.class);

	@Override
	protected void init() {

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
