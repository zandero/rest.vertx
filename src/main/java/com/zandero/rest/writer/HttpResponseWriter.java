package com.zandero.rest.writer;

import com.zandero.rest.data.RouteDefinition;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public interface HttpResponseWriter {

	void write(Object result, HttpServerRequest request, HttpServerResponse response);

	default void addResponseHeaders(RouteDefinition definition, HttpServerResponse response) {

		if (!response.headers().contains(HttpHeaders.CONTENT_TYPE)) {

			if (definition.getProduces() != null) {
				for (MediaType produces : definition.getProduces()) {
					response.putHeader(HttpHeaders.CONTENT_TYPE, produces.toString());
				}
			}
			else {
				response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD);
			}
		}
	}
}
