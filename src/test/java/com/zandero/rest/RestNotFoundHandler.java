package com.zandero.rest;

import com.zandero.rest.writer.NotFoundResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class RestNotFoundHandler extends NotFoundResponseWriter {

	@Override
	public void write(HttpServerRequest request, HttpServerResponse response) {

		response.end("REST endpoint: '" + request.path() + "' not found!");
	}
}
