package com.zandero.rest.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;

public class MyAuthorizationProvider implements AuthorizationProvider {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void getAuthorizations(User user, Handler<AsyncResult<Void>> handler) {

    }
}
