package com.zandero.resttest.test;

import com.zandero.resttest.test.json.Dummy;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Test out custom JSON Object mappers
 */
@Path("/json")
@Produces(MediaType.APPLICATION_JSON)
public class TestJsonRest {

    @GET
    @Path("dummy")
    public Dummy returnBody() {
        return new Dummy("name", "value");
    }
}
