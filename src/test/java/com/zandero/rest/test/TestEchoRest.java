package com.zandero.rest.test;

import com.zandero.rest.annotation.Trace;

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

	@Trace("echo") // experimenting ... with path is value of method
	public String trace() {
		return "trace";
	}
}
