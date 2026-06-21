package com.zandero.rest.jakarta.test;

import com.zandero.rest.annotation.*;
import io.vertx.core.*;
import io.vertx.core.json.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

@Path("/issue")
public class TestIssue109Rest {

    @POST
    @Path("109")
    @ResponseWriter(MyJsonObjectWriter.class)
    public Promise<JsonObject> post(@Context Vertx vertx, JsonObject user) {
        Promise<JsonObject> promise = Promise.promise();

        vertx.eventBus().<io.vertx.core.eventbus.Message<Object>>request("Hello", user).onComplete(replyHandler -> {
            if (replyHandler.failed()) {
                promise.fail(replyHandler.cause());
            } else {
                promise.complete((JsonObject) replyHandler.result().body());
            }
        });
        return promise;
    }
}
