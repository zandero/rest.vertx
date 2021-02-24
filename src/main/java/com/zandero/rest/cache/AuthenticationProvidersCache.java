package com.zandero.rest.cache;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

public class AuthenticationProvidersCache extends ClassCache<AuthenticationProvider> {

    private final static Logger log = LoggerFactory.getLogger(AuthenticationProvidersCache.class);

    public AuthenticationProvidersCache() {
        clear();
    }

    public AuthenticationProvider provide(Class<? extends AuthenticationProvider> authenticationProvider,
                                          InjectionProvider provider,
                                          RoutingContext context) throws ClassFactoryException, ContextException {

        // create class instance
        return (AuthenticationProvider) ClassFactory.getClassInstance(authenticationProvider,
                                                                      this,
                                                                      provider,
                                                                      context);
    }

    public void register(Class<?> aClass, Class<? extends AuthenticationProvider> clazz) {
        super.registerTypeByAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, AuthenticationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
