package com.zandero.rest.handler;

import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class MyUserHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {

        // read header ... if present ... create user with given value
        String token = routingContext.request().getHeader("X-Token");

        // set user ...
        if (token != null) {
            routingContext.setUser(new SimulatedUser(token));
        }

        routingContext.next();
    }
}
