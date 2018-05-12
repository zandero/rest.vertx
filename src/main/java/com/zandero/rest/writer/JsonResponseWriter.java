package com.zandero.rest.writer;

import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Converts result into JSON object if not null
 */
public class JsonResponseWriter<T> implements HttpResponseWriter<T> {

	// TODO: add custom mapper ... to override vertx.mapper if desired

	@Override
	public void write(T result, HttpServerRequest request, HttpServerResponse response) {

		if (result != null) {
			response.end(JsonUtils.toJson(result, io.vertx.core.json.Json.mapper));
		}
		else {
			response.end();
		}
	}
}
