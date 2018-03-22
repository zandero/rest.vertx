package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/invalid")
public class TestDoubleBodyParamRest {

	@Path("/double")
	@POST
	public String echo(String body1, String body2) {

		return "not possible";
	}
}
