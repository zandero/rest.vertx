package com.zandero.rest.test.writer;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class ExceptionWriter implements HttpResponseWriter<Throwable> {

	@Override
	public void write(Throwable result, HttpServerRequest request, HttpServerResponse response) {

		response.end("Exception: " + result.getMessage());
	}
}
