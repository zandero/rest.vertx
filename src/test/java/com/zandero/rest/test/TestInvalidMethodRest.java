package com.zandero.rest.test;

import javax.ws.rs.*;

/**
 *
 */
@Path("/invalid")
public class TestInvalidMethodRest {

    @Path("/duplicate")
    @POST
    @GET
    public String echo() {

        return "not possible";
    }
}
