package com.zandero.rest.jakarta.test;

import com.zandero.rest.annotation.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

/**
 *
 */
@Path("/html")
@Produces(MediaType.TEXT_HTML)
public class TestHtmlRest {

    @GET
    @Path("body")
    public String returnBody() {
        return "body";
    }

    @Get("head")
    public String returnHead() {
        return "head";
    }
}
