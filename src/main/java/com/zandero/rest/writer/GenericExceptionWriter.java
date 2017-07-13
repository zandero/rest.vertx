package com.zandero.rest.writer;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class GenericExceptionWriter implements HttpResponseWriter<Throwable> {

	@Override
	public void write(Throwable exception, HttpServerRequest request, HttpServerResponse response) {

		response.end(exception.getMessage());
	}
}
