package com.zandero.rest.test;

import com.zandero.rest.annotation.CatchWith;
import com.zandero.rest.exception.WebApplicationExceptionHandler;
import com.zandero.rest.test.handler.HandleRestException;
import com.zandero.rest.test.handler.UnhandledRestErrorHandler;
import com.zandero.rest.test.writer.ExceptionWriter;
import com.zandero.rest.test.writer.IllegalArgumentExceptionWriter;
import com.zandero.rest.writer.GenericResponseWriter;
import com.zandero.rest.writer.JsonResponseWriter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/throw")
@CatchWith(value = UnhandledRestErrorHandler.class, writer = JsonResponseWriter.class) // catch globally for whole root
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
	@CatchWith(value = HandleRestException.class, writer = GenericResponseWriter.class)
	public String returnBang() {

		throw new IllegalArgumentException("Bang!");
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("multi/{bang}")
	@CatchWith(value = {HandleRestException.class, WebApplicationExceptionHandler.class},
	           writer = {IllegalArgumentExceptionWriter.class, ExceptionWriter.class})
	public String returnMultiBang(@PathParam("bang") String bang) {

		switch (bang) {
			case "one":
				throw new NotAllowedException("Not for you!");

			case "two":
			default:
				throw new IllegalArgumentException("Bang!");
		}
	}

}
