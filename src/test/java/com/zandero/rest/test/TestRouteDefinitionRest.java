package com.zandero.rest.test;

import com.zandero.rest.data.RouteDefinition;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;

/**
 *
 */
@Path("route")
public class TestRouteDefinitionRest {

    @GET
    @Path("/definition")
    public String route(@Context RouteDefinition definition) {
        return definition.getReturnType().toString() + ", " +
            definition.getParameters().get(0).getName() + ", " +
            definition.getParameters().get(0).getDataType().toString() + ", " +
            definition.getParameters().get(0).getType().toString();
    }
}
