package com.zandero.resttest.test;

import jakarta.ws.rs.*;

/**
 *
 */
@Path("/{root}")
public class TestPathRest {

    @Path("/echo/{param}")
    @GET
    public String echo(@PathParam("root") String rootPath, @PathParam("param") String param) {

        return rootPath + param;
    }

    // Root only
    @GET
    public String echoRoot(@PathParam("root") String rootPath) {

        return rootPath;
    }
}
