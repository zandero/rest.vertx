package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("rest")
public class TestEchoRest {

	@GET
	@Path("echo")
	public String echo() {
		return "echo";
	}
}
