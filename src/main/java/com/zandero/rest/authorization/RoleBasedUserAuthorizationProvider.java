package com.zandero.rest.authorization;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.ForbiddenException;
import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.*;
import org.slf4j.*;

import javax.ws.rs.core.Context;
import java.util.*;

/**
 * This is for back compatibility purposes only, since Vert.x 4 the @Authentication / @Authorization annotations with
 * AuthenticationProvider / AuthorizationProvider should be used.
 * <p>
 * Handling: @PermitAll, @DenyAll and  @RolesAllowed route annotations
 * Expects user to provide a PermissionBasedAuthorization with a given role in order to check if user is in certain role
 */
public class RoleBasedUserAuthorizationProvider implements AuthorizationProvider {

    @Context
    private RouteDefinition definition;

    private final static Logger log = LoggerFactory.getLogger(RoleBasedUserAuthorizationProvider.class);

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
                handler.handle(Future.failedFuture(new ForbiddenException(user)));
            }
        } else {
            try {
                if (user != null && user.authorizations() != null && definition.getRoles() != null) {
                    Optional<String> found = Arrays.stream(definition.getRoles())
                                                 .filter(role -> RoleBasedAuthorization.create(role).match(user))
                                                 .findFirst();

                    if (found.isPresent()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        log.trace("User authorization failed: '" + user.principal() + "', not authorized to access: " + definition.toString());
                        handler.handle(Future.failedFuture(new ForbiddenException(user)));
                    }
                } else {
                    if (definition.getRoles() == null) {
                        log.trace("User authorization failed: " + definition.toString() + ", is missing @RolesAllowed annotation. " +
                                      "Either provide @RolesAllowed annotation or use different AuthorizationProvider");
                    } else if (user != null) {
                        log.trace("User authorization failed: '" + user.principal() + "', not authorized to access: " + definition.toString());
                    } else {
                        log.trace("User authorization failed: no user was provided, for: " + definition.toString());
                    }

                    handler.handle(Future.failedFuture(new ForbiddenException(user)));
                }
            } catch (Throwable e) {
                log.error("Failed to provide user authorization: " + e.getMessage(), e);
                handler.handle(Future.failedFuture(e));
            }
        }
    }
}
