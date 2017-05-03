package com.zandero.rest.test;

import com.zandero.rest.annotation.RouteOrder;

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

	@RouteOrder(20)
	@GET
	@Path("/{one:\\w+}/{two:\\d+}/{three:\\w+}")
	public Response oneTwoThree(@PathParam("two") int two, @PathParam("one") String one, @PathParam("three") String three) {

		Map<String, String> map = new HashMap<>();
		map.put("one", one);
		map.put("two", two + "");
		map.put("three", three);

		return Response.ok(map).build();
	}

	@RouteOrder(10)
	@GET
	@Path("/\\d+")
	public Response test(int one) {
		return Response.ok(one).build();
	}

	@RouteOrder(15)
	@GET
	@Path("/\\d+/minus/\\d+")
	public Response test(int one, int two) {
		return Response.ok(one - two).build();
	}
}
