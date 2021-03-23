package com.zandero.rest.cache;

import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.provisioning.ClassProducer;
import com.zandero.utils.Assert;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Type;
import java.util.*;

import static com.zandero.rest.provisioning.ClassUtils.*;

/**
 *
 */
public class ExceptionHandlerCache extends ClassCache<ExceptionHandler> {

    private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerCache.class);

    // NOTE
    // classType list holds list of exception handlers and order how they are considered
    // cache holds handler instances once initialized

    static Map<Class<?>, Class<? extends ExceptionHandler<?>>> defaultHandlers;

    static {
        defaultHandlers = new LinkedHashMap<>();
        defaultHandlers.put(ConstraintException.class, ConstraintExceptionHandler.class);
        defaultHandlers.put(WebApplicationException.class, WebApplicationExceptionHandler.class);
        defaultHandlers.put(Throwable.class, GenericExceptionHandler.class);
    }

    public ExceptionHandlerCache() {

        // register handlers from specific to general ...
        // when searching we go over handlers ... first match is returned
        clear();
    }

    @SafeVarargs
    public final void register(Class<? extends ExceptionHandler>... handlers) {

        Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");

        for (Class<? extends ExceptionHandler> handler : handlers) {

            Type type = getGenericType(handler);
            Assert.notNull(type, "Can't extract generic class type for exception handler: " + handler.getClass().getName());

            checkIfAlreadyRegistered((Class) type);

            // cache instance by handler class type
            super.registerAssociatedType((Class<?>) type, handler);
        }
    }

    public final void register(ExceptionHandler... handlers) {

        Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");
        for (ExceptionHandler handler : handlers) {

            Assert.isFalse(ContextProviderCache.hasContext(handler.getClass()),
                           "Exception handler utilizing @Context must be registered as class type not as instance!");

            Type generic = getGenericType(handler.getClass());
            Assert.notNull(generic, "Can't extract generic class type for exception handler: " + handler.getClass().getName());

            // check if already registered
            checkIfAlreadyRegistered((Class) generic);

            // cache instance by handler class type
            super.registerInstanceByAssociatedType((Class) generic, handler);
        }
    }

    private void checkIfAlreadyRegistered(Class<?> clazz) {

        // check if already registered
        Class<? extends ExceptionHandler> found = associatedTypeMap.get(clazz);
        if (found != null) {
            throw new IllegalArgumentException("Exception handler for: " + clazz.getName() + " already registered with: " + found.getName());
        }
    }

    public ExceptionHandler getExceptionHandler(Class<? extends Throwable> aClass,
                                                Class<? extends ExceptionHandler>[] definitionExHandlers,
                                                InjectionProvider provider,
                                                RoutingContext context) throws ClassFactoryException, ContextException {

        // trickle down ... from definition to default handler
        // search definition add as given in REST (class or method annotation)
        if (definitionExHandlers != null && definitionExHandlers.length > 0) {

            for (Class<? extends ExceptionHandler> handler : definitionExHandlers) {

                Type type = getGenericType(handler);
                if (checkIfCompatibleType(aClass, type)) {
                    log.info("Found matching exception handler: " + handler.getName());
                    return (ExceptionHandler) ClassProducer.getClassInstance(handler, this, provider, context);
                }
            }
        }

        ExceptionHandler<?> cached = getInstanceByAssociatedType(aClass);
        if (cached != null) {
            log.trace("Returning cached exception handler: " + cached.getClass().getName());
            return cached;
        }

        Class<? extends ExceptionHandler> found = getAssociatedType(aClass);
        if (found != null) {
            log.info("Found matching exception handler: " + found.getName());
            return (ExceptionHandler) ClassProducer.getClassInstance(found, this, provider, context);
        }

        for (Class<? extends ExceptionHandler> handler : defaultHandlers.values()) {
            Type type = getGenericType(handler);
            if (checkIfCompatibleType(aClass, type)) {
                log.info("Found matching exception handler: " + handler.getName());
                return (ExceptionHandler) ClassProducer.getClassInstance(handler, this, provider, context);
            }
        }

        // create class instance
        log.info("Resolving to generic exception handler: " + GenericExceptionHandler.class.getName());
        return (ExceptionHandler) ClassProducer.getClassInstance(GenericExceptionHandler.class, this, provider, context);
    }
}
