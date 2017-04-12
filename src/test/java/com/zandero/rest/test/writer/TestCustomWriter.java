package com.zandero.rest.test.writer;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class TestCustomWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerResponse response) {

		String out = null;
		if (result instanceof String) {
			out = (String) result;
		}
		else {
			out = result.toString();
		}

		response.end("<custom>" + out + "</custom>");
	}
}
