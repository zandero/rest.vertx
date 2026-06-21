package com.zandero.rest.authorization;

import com.zandero.rest.exception.ExecuteException;
import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.*;

public class OtherAuthorizationProvider implements AuthorizationProvider {

    public static final String ID = "OtherAuthorizationProvider";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Future<Void> getAuthorizations(User user) {

        if (RoleBasedAuthorization.create("SuperSecretPassword").match(user)) {
            return Future.succeededFuture();
        } else {
            return Future.failedFuture(new ExecuteException(400, "HTTP 400 Bad Request"));
        }
    }
}
