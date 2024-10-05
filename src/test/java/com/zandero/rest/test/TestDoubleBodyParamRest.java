package com.zandero.rest.test;

import jakarta.ws.rs.*;

/**
 *
 */
@Path("/invalid")
public class TestDoubleBodyParamRest {

    @Path("/double")
    @POST
    public String echo(String body1, String body2) {

        return "not possible";
    }
}
