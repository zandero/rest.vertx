package com.zandero.rest.writer;

import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class GenericResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerResponse response) {

		response.setStatusCode(200);
		response.end(result.toString());
	}
}
