package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

@Path("/issue")
public class TestIssue109Rest {

    @POST
    @Path("109")
    @ResponseWriter(MyJsonObjectWriter.class)
    public Promise<JsonObject> post(@Context Vertx vertx, JsonObject user) {
        Promise<JsonObject> promise = Promise.promise();

        vertx.eventBus().request("Hello", user, replyHandler -> {
            if (!replyHandler.succeeded()) {
                promise.fail(replyHandler.cause());
            } else {
                // promise.complete();
                promise.complete((JsonObject) replyHandler.result().body());
            }
        });
        return promise;
    }
}
