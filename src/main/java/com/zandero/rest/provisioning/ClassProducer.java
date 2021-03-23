package com.zandero.rest.provisioning;

import com.zandero.rest.annotation.NoCache;
import com.zandero.rest.cache.*;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;


public class ClassProducer {

    private final static Logger log = LoggerFactory.getLogger(ClassProducer.class);

    /**
     * 1. Checks cache if class instance is already present
     * 2. Utilizes ClassFactory to create a new instance
     * 3. Stores produced class instance in cache
     *
     * @param clazz type desired
     * @param classCache to check against if instance already present
     * @param provider injection provider
     * @param context routing context
     * @return created class instance
     * @throws ClassFactoryException
     * @throws ContextException
     */
    @SuppressWarnings("unchecked")
    public static Object getClassInstance(Class<?> clazz,
                                          ClassCache classCache,
                                          InjectionProvider provider,
                                          RoutingContext context) throws ClassFactoryException, ContextException {

        if (clazz == null) {
            log.trace("No class given!");
            return null;
        }

        Object instance = classCache.getInstanceByAssociatedType(clazz);
        if (instance != null) {
            log.trace("Found instance: " + instance.getClass().getName()  + "in cache by: " + clazz.getName());
            return instance;
        }

        instance = ClassFactory.newInstanceOf(clazz, provider, context);

        // only use cache if no @Context is needed (TODO: join this two calls into one!)
        boolean hasContext = ContextProviderCache.hasContext(clazz); // TODO: move this method somewhere else
        boolean cacheIt = clazz.getAnnotation(NoCache.class) == null; // caching disabled / enabled

        if (!hasContext && cacheIt) { // no context .. we can cache this instance
            classCache.registerInstanceByAssociatedType(clazz, instance);
        }

        return instance;
    }
}
