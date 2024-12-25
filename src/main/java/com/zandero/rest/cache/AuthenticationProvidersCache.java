package com.zandero.rest.cache;

import com.zandero.rest.authentication.*;
import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.*;
import io.vertx.ext.web.*;

public class AuthenticationProvidersCache extends ClassCache<RestAuthenticationProvider> {

    public AuthenticationProvidersCache() {
        clear();
    }

    public RestAuthenticationProvider provide(Class<? extends RestAuthenticationProvider> authenticationProvider,
                                              InjectionProvider provider,
                                              RoutingContext context) throws ClassFactoryException, ContextException {
        return (RestAuthenticationProvider) ClassFactory.getClassInstance(authenticationProvider,
                                                                          this,
                                                                          provider,
                                                                          context);
    }

    public void register(Class<?> aClass, Class<? extends RestAuthenticationProvider> clazz) {
        super.registerTypeByAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, RestAuthenticationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
