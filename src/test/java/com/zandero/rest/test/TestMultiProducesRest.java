package com.zandero.rest.test;

import javax.ws.rs.*;

/**
 *
 */
@Path("multi")
@Produces("application/xml")
public class TestMultiProducesRest {

    @GET
    @Path("/consume")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json"}) // "application/xml"
    public String getAsDesired() {

        return "HELLO!";
    }
}
