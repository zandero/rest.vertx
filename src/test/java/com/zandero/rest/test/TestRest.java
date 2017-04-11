package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Simple REST to test annotation processing
 */
@Produces("application/json")
@Path("/test")
public class TestRest {

	@GET
	@Path("/echo")
	public String echo() {

		return "Hello world!";
	}
}
