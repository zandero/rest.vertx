package com.zandero.rest.test;

import javax.annotation.security.*;
import javax.ws.rs.*;

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
