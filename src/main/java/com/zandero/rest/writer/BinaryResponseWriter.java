package com.zandero.rest.writer;

import io.vertx.core.buffer.*;
import io.vertx.core.http.*;

/**
 * Writes binary data to response, takes byte[] as input
 * Example Usecase: Downloading PDF
 * TODO: Improve
 */
public class BinaryResponseWriter<T> implements HttpResponseWriter<T> {
    @Override
    public void write(T result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        if (result == null) {
            response.setStatusCode(204).end(); // No Content
            return;
        }

        if (result instanceof byte[]) {
            response.end(Buffer.buffer((byte[]) result));
        } else {
            response.setStatusCode(500).end("Unsupported file type");
        }
    }
}
