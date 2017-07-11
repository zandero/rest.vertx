package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class HandleRestException implements ExceptionHandler<IllegalArgumentException> {

	@Override
	public void handle(IllegalArgumentException cause, HttpResponseWriter<IllegalArgumentException> writer, RoutingContext context) {

		writer.write(cause, context.request(), context.response());
	}
}
