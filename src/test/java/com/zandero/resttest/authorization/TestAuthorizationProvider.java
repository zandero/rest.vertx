package com.zandero.resttest.authorization;

import com.zandero.rest.exception.ExecuteException;
import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.*;

public class TestAuthorizationProvider implements AuthorizationProvider {

    public static final String ID = "TestAuthorizationProvider";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void getAuthorizations(User user, Handler<AsyncResult<Void>> handler) {

        if (RoleBasedAuthorization.create("IKnowThePassword").match(user)) {
            handler.handle(Future.succeededFuture());
        } else if (PermissionBasedAuthorization.create("LetMeIn").match(user)) {
            handler.handle(Future.succeededFuture());
        } else {
            handler.handle(Future.failedFuture(new ExecuteException(400, "HTTP 400 Bad Request")));
        }
    }
}
