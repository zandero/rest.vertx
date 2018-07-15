package com.zandero.rest.test;

import com.zandero.rest.test.data.SimulatedUser;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

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
