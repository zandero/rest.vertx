package com.zandero.rest.authorization;

import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;

public class MyAuthenticator implements AuthenticationProvider {

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {

        String token = credentials.getString("token");

        if (token != null) {
            resultHandler.handle(Future.succeededFuture(new SimulatedUser(token)));
        } else {
            resultHandler.handle(Future.failedFuture("Missing authentication token"));
        }
    }
}
