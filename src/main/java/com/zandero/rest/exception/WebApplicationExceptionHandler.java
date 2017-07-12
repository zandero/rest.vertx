package com.zandero.rest.exception;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.WebApplicationException;

/**
 *
 */
public class WebApplicationExceptionHandler implements ExceptionHandler<WebApplicationException> {

	@Override
	public void handle(WebApplicationException cause, HttpResponseWriter writer, RoutingContext context) {

		context.response().setStatusCode(cause.getResponse().getStatus());
		writer.write(cause, context.request(), context.response());
	}
}
