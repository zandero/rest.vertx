package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Path("regEx")
public class TestRegExRest {

	@GET
	@Path("/\\d")
	public Response test() {
		return Response.ok().build();
	}

	@GET
	@Path("/{one:\\w}/{two:\\d}/{three:\\s}")
	public Response oneTwoThree(@PathParam("two") int two, @PathParam("one") String one, @PathParam("three") String three) {

		Map<String, String> map = new HashMap<>();
		map.put("one", one);
		map.put("two", two + "");
		map.put("three", three);

		return Response.ok(map).build();
	}
}
