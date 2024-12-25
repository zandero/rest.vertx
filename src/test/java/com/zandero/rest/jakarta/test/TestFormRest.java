package com.zandero.rest.jakarta.test;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

/**
 *
 */
@Path("form")
public class TestFormRest {

    @POST
    @Path("login")
    @Produces(MediaType.TEXT_PLAIN)
    public String loginForm(@FormParam("username") String username, @FormParam("password") String password) {

        return username + ":" + password;
    }

    @POST
    @Path("cookie")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCookie(@CookieParam("username") String username) {

        return username;
    }
}
