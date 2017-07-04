package com.zandero.rest.exception;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.WebApplicationException;

/**
 *
 */
public class WebApplicationExceptionHandler implements ExceptionHandler {

	@Override
	public void handle(Throwable cause, HttpResponseWriter writer, RoutingContext context) {

		if (cause instanceof WebApplicationException) {

			WebApplicationException exception = (WebApplicationException)cause;

			context.response().setStatusCode(exception.getResponse().getStatus());
			writer.write(exception.getMessage(), context.request(), context.response());
		}
	}
}
