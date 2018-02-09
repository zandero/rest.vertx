package com.zandero.rest.writer;

import com.zandero.rest.RestRouter;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ContextException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;

/**
 * Tries to find and utilize associated mime type / media type writer
 * If no writer found a generic Object.toString() write is triggered
 */
@SuppressWarnings("unchecked")
public class GenericResponseWriter<T> implements HttpResponseWriter<T> {

	private final static Logger log = LoggerFactory.getLogger(GenericResponseWriter.class);

	@Context
	RoutingContext context;

	@Context
	RouteDefinition definition;

	@Override
	public void write(T result, HttpServerRequest request, HttpServerResponse response) {

		String mediaType = response.headers().get(HttpHeaders.CONTENT_TYPE);

		HttpResponseWriter writer;
		try {
			writer = RestRouter.getWriters().get(mediaType, definition, context);
		}
		catch (ClassFactoryException | ContextException e) {
			// writer = RestRouter.getWriters().get(result);
			log.warn("Failed to provide GenericResponseWriter: ", e);
			writer = null;
		}

		if (writer != null && !(writer instanceof GenericResponseWriter)) {
			writer.write(result, request, response);
		}
		else {

			log.warn("No writer associated with: '" + mediaType + "', defaulting to toString() as output!");
			if (result != null) {
				response.end(result.toString());
			}
			else {
				response.end();
			}
		}
	}
}
