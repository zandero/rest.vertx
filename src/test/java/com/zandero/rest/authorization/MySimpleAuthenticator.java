package com.zandero.rest.authorization;

import com.zandero.rest.authentication.RestAuthenticationProvider;
import com.zandero.rest.exception.ExecuteException;
import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.*;
import io.vertx.ext.web.RoutingContext;

public class MySimpleAuthenticator implements RestAuthenticationProvider {

    @Override
    public Future<User> authenticate(Credentials credentials) {
        if (credentials instanceof TokenCredentials) {
            String token = ((TokenCredentials) credentials).getToken();
            if (token != null && (token.startsWith("LetMeIn") || token.startsWith("SuperSecretPassword"))) {
                return Future.succeededFuture(new SimulatedUser(token));
            }
            return Future.failedFuture(new ExecuteException(406, "HTTP 406 no entry for you"));
        }
        return Future.failedFuture(new ExecuteException(400, "HTTP 400 just for you"));
    }

    @Override
    public Credentials provideCredentials(RoutingContext context) {
        String token = context.request().getHeader("X-Token");
        return token != null ? new TokenCredentials(token) : null;
    }
}
