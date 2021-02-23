package com.zandero.rest.cache;

import com.zandero.rest.authorization.RoleBasedUserAuthorizationProvider;
import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

public class AuthorizationProvidersCache extends ClassCache<AuthorizationProvider> {

    private final static Logger log = LoggerFactory.getLogger(AuthorizationProvidersCache.class);

    public AuthorizationProvidersCache() {
        clear();
    }

    public AuthorizationProvider provide(Class<? extends AuthorizationProvider> authorizationProvider,
                                         InjectionProvider provider,
                                         RouteDefinition definition,
                                         RoutingContext context) throws ClassFactoryException, ContextException {

        // For back compatibility purposes only ... handling @RolesAllowed, @PermitAll, @DenyAll routes
        if (authorizationProvider == RoleBasedUserAuthorizationProvider.class) {
            return new RoleBasedUserAuthorizationProvider(definition);
        }

        // create class instance
        return (AuthorizationProvider) ClassFactory.getClassInstance(authorizationProvider, this, provider, context);
    }

    public void register(Class<?> aClass, Class<? extends AuthorizationProvider> clazz) {
        super.registerTypeByAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, AuthorizationProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }
}
