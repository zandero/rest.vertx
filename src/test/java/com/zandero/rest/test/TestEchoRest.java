package com.zandero.rest.test;

import com.zandero.rest.annotation.*;

import javax.ws.rs.*;

/**
 *
 */
@Path("rest")
public class TestEchoRest {

    @Get(value = "echo",
        produces = "application/json, application/json;charset=UTF-8",
        consumes = "text/html") // experimenting ... with path is value of method
    public String echo() {
        return "echo";
    }

    @Trace("echo")
    public String trace() {
        return "trace";
    }

    @GET
    @Path("echo/body")
    public String echoBody(@BodyParam String body) {
        return body;
    }

    @GET
    @Path("echo/simple/body")
    public String echoSimpleBody(String body) {
        return "Simple: " + body;
    }
}
