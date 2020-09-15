package com.zandero.rest.test;

import com.zandero.rest.annotation.Get;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
