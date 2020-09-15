package com.zandero.rest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.test.events.*;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 *
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("events")
public class TestEventsRest {

    @GET
    @Path("ok")
    @Event(SimpleEvent.class) // always triggered
    public Dummy returnBody() {
        return new Dummy("one", "event");
    }

    @GET
    @Path("error/{status}")
    @Events({@Event(SimpleEvent.class),
        @Event(value = FailureEvent.class, exception = IllegalArgumentException.class), // triggered via exception thrown
        @Event(value = SimpleEvent.class, response = 301)}) // triggered on response code 301
    public Dummy returnOrFail(@PathParam("status") int status, @Context HttpServerResponse response) {

        if (status >= 200 && status < 300) {
            return new Dummy("one", "event");
        }

        if (status >= 300 && status < 400) {
            response.setStatusCode(301);
            return new Dummy("two", "failed");
        }

        throw new IllegalArgumentException("Failed: " + status);
    }
}
