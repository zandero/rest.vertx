package com.zandero.rest.exception;

import com.zandero.rest.context.ContextProviderFactory;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.utils.Assert;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerFactory.class);

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

	public ExceptionHandler getExceptionHandler(Class<? extends Throwable> aClass,
	                                            Class<? extends ExceptionHandler>[] handlers,
	                                            InjectionProvider provider,
	                                            RoutingContext context) throws ClassFactoryException, ContextException {

		// trickle down ... from definition to default handler
		Class<? extends ExceptionHandler> found = null;

		// add as given in REST (class or method annotation)
		if (handlers != null && handlers.length > 0) {

			for (Class<? extends ExceptionHandler> handler: handlers) {

				Type type = getGenericType(handler);
				if (checkIfCompatibleTypes(aClass, type)) {
					found = handler;
					log.info("Found matching exception handler: " + found.getName());
					break;
				}
			}
		}

		// get handler instance by exception type
		if (found == null) {
			ExceptionHandler handler = getCached(aClass.getName());
			if (handler != null) {
				log.info("Found matching exception handler: " + handler.getClass().getName());
				return handler;
			}
		}

		// add globally registered class type
		if (found == null && exceptionHandlers != null && exceptionHandlers.size() > 0) {

			for (Class<? extends ExceptionHandler> handler: exceptionHandlers) {

				Type type = getGenericType(handler);
				if (checkIfCompatibleTypes(aClass, type)) {
					found = handler;
					log.info("Found matching exception handler: " + handler.getClass().getName());
					break;
				}
			}
		}

		// get by exception type from classTypes list
		if (found == null) {
			found = super.get(aClass);

			if (found != null) {
				log.info("Found matching class type exception handler: " + found.getClass().getName());
			}
		}

		// nothing found provide generic
		if (found == null) {
			found = GenericExceptionHandler.class;
			log.info("Resolving to generic exception handler: " + found.getClass().getName());
		}

		// create class instance
		return super.getClassInstance(found, provider, context);
	}

	@SafeVarargs
	public final void register(Class<? extends ExceptionHandler>... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");
		exceptionHandlers.addAll(Arrays.asList(handlers));
	}

	public final void register(ExceptionHandler... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");
		for (ExceptionHandler handler: handlers) {

			Assert.isFalse(ContextProviderFactory.hasContext(handler.getClass()),
			               "Exception handler utilizing @Context must be registered as class type not as instance!");

			Type generic = getGenericType(handler.getClass());
			Assert.notNull(generic, "Can't extract generic class type for exception handler: " + handler.getClass().getName());
			super.register((Class)generic, handler);
		}
	}
}
