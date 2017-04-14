package com.zandero.rest.test;

import com.zandero.rest.test.json.Dummy;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 */
@Produces("application/json")
@Path("/post")
public class TestPostRest {

	@POST
	@Path("/json")
	public Dummy echoJsonPut(Dummy postParam, @HeaderParam("X-Test") String testHeader) {

		postParam.name = "Received-" + postParam.name;
		postParam.value = "Received-" + postParam.value;

		return postParam;
	}
}
