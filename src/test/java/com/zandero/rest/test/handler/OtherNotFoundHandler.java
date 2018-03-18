package com.zandero.rest.test.handler;

import com.zandero.rest.writer.NotFoundResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class OtherNotFoundHandler extends NotFoundResponseWriter {

	@Override
	public void write(HttpServerRequest request, HttpServerResponse response) {

		response.end("'" + request.path() + "' not found!");
	}
}
