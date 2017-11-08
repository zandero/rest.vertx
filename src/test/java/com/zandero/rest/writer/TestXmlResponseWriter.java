package com.zandero.rest.writer;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class TestXmlResponseWriter implements HttpResponseWriter<String> {

	@Override
	public void write(String result, HttpServerRequest request, HttpServerResponse response) {
		response.end("<xml>" + result + "</xml>");
	}
}
