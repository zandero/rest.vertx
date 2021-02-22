package com.zandero.rest.authorization;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.UnauthorizedException;
import com.zandero.utils.Assert;
import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;

public class RoleBasedUserAuthorizationProvider implements AuthorizationProvider {

    private final RouteDefinition definition;

    public RoleBasedUserAuthorizationProvider(RouteDefinition routeDefinition) {
        Assert.notNull(routeDefinition, "No route definition provided: RoleBasedAuthorizationProvider!");
        definition = routeDefinition;
    }

    @Override
    public String getId() {
        return definition.getPath();
    }

    @Override
    public void getAuthorizations(User user, Handler<AsyncResult<Void>> handler) {

        if (definition.getPermitAll() != null) {
            if (definition.getPermitAll()) {
                handler.handle(Future.succeededFuture());
            } else {
                handler.handle(Future.failedFuture(new UnauthorizedException(user)));
            }
        } else {
            if (user instanceof RoleBasedUser) {
                if (((RoleBasedUser) user).hasRole(definition.getRoles()))
                    handler.handle(Future.succeededFuture());
                else
                    handler.handle(Future.failedFuture(new UnauthorizedException(user)));
            } else {
                handler.handle(Future.failedFuture(new UnauthorizedException(user)));
            }
        }
    }
}
