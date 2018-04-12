package com.zandero.rest.test;

import com.zandero.rest.test.json.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
