package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;

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
