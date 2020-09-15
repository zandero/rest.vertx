package com.zandero.rest.test;

import com.zandero.rest.annotation.ContextReader;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.test.data.*;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.http.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 *
 */
@Path("/context")
public class TestContextRest {

    @GET
    @Path("/route")
    public String getRoute(@Context RouteDefinition definition) {

        return definition.toString().trim();
    }

    @GET
    @Path("/context")
    public String getRoute(@Context HttpServerResponse response, @Context HttpServerRequest request) {

        response.setStatusCode(201);
        return request.uri();
    }

    @GET
    @Path("/unknown")
    public String getRoute(@Context Request request) {

        return null;
    }

    @GET
    @Path("/custom")
    @Produces(MediaType.APPLICATION_JSON)
    public Dummy getRoute(@Context Dummy object) {

        return object;
    }

    @GET
    @Path("/login")
    public HttpServerResponse login(@Context HttpServerResponse response) {

        response.setStatusCode(201);
        response.putHeader("X-SessionId", "session");
        response.end("Hello world!");
        return response;
    }

    @GET
    @Path("/token")
    public String login(@Context Token token) {

        return token.token;
    }

    @GET
    @Path("/dummy")
    @ContextReader(DummyProvider.class)
    public String contextRead(@Context Token token, @Context Dummy dummy) {

        return token.token + "," + dummy.name + ":" + dummy.value;
    }

    @GET
    @Path("/dummy-token")
    public String paramContextRead(@ContextReader(NewTokenProvider.class) @Context Token token,
                                   @ContextReader(DummyProvider.class) @Context Dummy dummy) {

        return token.token + "," + dummy.name + ":" + dummy.value;
    }
}
