package com.zandero.rest.test;

import javax.ws.rs.*;

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
