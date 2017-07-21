package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/invalid")
public class TestInvalidMethodRest {

	@Path("/duplicate")
	@POST
	@GET
	public String echo() {

		return "not possible";
	}
}
