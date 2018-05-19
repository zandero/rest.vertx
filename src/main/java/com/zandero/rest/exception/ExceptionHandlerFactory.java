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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class ExceptionHandlerFactory extends ClassFactory<ExceptionHandler> {

	private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerFactory.class);

	// NOTE
	// classType list holds list of exception handlers and order how they are considered
	// cache holds handler instances once initialized

	static Map<Class, Class<? extends ExceptionHandler>> defaultHandlers;
	{
		defaultHandlers = new LinkedHashMap<>();
		defaultHandlers.put(WebApplicationException.class, WebApplicationExceptionHandler.class);
		defaultHandlers.put(Throwable.class, GenericExceptionHandler.class);
	}

	@Override
	protected void init() {

		// register handlers from specific to general ...
		// when searching we go over handlers ... first match is returned
		classTypes = new LinkedHashMap<>();
	}

	public ExceptionHandler getExceptionHandler(Class<? extends Throwable> aClass,
	                                            Class<? extends ExceptionHandler>[] definitionExHandlers,
	                                            InjectionProvider provider,
	                                            RoutingContext context) throws ClassFactoryException, ContextException {

		// trickle down ... from definition to default handler
		Class<? extends ExceptionHandler> found = null;

		// search definition add as given in REST (class or method annotation)
		if (definitionExHandlers != null && definitionExHandlers.length > 0) {

			for (Class<? extends ExceptionHandler> handler: definitionExHandlers) {

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

		// get by exception type from classTypes list
		if (found == null) {
			found = super.get(aClass);

			if (found != null) {
				log.info("Found matching class type exception handler: " + found.getName());
			}
		}

		// nothing found provide default or generic
		if (found == null) {

			found = defaultHandlers.get(aClass);
			if (found == null) {
				found = GenericExceptionHandler.class;
			}
			log.info("Resolving to generic exception handler: " + found.getName());
		}

		// create class instance
		return super.getClassInstance(found, provider, context);
	}

	@SafeVarargs
	public final void register(Class<? extends ExceptionHandler>... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");

		for (Class<? extends ExceptionHandler> handler: handlers) {

			Type type = getGenericType(handler);
			classTypes.put((Class)type, handler);
		}
	}

	public final void register(ExceptionHandler... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");
		for (ExceptionHandler handler: handlers) {

			Assert.isFalse(ContextProviderFactory.hasContext(handler.getClass()),
			               "Exception handler utilizing @Context must be registered as class type not as instance!");

			Type generic = getGenericType(handler.getClass());
			Assert.notNull(generic, "Can't extract generic class type for exception handler: " + handler.getClass().getName());

			// register
			classTypes.put((Class)generic, handler.getClass());

			// cache instance
			super.register((Class)generic, handler);
		}
	}
}
