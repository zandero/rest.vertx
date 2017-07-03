package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class UnhandledRestErrorHandler implements ExceptionHandler {

	@Override
	public void handle(Throwable cause, HttpResponseWriter writer, RoutingContext context) {

		context.response().setStatusCode(406);

		ErrorJSON error = new ErrorJSON();
		error.code = context.response().getStatusCode();
		error.message = cause.getMessage();

		writer.write(error, context.request(), context.response());
	}
}
