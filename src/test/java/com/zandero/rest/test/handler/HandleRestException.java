package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class HandleRestException implements ExceptionHandler {
	@Override
	public void handle(Throwable cause, HttpResponseWriter writer, RoutingContext context) {

		writer.write("Huh this produced an error: '" + cause.getMessage() + "'", context.request(), context.response());
	}
}
