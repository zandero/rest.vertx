package com.zandero.rest.jakarta.test;

import com.zandero.rest.data.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

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
