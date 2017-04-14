package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 *
 */
@Path("query")
public class TestQueryRest {

	@GET
	@Path("add")
	public int add(@QueryParam("one") int one, @QueryParam("two") int two) {

		return one + two;
	}
}
