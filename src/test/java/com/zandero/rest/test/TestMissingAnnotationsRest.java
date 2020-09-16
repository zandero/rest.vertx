package com.zandero.rest.test;

import javax.ws.rs.*;

/**
 *
 */
@Deprecated
@Path("/incompatible")
public class TestMissingAnnotationsRest {

    @GET
    @Path("ouch")
    public String returnOuch(String bla) {

        return "should not work!";
    }
}
