package com.zandero.rest.test.events;

import com.zandero.rest.events.RestEvent;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class FailureEvent implements RestEvent<Exception> {

    @Override
    public void execute(Exception entity, RoutingContext context) throws Throwable {

        System.out.println("Exception triggering event: " + entity.getMessage());
        context.vertx().eventBus().send("rest.vertx.testing", JsonUtils.toJson(entity)); // send as JSON to event bus ...
    }
}
