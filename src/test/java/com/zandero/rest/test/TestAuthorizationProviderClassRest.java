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
@Authenticate(MySimpleAuthenticator.class)
@Authorize(TestAuthorizationProvider.class)
public class TestAuthorizationProviderClassRest {

    @GET
    @Path("/access")
    @Produces(MediaType.TEXT_PLAIN)
    public String first() {
        return "access granted";
    }

    @GET
    @Path("/secondAccess")
    @Authenticate(MyOtherAuthenticator.class)
    @Produces(MediaType.TEXT_PLAIN)
    public String second() {
        return "access granted";
    }

    @GET
    @Path("/thirdAccess")
    @Authorize(OtherAuthorizationProvider.class)
    @Produces(MediaType.TEXT_PLAIN)
    public String third() { return "access granted"; }

    @GET
    @Path("/fourthAccess")
    @Authorize(OtherAuthorizationProvider.class)
    @Authenticate(MyOtherAuthenticator.class)
    @Produces(MediaType.TEXT_PLAIN)
    public String fourth() { return "access granted"; }
}
