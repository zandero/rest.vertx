package com.zandero.rest.authentication;

import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.*;
import io.vertx.ext.web.RoutingContext;

public interface RestAuthenticationProvider extends AuthenticationProvider {

    default Future<User> authenticate(RoutingContext context) {
        return authenticate(provideCredentials(context));
    }

    Credentials provideCredentials(RoutingContext context);
}

