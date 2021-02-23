package com.zandero.rest.authorization;

/**
 * Interface to be implemented in order to check if User is in certain role
 * To be used with default RoleBasedUserAuthorizationProvider
 */
public interface RoleBasedUser {

    boolean hasRole(String... roles);
}
