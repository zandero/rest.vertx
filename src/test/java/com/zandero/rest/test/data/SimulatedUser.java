package com.zandero.rest.test.data;

import com.zandero.rest.authorization.RoleBasedUser;
import io.vertx.ext.auth.impl.UserImpl;

import java.util.Arrays;

/**
 * Simplistic user where name is his role ...
 */
public class SimulatedUser extends UserImpl implements RoleBasedUser {

    private final String role;

    public SimulatedUser(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean hasRole(String... roles) {
        return roles != null && roles.length > 0 && Arrays.asList(roles).contains(role);
    }

    public static SimulatedUser valueOf(String value) {
        return new SimulatedUser(value);
    }
}
