package com.zandero.resttest.test;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;

/**
 *
 */
@Path("interface")
@Produces("application/json")
@PermitAll
public interface InterfaceRest {

    @GET
    @Consumes("application/json")
    @Path("echo")
    String echo(@QueryParam("name") String name);
}
