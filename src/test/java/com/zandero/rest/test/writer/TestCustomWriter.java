package com.zandero.rest.test.writer;

import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class TestCustomWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

		String out;
		if (result instanceof String) {
			out = (String) result;
		}
		else if (result instanceof Dummy) {
			Dummy data = (Dummy) result;
			out = data.name + "=" + data.value;
		}
		else {
			out = result.toString();
		}

		response.end("<custom>" + out + "</custom>");
	}
}
