package com.zandero.rest.writer;

import com.zandero.utils.JsonUtils;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class JsonResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerResponse response) {

		response.end(JsonUtils.toJson(result));
	}
}
