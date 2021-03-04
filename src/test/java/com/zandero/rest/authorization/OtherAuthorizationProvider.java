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
    public void getAuthorizations(User user, Handler<AsyncResult<Void>> handler) {

        if (RoleBasedAuthorization.create("SuperSecretPassword").match(user)) {
            handler.handle(Future.succeededFuture());
        } else {
            handler.handle(Future.failedFuture(new ExecuteException(400, "HTTP 400 Bad Request")));
        }
    }
}
