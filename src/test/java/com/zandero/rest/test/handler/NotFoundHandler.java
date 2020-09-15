package com.zandero.rest.test.handler;

import com.zandero.rest.writer.NotFoundResponseWriter;
import io.vertx.core.http.*;

/**
 *
 */
public class NotFoundHandler extends NotFoundResponseWriter {

    @Override
    public void write(HttpServerRequest request, HttpServerResponse response) {

        response.end("404 HTTP Resource: '" + request.path() + "' not found!");
    }
}
