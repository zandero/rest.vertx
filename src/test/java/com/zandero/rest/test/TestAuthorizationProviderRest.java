package com.zandero.rest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.authorization.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Test access based on assigned AuthorizationProvider
 *
 */
@Path("/private")
@Authenticate(auth = MyAuthenticator.class, with = MyCredentialProvider.class)
public class TestAuthorizationProviderRest {

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    @Authorize(TestAuthorizationProvider.class)
    public String all() {
        return "all";
    }

    @GET
    @Path("/all_default")
    @Produces(MediaType.TEXT_PLAIN)
    @Authorize(role = "user")
    public String all_default() {

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
