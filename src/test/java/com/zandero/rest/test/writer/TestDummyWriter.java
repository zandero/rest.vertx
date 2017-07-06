package com.zandero.rest.test.writer;

import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class TestDummyWriter implements HttpResponseWriter<Dummy> {

	@Override
	public void write(Dummy data, HttpServerRequest request, HttpServerResponse response) {

		String out = data.name + "=" + data.value;
		response.end("<custom>" + out + "</custom>");
	}
}
