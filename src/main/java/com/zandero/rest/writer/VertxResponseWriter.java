package com.zandero.rest.writer;

import io.vertx.core.http.*;

/**
 *
 */
public class VertxResponseWriter<T> implements HttpResponseWriter<T> {

    @Override
    public void write(T result, HttpServerRequest request, HttpServerResponse response) {

        // placeholder, nothing to do ...
    }
}
