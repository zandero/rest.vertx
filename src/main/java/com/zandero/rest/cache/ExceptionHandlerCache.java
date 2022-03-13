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

    // NOTE
    // classType list holds list of exception handlers in the order of consideration
    // cache holds handler instances once initialized
    public static Map<Class<?>, Class<? extends ExceptionHandler<?>>> defaultHandlers;

    static {
        defaultHandlers = new LinkedHashMap<>();
        defaultHandlers.put(ConstraintException.class, ConstraintExceptionHandler.class);
        defaultHandlers.put(WebApplicationException.class, WebApplicationExceptionHandler.class);
        defaultHandlers.put(Throwable.class, GenericExceptionHandler.class);
    }

    private final ContextProviderCache contextProviderCache;

    public ExceptionHandlerCache(ContextProviderCache contextProviderCache) {

        // register handlers from specific to general ...
        // when searching we go over handlers ... first match is returned
        this.contextProviderCache = contextProviderCache;
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

            Assert.isFalse(contextProviderCache.hasContext(handler.getClass()),
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
}
