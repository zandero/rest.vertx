package com.zandero.rest.test;

import com.zandero.rest.test.json.Dummy;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
