package com.zandero.rest.authorization;

public interface RoleBasedUser {

    boolean hasRole(String... roles);
}
