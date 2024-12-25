package com.zandero.rest.jakarta.test;

import jakarta.ws.rs.*;

import static com.zandero.rest.VertxTest.*;

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
