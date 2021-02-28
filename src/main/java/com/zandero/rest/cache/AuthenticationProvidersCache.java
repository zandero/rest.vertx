package com.zandero.rest.cache;

import com.zandero.rest.authentication.RestAuthenticationProvider;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

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
