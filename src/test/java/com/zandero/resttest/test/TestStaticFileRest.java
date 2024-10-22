package com.zandero.resttest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.writer.FileResponseWriter;

import jakarta.ws.rs.*;

/**
 *
 */
@Path("docs")
public class TestStaticFileRest {

    @GET
    @Path("/{path:.*}")
    @ResponseWriter(FileResponseWriter.class)
    public String serveDocFile(@PathParam("path") String path) {

        return "html/" + path;
    }
}
