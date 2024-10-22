package com.zandero.resttest.test;

import com.zandero.resttest.test.data.SimulatedUser;

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
