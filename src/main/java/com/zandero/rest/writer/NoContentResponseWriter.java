package com.zandero.rest.writer;

import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class NoContentResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerResponse response) {

		response.setStatusCode(204);
	}
}
