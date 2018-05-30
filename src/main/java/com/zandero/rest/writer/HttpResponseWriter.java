package com.zandero.rest.writer;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.Header;
import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.RouteDefinition;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Response writer interface to implement
 * use RestRouter.getWriters().register(...) to register a global writer
 * or use @ResponseWriter annotation to associate REST with given writer
 */
public interface HttpResponseWriter<T> {

	void write(T result, HttpServerRequest request, HttpServerResponse response) throws Throwable;

	default void addResponseHeaders(RouteDefinition definition, HttpServerResponse response) {

		if (!response.ended()) {

			Map<String, String> headers = new HashMap<>();

			// collect all headers to put into response ...

			// 1. add definition headers
			headers = join(headers, definition.getHeaders());

			// 2. add REST produces
			headers = join(headers, definition.getProduces());

			// 3. add writer headers
			Header writerHeader = this.getClass().getAnnotation(Header.class);
			if (writerHeader != null && writerHeader.value().length > 0) {
				headers = join(headers, AnnotationProcessor.getNameValuePairs(writerHeader.value()));
			}

			// add Writer produces
			Produces writerProduces = this.getClass().getAnnotation(Produces.class);
			if (writerProduces != null && writerProduces.value().length > 0) {
				headers = join(headers, MediaTypeHelper.getMediaTypes(writerProduces.value()));
			}

			// add wildcard if no content-type present
			if (!headers.containsKey(HttpHeaders.CONTENT_TYPE.toString())) {
				headers.put(HttpHeaders.CONTENT_TYPE.toString(), MediaType.WILDCARD);
			}

			// add all headers not present in response
			for (String name : headers.keySet()) {
				if (!response.headers().contains(name)) {
					response.headers().add(name, headers.get(name));
				}
			}
		}
	}

	default Map<String, String> join(Map<String, String> original, Map<String, String> additional) {
		if (additional != null && additional.size() > 0) {
			original.putAll(additional);
		}

		return original;
	}

	default Map<String, String> join(Map<String, String> original, MediaType[] additional) {
		if (additional != null && additional.length > 0) {
			for (MediaType produces : additional) {
				original.put(HttpHeaders.CONTENT_TYPE.toString(), MediaTypeHelper.toString(produces));
			}
		}

		return original;
	}
}
