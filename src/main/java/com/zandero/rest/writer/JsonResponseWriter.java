package com.zandero.rest.writer;

import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Converts result into JSON object if not null
 */
public class JsonResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

		if (result != null) {
			response.end(JsonUtils.toJson(result));
		}
		else {
			response.end();
		}
	}
}
