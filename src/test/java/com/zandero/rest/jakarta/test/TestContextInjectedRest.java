package com.zandero.rest.jakarta.test;

import com.zandero.rest.test.data.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

/**
 *
 */
@Path("/context")
public class TestContextInjectedRest {

    @GET
    @Path("/user")
    public String login(@Context SimulatedUser user) {

        return user.getRole();
    }
}
