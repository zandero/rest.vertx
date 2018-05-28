package com.zandero.rest.test;

import com.zandero.rest.annotation.Get;
import com.zandero.rest.annotation.Trace;

import javax.ws.rs.Path;

/**
 *
 */
@Path("rest")
public class TestEchoRest {

	@Get(value = "echo", produces = "application/json", consumes = "text/html") // experimenting ... with path is value of method
	public String echo() {
		return "echo";
	}

	@Trace(value = "echo")
	public String trace() {
		return "trace";
	}

}
