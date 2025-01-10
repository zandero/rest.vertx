package com.zandero.rest.writer;

import io.netty.handler.codec.http.*;
import io.vertx.core.buffer.*;
import io.vertx.core.http.*;

/**
 * Serves binary data as response
 */
public class BinaryResponseWriter implements HttpResponseWriter<byte[]> {

    @Override
    public void write(byte[] result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        if (result == null) {
            response.setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
            return;
        }

        response.send(Buffer.buffer(result));
    }
}
