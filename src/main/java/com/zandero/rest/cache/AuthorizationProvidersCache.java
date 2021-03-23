package com.zandero.rest.cache;

import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.provisioning.ClassProducer;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;

public class AuthorizationProvidersCache extends ClassCache<AuthorizationProvider> {

    public AuthorizationProvidersCache() {
        clear();
    }

    public AuthorizationProvider provide(Class<? extends AuthorizationProvider> authorizationProvider,
                                         InjectionProvider provider,
                                         RoutingContext context) throws ClassFactoryException, ContextException {
        return (AuthorizationProvider) ClassProducer.getClassInstance(authorizationProvider,
                                                                      this,
                                                                      provider,
                                                                      context);
    }

    public void register(Class<?> aClass, Class<? extends AuthorizationProvider> clazz) {
        super.registerAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, AuthorizationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
