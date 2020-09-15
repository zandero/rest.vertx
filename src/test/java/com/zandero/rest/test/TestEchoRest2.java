package com.zandero.rest.test;

import javax.ws.rs.*;

import static com.zandero.rest.VertxTest.API_ROOT;

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
