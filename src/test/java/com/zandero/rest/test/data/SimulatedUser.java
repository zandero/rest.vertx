package com.zandero.rest.test.data;

import com.zandero.rest.authorization.TestAuthorizationProvider;
import io.vertx.ext.auth.authorization.*;
import io.vertx.ext.auth.authorization.impl.AuthorizationsImpl;
import io.vertx.ext.auth.impl.UserImpl;

/**
 * Simplistic user where name is his role ...
 */
public class SimulatedUser extends UserImpl {

    private final String role;

    public SimulatedUser(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Authorizations authorizations() {
        AuthorizationsImpl authorizations = new AuthorizationsImpl();
        authorizations.put("", RoleBasedAuthorization.create(role));
        authorizations.put(TestAuthorizationProvider.ID, PermissionBasedAuthorization.create(role));
        return authorizations;
    }

    public static SimulatedUser valueOf(String value) {
        return new SimulatedUser(value);
    }
}
