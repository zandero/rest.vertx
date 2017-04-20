package com.zandero.rest.writer;

import com.zandero.rest.RestRouter;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class GenericResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

		response.setStatusCode(200);

		String mediaType = response.headers().get(HttpHeaders.CONTENT_TYPE);
		HttpResponseWriter writer = RestRouter.getResponseWriterInstance(mediaType);

		if (writer != null && !(writer instanceof GenericResponseWriter)) {
			writer.write(result, request, response);
		}
		else {
			response.end(result.toString());
		}
	}
}