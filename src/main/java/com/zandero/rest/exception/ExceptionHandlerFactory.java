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

	public ExceptionHandler getFailureHandler(Class<? extends ExceptionHandler> handler, ExceptionHandler defaultHandler) {

		// get and cache
		if (handler == null) {
			return getDefaultHandler(defaultHandler);
		}

		try {
			return super.getClassInstance(handler);
		} catch (ClassFactoryException e) {

			log.error(e.getMessage());
			return getDefaultHandler(defaultHandler);
		}
	}

	private ExceptionHandler getDefaultHandler(ExceptionHandler defaultHandler) {

		if (defaultHandler == null) {
			return new GenericExceptionHandler();
		}

		return defaultHandler;
	}
}
