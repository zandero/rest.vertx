package com.zandero.rest.jakarta.test;

import jakarta.annotation.security.*;
import jakarta.ws.rs.*;

/**
 *
 */
@Path("implementation")
public class ImplementationRest extends AbstractRest {

    @RolesAllowed("test")
    @Consumes("html/text") // override abstract
    @Override
    public String get(String id, @QueryParam("additional") String add) {
        return id + add;
    }

    @GET
    @Path("other")
    @PermitAll                // override abstract "admin" role
    public String other() {
        return "other";
    }
}
