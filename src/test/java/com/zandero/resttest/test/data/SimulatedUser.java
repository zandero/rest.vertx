package com.zandero.resttest.test.data;

import com.zandero.resttest.authorization.TestAuthorizationProvider;
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
        return new AuthorizationsImpl().add("",
                                            RoleBasedAuthorization.create(role))
                   .add(TestAuthorizationProvider.ID,
                        PermissionBasedAuthorization.create(role));
    }

    public static SimulatedUser valueOf(String value) {
        return new SimulatedUser(value);
    }
}
