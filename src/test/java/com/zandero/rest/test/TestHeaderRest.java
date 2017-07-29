package com.zandero.rest.test;

import com.zandero.rest.test.json.Dummy;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

/**
 *
 */
@Path("header")
public class TestHeaderRest {

	@GET
	@Path("/dummy")
	public String getExtendedDummyFromHeader(@HeaderParam("dummy") Dummy dummy) {

		return dummy.name + "=" + dummy.value;
	}
}
