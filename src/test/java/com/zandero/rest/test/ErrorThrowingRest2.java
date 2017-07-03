package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/throw")
public class ErrorThrowingRest2 {

	@GET
	@Path("unhandled")
	public String returnKabum() {

		throw new IllegalArgumentException("KABUM!");
	}
}
