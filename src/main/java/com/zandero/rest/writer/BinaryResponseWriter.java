package com.zandero.rest.writer;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Writes binary data to response, takes byte[] as input
 * Example Usecase: Downloading PDF
 *
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
