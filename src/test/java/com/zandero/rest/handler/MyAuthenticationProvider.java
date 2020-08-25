package com.zandero.rest.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;

public class MyAuthenticationProvider implements AuthenticationProvider {

    @Override
    public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {

    }
}
