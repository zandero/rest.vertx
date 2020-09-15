package com.zandero.rest.test;

import com.zandero.rest.annotation.RouteOrder;
import io.vertx.ext.auth.User;

import javax.annotation.security.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

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
    //@RouteOrder(10)
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

        return user.principal().encode();
    }
}
