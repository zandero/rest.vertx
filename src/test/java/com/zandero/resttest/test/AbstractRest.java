package com.zandero.resttest.test;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

/**
 *
 */
@Path("abstract")
@RolesAllowed("admin")
public abstract class AbstractRest implements InterfaceRest {

    @Produces("html/text") // override interface
    @Override
    public String echo(String name) {
        return name;
    }

    @GET
    @Consumes("application/json")
    @Path("get/{id}")
    @RolesAllowed("user")
    public abstract String get(@PathParam("id") String id, String add);
}
