package com.zandero.rest.exception;

import com.zandero.rest.data.ClassFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Type;
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

	public ExceptionHandler getFailureHandler(Class<? extends ExceptionHandler>[] handlers,
	                                          Class<? extends ExceptionHandler> defaultHandler,
	                                          Class<? extends Throwable> aClass) {

		// trickle down ... from definition to default handler
		Class<? extends ExceptionHandler> found = null;

		if (handlers == null || handlers.length == 0) {
			found = defaultHandler;
		}
		else {

			for (Class<? extends ExceptionHandler> handler: handlers) {

				Type type = getGenericType(handler);
				if (checkIfCompatibleTypes(aClass, type)) {
					found = handler;
					break;
				}
			}
		}

		if (found == null) {
			found = get(aClass);
		}

		if (found == null) { // nothing found provide generic
			found = GenericExceptionHandler.class;
		}

		try {
			return super.getClassInstance(found);

		} catch (ClassFactoryException ex) {

			log.error(ex.getMessage());
			return getFailureHandler(null, GenericExceptionHandler.class, ex.getCause().getClass());
		}
	}
}
