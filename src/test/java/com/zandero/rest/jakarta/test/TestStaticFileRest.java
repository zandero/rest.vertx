package com.zandero.rest.jakarta.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.writer.*;
import jakarta.ws.rs.*;

/**
 *
 */
@Path("/docs")
public class TestStaticFileRest {

    @GET
    @Path("/{path:.*}")
    @ResponseWriter(FileResponseWriter.class)
    public String serveDocFile(@PathParam("path") String path) {

        return "html/" + path;
    }
}
