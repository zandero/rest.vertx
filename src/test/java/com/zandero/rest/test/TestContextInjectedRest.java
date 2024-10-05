package com.zandero.rest.test;

import com.zandero.rest.test.data.SimulatedUser;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

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
