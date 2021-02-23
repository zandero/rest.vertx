package com.zandero.rest.test;

import com.zandero.rest.annotation.Authorize;
import com.zandero.rest.authorization.TestAuthorizationProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Test access based on assigned AuthorizationProvider
 */
@Path("/private")
public class TestAuthorizationProviderRest {

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    @Authorize(TestAuthorizationProvider.class)
    public String all() {

        return "all";
    }

    /*@GET
    @Path("/nobody")
    @Produces(MediaType.TEXT_PLAIN)
    @DenyAll()
    public String nobody() {

        return "nobody";
    }

    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("user")
    public String user() {

        return "user";
    }

    @POST
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("user")
    public String setTest(String test) {

        return test;
    }

    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("admin")
    @RouteOrder(20)
    public String admin() {

        return "admin";
    }

    @GET
    @Path("/other")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"one", "two"})
    public String oneOrTwo(@Context User user) {

        if (user instanceof SimulatedUser)
            return "{\"role\":\"" + ((SimulatedUser)user).getRole() + "\"}";

        return user.toString();
    }*/
}
