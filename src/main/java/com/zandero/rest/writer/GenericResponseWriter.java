package com.zandero.rest.writer;

import com.zandero.rest.RestRouter;
import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.*;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.core.*;

/**
 * Tries to find and utilize associated mime type / media type writer
 * If no writer found a generic Object.toString() write is triggered
 */
@SuppressWarnings("unchecked")
public class GenericResponseWriter<T> implements HttpResponseWriter<T> {

    private final static Logger log = LoggerFactory.getLogger(GenericResponseWriter.class);

    @Context
    RoutingContext context;

    @Override
    public void write(T result, HttpServerRequest request, HttpServerResponse response) throws Throwable {

        // Content-Type header is filled by HttpResponseWriter.addResponseHeaders() call
        String mediaType = response.headers().get(HttpHeaders.CONTENT_TYPE);
        if (mediaType == null) {
            log.trace("No Content-Type selected, defaulting to Content-Type='*/*'");
            mediaType = MediaType.WILDCARD;
        }

        HttpResponseWriter writer;
        try {
            writer = (HttpResponseWriter) ClassFactory.get(mediaType, RestRouter.getWriters(), context);
        } catch (ClassFactoryException | ContextException e) {
            // writer = RestRouter.getWriters().get(result);
            log.warn("Failed to provide GenericResponseWriter: ", e);
            writer = null;
        }

        if (writer != null && !(writer instanceof GenericResponseWriter)) {
            writer.write(result, request, response);
        } else {
            log.warn(request.path() + " - no writer for 'content-type'='" + mediaType + "', defaulting to toString() as output!");
            MediaType type = MediaTypeHelper.valueOf(mediaType);
            if (type != null) {
                // TODO ... correct response headers should already be set according to ACCEPT header
                response.headers().remove(HttpHeaders.CONTENT_TYPE.toString());
                response.headers().add(HttpHeaders.CONTENT_TYPE.toString(), MediaTypeHelper.toString(type));
            }

            if (result != null) {
                response.end(result.toString());
            } else {
                response.end();
            }
        }
    }
}
