package com.zandero.rest.exception;

import io.vertx.ext.auth.User;

import javax.ws.rs.core.Response;

/**
 * We don't know the user, he is not allowed to access any resource
 */
public class UnauthorizedException extends ExecuteException {

    public final User user;

    public UnauthorizedException(User user) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(), "HTTP 401 Unauthorized");
        this.user = user;
    }
}
