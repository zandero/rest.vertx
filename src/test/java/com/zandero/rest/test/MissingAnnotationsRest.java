package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/incompatible")
public class MissingAnnotationsRest {

	@GET
	@Path("ouch")
	public String returnOuch(String bla) {

		return "should not work!";
	}
}
