package com.zandero.resttest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.authorization.*;

import com.zandero.resttest.authorization.MyAuthenticator;
import com.zandero.resttest.authorization.MySimpleAuthenticator;
import com.zandero.resttest.authorization.TestAuthorizationProvider;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Test access based on assigned AuthorizationProvider
 *
 */
@Path("/private")
@Authenticate(MyAuthenticator.class)
@Authorize(TestAuthorizationProvider.class)
public class TestAuthorizationProviderRest {

    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    public String user() {
        return "user";
    }

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String all() {
        return "all";
    }

    @GET
    @Path("/all_default")
    @Produces(MediaType.TEXT_PLAIN)
    @Authorize(role = "user") // uses RoleBasedAuthorizationProvider
    public String all_default() {

        return "all";
    }

    @GET
    @Path("/other_user")
    @Produces(MediaType.TEXT_PLAIN)
    @Authenticate(MySimpleAuthenticator.class) // override class authenticator
    public String other_user() {
        return "other_user";
    }
}
