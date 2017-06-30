package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/throw")
public class ErrorThrowingRest {

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("unhandled")
	public String returnBody() {

		throw new IllegalArgumentException("Ouch!");
	}
}
