package com.zandero.resttest.test;

import com.zandero.rest.annotation.RouteOrder;
import com.zandero.resttest.test.data.SimulatedUser;
import io.vertx.ext.auth.User;

import jakarta.annotation.security.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

/**
 * Test access based on User context / roles
 */
@Path("/private")
public class TestAuthorizationRest {

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll()
    public String all() {

        return "all";
    }

    @GET
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
    }
}
