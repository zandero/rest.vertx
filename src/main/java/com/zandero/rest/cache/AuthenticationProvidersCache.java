package com.zandero.rest.cache;

import com.zandero.rest.authentication.RestAuthenticationProvider;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.provisioning.ClassProducer;
import io.vertx.ext.web.RoutingContext;

public class AuthenticationProvidersCache extends ClassCache<RestAuthenticationProvider> {

    public AuthenticationProvidersCache() {
        clear();
    }

    public RestAuthenticationProvider provide(Class<? extends RestAuthenticationProvider> authenticationProvider,
                                          InjectionProvider provider,
                                          RoutingContext context) throws ClassFactoryException, ContextException {
        return (RestAuthenticationProvider) ClassProducer.getClassInstance(authenticationProvider,
                                                                           this,
                                                                           provider,
                                                                           context);
    }

    public void register(Class<?> aClass, Class<? extends RestAuthenticationProvider> clazz) {
        super.registerAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, RestAuthenticationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
