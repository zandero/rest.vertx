package com.zandero.resttest.test;

import com.zandero.resttest.test.json.User;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 *
 */
@Path("xml")
public class TestWithXmlRest {

    @GET
    @Path("/test")
    @Produces({MediaType.APPLICATION_XML})
    public User h1() {
        return new User("test");
    }

    @GET
    @Path("/test2")
    public User h2() {
        return new User("test");
    }
}
