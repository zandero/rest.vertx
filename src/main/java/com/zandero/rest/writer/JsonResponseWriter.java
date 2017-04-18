package com.zandero.rest.writer;

import com.zandero.utils.JsonUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class JsonResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

		response.end(JsonUtils.toJson(result));
	}
}
