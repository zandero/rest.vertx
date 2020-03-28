package com.zandero.rest.writer;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns toString() output
 *
 * @param <T> provided response type
 */
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
