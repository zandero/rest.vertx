package com.zandero.rest.test;

import com.zandero.rest.annotation.Catch;
import com.zandero.rest.test.handler.UnhandledRestErrorHandler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/throw")
@Catch(UnhandledRestErrorHandler.class)
public class ErrorThrowingRest {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("unhandled")
	public String returnBody() {

		throw new IllegalArgumentException("Ouch!");
	}
}
