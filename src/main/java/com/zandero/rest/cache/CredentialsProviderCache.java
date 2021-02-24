package com.zandero.rest.cache;

import com.zandero.rest.authentication.CredentialsProvider;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

public class CredentialsProviderCache extends ClassCache<CredentialsProvider> {

    private final static Logger log = LoggerFactory.getLogger(CredentialsProviderCache.class);

    public CredentialsProviderCache() {
        clear();
    }

    public CredentialsProvider provide(Class<? extends CredentialsProvider> authenticationProvider,
                                       InjectionProvider provider,
                                       RoutingContext context) throws ClassFactoryException, ContextException {

        // create class instance
        return (CredentialsProvider) ClassFactory.getClassInstance(authenticationProvider,
                                                                   this,
                                                                   provider,
                                                                   context);
    }

    public void register(Class<?> aClass, Class<? extends CredentialsProvider> clazz) {
        super.registerTypeByAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, CredentialsProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
