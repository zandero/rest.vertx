package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/incompatible")
public class TestMissingAnnotationsRest {

	@GET
	@Path("ouch")
	public String returnOuch(String bla) {

		return "should not work!";
	}
}
