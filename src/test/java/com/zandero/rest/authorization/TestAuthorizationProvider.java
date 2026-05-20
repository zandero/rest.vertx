package com.zandero.rest.authorization;

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
    public Future<Void> getAuthorizations(User user) {

        if (RoleBasedAuthorization.create("IKnowThePassword").match(user)) {
            return Future.succeededFuture();
        } else if (PermissionBasedAuthorization.create("LetMeIn").match(user)) {
            return Future.succeededFuture();
        } else {
            return Future.failedFuture(new ExecuteException(400, "HTTP 400 Bad Request"));
        }
    }
}
