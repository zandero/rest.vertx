package com.zandero.rest.test;

import com.zandero.rest.data.*;
import io.vertx.core.http.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 *
 */
@Path("/application")
public class TestApplicationV2PathRest extends RestApplicationV2 {

    @Path("/echo/{param}")
    @GET
    public String echo(
        @Context RouteDefinition definition,
        @PathParam("param") String param,
        @QueryParam("query") String query) {

        return "2" + param + query;
    }

    @Path("/echo2/{param}")
    @GET
    public String echo2(
        @PathParam("param") String param,
        @QueryParam("query") String query,
        @Context HttpServerRequest request) {
        return request.path() + "2" + query;
    }
}
