package com.zandero.rest.exception;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public interface ExceptionHandler {

	void handle(Throwable cause, HttpResponseWriter writer, RoutingContext context);
}
