package com.zandero.rest.handler;

import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.injection.UserService;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.User;

import javax.inject.Inject;

/**
 *
 */
public class UserHandler implements ContextProvider<User> {

    @Inject
    UserService users;

    @Override
    public User provide(HttpServerRequest request) {
        String token = request.getHeader("X-Token");

        // set user ...
        if (token != null) {
            return users.getUser(token);
        }

        return null;
    }
}
