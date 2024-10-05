package com.zandero.rest.test;

import com.zandero.rest.data.RouteDefinition;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

/**
 *
 */
@Path("/application")
public class TestApplicationV1PathRest implements RestApplicationV1 {

    @Path("/echo/{param}")
    @GET
    public String echo(
        @Context RouteDefinition definition,
        @PathParam("param") String param) {

        return param;
    }
}
