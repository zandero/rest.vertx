package com.zandero.rest.authorization;

import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.impl.RoleBasedAuthorizationImpl;

public class RoleBasedAuthorizationProvider implements AuthorizationProvider  {

    private final String role;

    public RoleBasedAuthorizationProvider(String role) {
        this.role = role;
    }

    @Override
    public String getId() {
        return role;
    }

    @Override
    public void getAuthorizations(User user, Handler<AsyncResult<Void>> handler) {
        if (user.attributes() != null && user.containsKey(role)) {
            user.authorizations().add(getId(), new RoleBasedAuthorizationImpl(role));
            handler.handle(Future.succeededFuture());
        }
        else {
            handler.handle(Future.failedFuture("Bla"));
        }
    }
}
