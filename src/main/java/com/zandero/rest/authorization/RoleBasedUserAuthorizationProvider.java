package com.zandero.rest.authorization;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.UnauthorizedException;
import com.zandero.utils.Assert;
import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;

/**
 * This is for back compatibility purposes only, since Vert.x 4 the @Authentication / @Authorization annotations with
 * AuthenticationProvider / AuthorizationProvider should be used.
 *
 * Default Authorization provider handing: @PermitAll, @DenyAll and  @RolesAllowed route annotations
 * Expects user to implement RoleBasedUser interface in order to check if user is in certain role
 */
public class RoleBasedUserAuthorizationProvider implements AuthorizationProvider {

    private final RouteDefinition definition;

    public RoleBasedUserAuthorizationProvider(RouteDefinition routeDefinition) {
        Assert.notNull(routeDefinition, "No route definition provided: RoleBasedAuthorizationProvider!");
        definition = routeDefinition;
    }

    @Override
    public String getId() {
        return definition.getId();
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
