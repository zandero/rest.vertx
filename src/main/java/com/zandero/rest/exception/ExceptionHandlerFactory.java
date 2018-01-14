package com.zandero.rest.exception;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.utils.Assert;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 */
public class ExceptionHandlerFactory extends ClassFactory<ExceptionHandler> {

	/**
	 * standalone list of global handlers
 	 */
	private List<Class<? extends ExceptionHandler>> exceptionHandlers;

	@Override
	protected void init() {

		exceptionHandlers = new ArrayList<>();

		// register handlers from specific to general ...
		// when searching we go over handlers ... first match is returned
		classTypes = new LinkedHashMap<>();
		classTypes.put(WebApplicationException.class, WebApplicationExceptionHandler.class);
		classTypes.put(Throwable.class, GenericExceptionHandler.class);
	}

	public ExceptionHandler getExceptionHandler(InjectionProvider provider,
												Class<? extends ExceptionHandler>[] handlers,
	                                            Class<? extends Throwable> aClass) throws ClassFactoryException {

		// trickle down ... from definition to default handler
		Class<? extends ExceptionHandler> found = null;

		List<Class<? extends ExceptionHandler>> joined = new ArrayList<>();
		// add as given in REST (class or method annotation)
		if (handlers != null && handlers.length > 0) {
			joined.addAll(Arrays.asList(handlers));
		}

		// add globally registered
		if (exceptionHandlers != null && exceptionHandlers.size() > 0) {
			joined.addAll(exceptionHandlers);
		}

		// go over list and try to find matching handler
		if (joined.size() > 0) {

			for (Class<? extends ExceptionHandler> handler: joined) {

				Type type = getGenericType(handler);
				if (checkIfCompatibleTypes(aClass, type)) {
					found = handler;
					break;
				}
			}
		}

		// get by exception type from classTypes list
		if (found == null) {
			found = super.get(aClass);
		}

		// nothing found provide generic
		if (found == null) {
			found = GenericExceptionHandler.class;
		}

		return super.getClassInstance(provider, found);
	}

	@SafeVarargs
	public final void register(Class<? extends ExceptionHandler>... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");
		exceptionHandlers.addAll(Arrays.asList(handlers));
	}

	public final void register(ExceptionHandler... handlers) {

	}
}
