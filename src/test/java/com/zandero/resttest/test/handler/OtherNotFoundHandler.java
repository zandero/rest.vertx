package com.zandero.resttest.test.handler;

import com.zandero.rest.writer.NotFoundResponseWriter;
import io.vertx.core.http.*;

/**
 *
 */
public class OtherNotFoundHandler extends NotFoundResponseWriter {

    @Override
    public void write(HttpServerRequest request, HttpServerResponse response) {

        response.end("'" + request.path() + "' not found!");
    }
}
