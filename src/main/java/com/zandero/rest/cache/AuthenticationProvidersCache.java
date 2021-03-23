package com.zandero.rest.cache;

import com.zandero.rest.authentication.RestAuthenticationProvider;

public class AuthenticationProvidersCache extends ClassCache<RestAuthenticationProvider> {

    public AuthenticationProvidersCache() {
        clear();
    }

    public void register(Class<?> aClass, Class<? extends RestAuthenticationProvider> clazz) {
        super.registerAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, RestAuthenticationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
