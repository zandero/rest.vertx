package com.zandero.rest.authorization;

import com.zandero.rest.authentication.RestAuthenticationProvider;
import com.zandero.rest.exception.ExecuteException;
import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.*;
import io.vertx.ext.web.RoutingContext;

public class MyOtherAuthenticator implements RestAuthenticationProvider {

    @Override
    public Future<User> authenticate(Credentials credentials) {
        if (credentials instanceof TokenCredentials) {
            String token = ((TokenCredentials) credentials).getToken();
            if (token != null && token.length() > 10) {
                return Future.succeededFuture(new SimulatedUser(token));
            }
            return Future.failedFuture(new ExecuteException(406, "HTTP 406 who are you"));
        }
        return Future.failedFuture(new ExecuteException(400, "HTTP 400 missing credentials"));
    }

    @Override
    public Credentials provideCredentials(RoutingContext context) {
        String token = context.request().getHeader("X-Token-The-Seconds");
        return token != null ? new TokenCredentials(token) : null;
    }
}
