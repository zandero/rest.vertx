package com.zandero.rest.jakarta.test;

import com.zandero.rest.test.json.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

/**
 *
 */
@Path("/default")
public class TestDefaultValueRest {

    @GET
    @Path("/echo")
    public String echoQuery(@QueryParam("name") @DefaultValue("unknown") String name) {

        return "Hello " + name;
    }

    @GET
    @Path("/context")
    public String echoContext(@Context @DefaultValue("{\"name\":\"unknown\", \"value\": \"user\"}") Dummy user) {

        return "Context is " + user.name + " " + user.value;
    }
}

