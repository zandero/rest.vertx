package com.zandero.rest.injection;

import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.test.data.SimulatedUser;
import com.zandero.rest.test.handler.MyExceptionClass;
import io.vertx.core.http.HttpServerRequest;

import javax.inject.Inject;

/**
 *
 */
public class SimulatedUserProvider implements ContextProvider<SimulatedUser> {

    private final UserService users;

    @Inject
    SimulatedUserProvider(UserService service) {
        users = service;
    }

    @Override
    public SimulatedUser provide(HttpServerRequest request) throws MyExceptionClass {

        String token = request.getHeader("X-Token");
        if (token == null) {
            throw new MyExceptionClass("No user present!", 404);
        }
        return users.getUser(token);
    }
}
