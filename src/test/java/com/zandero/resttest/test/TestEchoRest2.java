package com.zandero.resttest.test;

import jakarta.ws.rs.*;

import static com.zandero.resttest.VertxTest.API_ROOT;

/**
 *
 */
@Path(API_ROOT + "/test") // test
public class TestEchoRest2 {

    @GET
    @Path(API_ROOT + "/bla") //bla
    public String bla() {
        return "bla";
    }
}
