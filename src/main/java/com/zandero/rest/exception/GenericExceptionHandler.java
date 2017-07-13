package com.zandero.rest.exception;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

/**
 * Generic failure handler ...
 * writes out exception message as it is
 */
public class GenericExceptionHandler implements ExceptionHandler<Throwable> {

	@Override
	public void handle(Throwable cause, HttpResponseWriter writer, RoutingContext context) {

		// TODO: use cause only
		writer.write(cause.getMessage(), context.request(), context.response());
	}
}
