package com.zandero.rest.authentication;

import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.*;
import io.vertx.ext.web.RoutingContext;

public interface RestAuthenticationProvider extends AuthenticationProvider {

    default void authenticate(RoutingContext context, Handler<AsyncResult<User>> resultHandler) {
        authenticate(provideCredentials(context), resultHandler);
    }

    Credentials provideCredentials(RoutingContext context);
}

