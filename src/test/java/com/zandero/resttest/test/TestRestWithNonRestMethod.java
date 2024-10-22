package com.zandero.resttest.test;

import jakarta.ws.rs.*;

/**
 *
 */
@Path("/mixed")
public class TestRestWithNonRestMethod {

    @Path("/echo")
    @GET
    public String echo() {

        return hello();
    }

    public String hello() {
        return "hello";
    }
}
