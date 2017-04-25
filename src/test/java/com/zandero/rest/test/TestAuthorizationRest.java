package com.zandero.rest.test;

import io.vertx.ext.auth.User;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

	@GET
	@Path("/admin")
	@Produces(MediaType.TEXT_PLAIN)
	@RolesAllowed("admin")
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
