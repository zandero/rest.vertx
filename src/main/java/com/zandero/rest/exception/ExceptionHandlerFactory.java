package com.zandero.rest.exception;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.utils.ArrayUtils;
import com.zandero.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Type;
import java.util.*;

/**
 *
 */
public class ExceptionHandlerFactory extends ClassFactory<ExceptionHandler> {

	private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerFactory.class);

	private Map<Class<? extends Throwable>, Class<? extends ExceptionHandler>> exceptionTypes;
	private List<Class<? extends ExceptionHandler>> exceptionHandlers = new ArrayList<>();

	@Override
	protected void init() {

		exceptionHandlers = new ArrayList<>();

		// register handlers from specific to general ...
		// when searching we go over handlers ... first match is returned
		exceptionTypes = new LinkedHashMap<>();
		exceptionTypes.put(WebApplicationException.class, WebApplicationExceptionHandler.class);
		exceptionTypes.put(Throwable.class, GenericExceptionHandler.class);
	}

	public Class<? extends ExceptionHandler> get(Class<?> type) {

		for (Class<? extends Throwable> clazz: exceptionTypes.keySet()) {

			if (clazz.isAssignableFrom(type)) {
				return exceptionTypes.get(clazz);
			}
		}

		return null;
	}

	public ExceptionHandler getExceptionHandler(Class<? extends ExceptionHandler>[] handlers,
	                                            Class<? extends Throwable> aClass) {

		// trickle down ... from definition to default handler
		Class<? extends ExceptionHandler> found = null;

		List<Class<? extends ExceptionHandler>> joined = new ArrayList<>();
		// as given in REST (class or method annotation)
		if (handlers != null && handlers.length > 0) {
			joined.addAll(Arrays.asList(handlers));
		}

		// as globally registered
		if (exceptionHandlers != null && exceptionHandlers.size() > 0) {
			joined.addAll(exceptionHandlers);
		}

		if (joined.size() > 0) {

			for (Class<? extends ExceptionHandler> handler: joined) {

				Type type = getGenericType(handler);
				if (checkIfCompatibleTypes(aClass, type)) {
					found = handler;
					break;
				}
			}
		}

		// get by exception type from exceptionTypes list
		if (found == null) {
			found = get(aClass);
		}

		// nothing found provide generic
		if (found == null) {
			found = GenericExceptionHandler.class;
		}

		try {
			return super.getClassInstance(found);

		} catch (ClassFactoryException ex) {

			log.error(ex.getMessage());
			return getExceptionHandler(ArrayUtils.join(null, GenericExceptionHandler.class), ex.getCause().getClass());
		}
	}

	@SafeVarargs
	public final void register(Class<? extends ExceptionHandler>... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");
		exceptionHandlers.addAll(Arrays.asList(handlers));
	}
}
