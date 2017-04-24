package com.zandero.rest.writer;

import com.zandero.rest.exception.ClassFactoryException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class GenericResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

		WriterFactory writerFactory = new WriterFactory();

		response.setStatusCode(200);

		String mediaType = response.headers().get(HttpHeaders.CONTENT_TYPE);

		HttpResponseWriter writer;
		try {
			writer = writerFactory.get(mediaType);
		}
		catch (ClassFactoryException e) {
			writer = null;
		}

		if (writer != null && !(writer instanceof GenericResponseWriter)) {
			writer.write(result, request, response);
		}
		else {
			response.end(result.toString());
		}
	}
}
