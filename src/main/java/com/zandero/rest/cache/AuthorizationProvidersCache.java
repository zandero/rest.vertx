package com.zandero.rest.cache;

import io.vertx.ext.auth.authorization.AuthorizationProvider;

public class AuthorizationProvidersCache extends ClassCache<AuthorizationProvider> {

    public AuthorizationProvidersCache() {
        clear();
    }

    public void register(Class<?> aClass, Class<? extends AuthorizationProvider> clazz) {
        super.registerAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, AuthorizationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
