package com.zandero.rest.handler;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.*;

public class TestAuthProvider implements AuthProvider {

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {

    }
}
