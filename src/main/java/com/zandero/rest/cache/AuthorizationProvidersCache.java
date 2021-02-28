package com.zandero.rest.cache;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

public class AuthorizationProvidersCache extends ClassCache<AuthorizationProvider> {

    public AuthorizationProvidersCache() {
        clear();
    }

    public AuthorizationProvider provide(Class<? extends AuthorizationProvider> authorizationProvider,
                                         InjectionProvider provider,
                                         RoutingContext context) throws ClassFactoryException, ContextException {
        return (AuthorizationProvider) ClassFactory.getClassInstance(authorizationProvider,
                                                                     this,
                                                                     provider,
                                                                     context);
    }

    public void register(Class<?> aClass, Class<? extends AuthorizationProvider> clazz) {
        super.registerTypeByAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, AuthorizationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
