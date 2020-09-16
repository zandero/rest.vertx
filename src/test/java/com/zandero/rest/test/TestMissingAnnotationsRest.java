package com.zandero.rest.test;

import org.junit.jupiter.api.Disabled;

import javax.ws.rs.*;

/**
 *
 */
@Path("/incompatible")
public class TestMissingAnnotationsRest {

    @GET
    @Path("ouch")
    public String returnOuch(String bla) {

        return "should not work!";
    }
}
