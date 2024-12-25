package com.zandero.rest.jakarta.test;

import jakarta.ws.rs.*;

/**
 * Test REST serving static files
 */
@Path("/")
public class TestFileRest {

    @GET
    @Path("{file}")
    public String serveFile(@PathParam("file") String file) {
        return file;
    }
}
