package com.zandero.rest.writer;

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

		if (!response.ended() &&
		    !response.headers().contains(HttpHeaders.CONTENT_TYPE)) {

			boolean addWildcardContentType = true;

			// add static headers if any
			Header headers = this.getClass().getAnnotation(Header.class);
			Map<String, String> nameValuePairs = getNameValuePairs(headers != null ? headers.value() : null);
			if (nameValuePairs != null && nameValuePairs.size() > 0) {
				response.headers().addAll(nameValuePairs);

				addWildcardContentType = !nameValuePairs.containsKey(HttpHeaders.CONTENT_TYPE.toString());
			}

			Produces writerProduces = this.getClass().getAnnotation(Produces.class);
			if (writerProduces != null && writerProduces.value().length > 0) {

				addWildcardContentType = false;
				for (String produces : writerProduces.value()) {
					response.putHeader(HttpHeaders.CONTENT_TYPE, produces);
				}
			} else {
				if (definition != null &&
				    definition.getProduces() != null) {

					addWildcardContentType = false;
					for (MediaType produces : definition.getProduces()) {
						response.putHeader(HttpHeaders.CONTENT_TYPE, MediaTypeHelper.toString(produces));
					}
				}
			}

			if (addWildcardContentType) {
				response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD);
			}
		}
	}

	default Map<String, String> getNameValuePairs(String[] values) {
		if (values == null || values.length == 0) {
			return null;
		}

		Map<String, String> output = new HashMap<>();
		for (String item : values) {

			// default if split point can not be found
			String name = item;
			String value = "";

			int idx = item.indexOf(":");
			if (idx <= 0) {
				idx = item.indexOf(" ");
			}

			if (idx > 0) {
				name = item.substring(0, idx).trim();
				value = item.substring(idx + 1).trim();
			}

			output.put(name, value);
		}

		return output;
	}
}
