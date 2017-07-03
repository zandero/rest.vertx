package com.zandero.rest.test;

import com.zandero.rest.annotation.Catch;
import com.zandero.rest.test.handler.HandleRestException;
import com.zandero.rest.test.handler.UnhandledRestErrorHandler;
import com.zandero.rest.writer.GenericResponseWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/throw")
@Catch(UnhandledRestErrorHandler.class) // catch globaly for whole root
public class ErrorThrowingRest {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("ouch")
	public String returnOuch() {

		throw new IllegalArgumentException("Ouch!");
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("bang")
	@Catch(value = HandleRestException.class, writer = GenericResponseWriter.class) // catch globaly for whole root
	public String returnBang() {

		throw new IllegalArgumentException("Bang!");
	}
}
