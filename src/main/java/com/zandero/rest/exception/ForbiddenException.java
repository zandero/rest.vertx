package com.zandero.rest.exception;

import io.vertx.ext.auth.User;

import javax.ws.rs.core.Response;

/**
 * We know the user but the user is not allowed to access a particular resource
 */
public class ForbiddenException extends ExecuteException {

    public final User user;

    public ForbiddenException(User user) {
        super(Response.Status.FORBIDDEN.getStatusCode(), "HTTP 403 Forbidden");
        this.user = user;
    }
}
