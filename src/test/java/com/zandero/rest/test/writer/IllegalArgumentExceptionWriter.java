package com.zandero.rest.test.writer;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class IllegalArgumentExceptionWriter implements HttpResponseWriter<IllegalArgumentException> {

	@Override
	public void write(IllegalArgumentException result, HttpServerRequest request, HttpServerResponse response) {

		response.end("Huh this produced an error: '" + result.getMessage()  + "'");
	}
}
