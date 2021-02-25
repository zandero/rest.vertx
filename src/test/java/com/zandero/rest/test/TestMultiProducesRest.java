package com.zandero.rest.test;

import com.zandero.rest.annotation.Header;

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

    @GET
    @Path("/produce")
    /*@Consumes({"application/json", "application/xml"})
    @Produces({"application/json"}) // "application/xml"*/
    @Header({"Accept: application/json",
        "Content-Type: application/json"})
    public String getAsJson() {
        return "Bam!";
    }

}
