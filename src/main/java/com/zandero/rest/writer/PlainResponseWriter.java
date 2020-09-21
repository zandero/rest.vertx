package com.zandero.rest.writer;

import io.vertx.core.http.*;
import org.slf4j.*;

/**
 * Returns toString() output
 *
 * @param <T> provided response type
 */
// @Produces("html/text")
public class PlainResponseWriter<T> implements HttpResponseWriter<T> {

    private final static Logger log = LoggerFactory.getLogger(PlainResponseWriter.class);

    @Override
    public void write(T result, HttpServerRequest request, HttpServerResponse response) {

        String mediaType = response.headers().get(HttpHeaders.CONTENT_TYPE);
        log.warn(request.path() + " - no writer for 'content-type'='" + mediaType + "', defaulting to toString() as output!");
        if (result != null) {
            response.end(result.toString());
        } else {
            response.end();
        }
    }
}
