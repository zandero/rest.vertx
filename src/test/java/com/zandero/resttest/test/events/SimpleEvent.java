package com.zandero.resttest.test.events;

import com.zandero.rest.events.RestEvent;
import com.zandero.resttest.test.json.Dummy;
import com.zandero.rest.utils.extra.JsonUtils;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class SimpleEvent implements RestEvent<Dummy> {

    @Override
    public void execute(Dummy entity, RoutingContext context) {

        System.out.println("Event triggered: " + entity.name + ": " + entity.value);
        context.vertx().eventBus().send("rest.vertx.testing", JsonUtils.toJson(entity)); // send as JSON to event bus ...
    }
}
