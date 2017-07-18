package com.zandero.rest.test;

import com.zandero.rest.test.json.Dummy;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 *
 */
@Path("/default")
public class TestDefaultValueRest {

	@GET
	@Path("/echo")
	public String echoQuery(@QueryParam("name") @DefaultValue("unknown") String name) {

		return "Hello " + name;
	}

	@GET
	@Path("/context")
	public String echoContext(@Context @DefaultValue("{\"name\":\"unknown\", \"value\": \"user\"}") Dummy user) {

		return "Context is " + user.name + " " + user.value;
	}
}

