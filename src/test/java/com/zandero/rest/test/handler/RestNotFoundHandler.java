package com.zandero.rest.test.handler;

import com.zandero.rest.writer.NotFoundResponseWriter;
import io.vertx.core.http.*;

/**
 *
 */
public class RestNotFoundHandler extends NotFoundResponseWriter {

    @Override
    public void write(HttpServerRequest request, HttpServerResponse response) {

        response.end("REST endpoint: '" + request.path() + "' not found!");
    }
}
