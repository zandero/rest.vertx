package com.zandero.rest;

import com.zandero.rest.writer.NotFoundResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class NotFoundHandler extends NotFoundResponseWriter {

	@Override
	public void write(HttpServerRequest request, HttpServerResponse response) {

		response.end("404 HTTP Resource: '" + request.path() + "' not found!");
	}
}
