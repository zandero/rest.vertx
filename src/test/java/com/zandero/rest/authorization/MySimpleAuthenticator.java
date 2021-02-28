package com.zandero.rest.authorization;

import com.zandero.rest.authentication.RestAuthenticationProvider;
import com.zandero.rest.exception.*;
import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.*;
import io.vertx.ext.web.RoutingContext;

public class MySimpleAuthenticator implements RestAuthenticationProvider {

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        String token = credentials != null ? credentials.getString("token") : null;
        if (token != null && token.startsWith("LetMeIn")) {
            resultHandler.handle(Future.succeededFuture(new SimulatedUser(token)));
        } else {
            resultHandler.handle(Future.failedFuture(new ExecuteException(406, "HTTP 406 no entry for you")));
        }
    }

    @Override
    public void authenticate(Credentials credentials, Handler<AsyncResult<User>> resultHandler) {
        if (credentials instanceof TokenCredentials) {
            TokenCredentials token = (TokenCredentials) credentials;
            authenticate(token.toJson(), resultHandler);
        } else {
            resultHandler.handle(Future.failedFuture(new ExecuteException(400, "HTTP 400 just for you")));
        }
    }

    @Override
    public Credentials provideCredentials(RoutingContext context) {
        String token = context.request().getHeader("X-Token");
        return token != null ? new TokenCredentials(token) : null; // token might be null
    }
}
