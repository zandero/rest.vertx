package com.zandero.rest.jakarta.test;

import com.zandero.rest.test.json.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

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
