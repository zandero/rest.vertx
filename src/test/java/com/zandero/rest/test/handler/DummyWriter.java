package com.zandero.rest.test.handler;

import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.*;

/**
 *
 */
public class DummyWriter implements HttpResponseWriter<Dummy> {

    @Override
    public void write(Dummy result, HttpServerRequest request, HttpServerResponse response) {
        System.out.println("produce result");

        if (result != null) {
            response.setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end("{\"name\": \"" + result.name + "\", \"value\": \"" + result.value + "\"}");
        } else {
            response.setStatusCode(204).end();
        }
    }
}
