package com.zandero.rest.writer;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Returns 204 response code
 */
public class NoContentResponseWriter implements HttpResponseWriter<Object> {

	@Override
	public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

		response.setStatusCode(204);
	}
}
