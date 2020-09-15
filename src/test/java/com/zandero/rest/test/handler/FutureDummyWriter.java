package com.zandero.rest.test.handler;

import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.*;

import java.util.concurrent.*;

/**
 *
 */
public class FutureDummyWriter implements HttpResponseWriter<CompletableFuture<Dummy>> {

    @Override
    public void write(CompletableFuture<Dummy> result, HttpServerRequest request, HttpServerResponse response) throws ExecutionException, InterruptedException {
        System.out.println("produce result");

        if (result != null) {
            Dummy dummy = result.get(); // we wait to get the result

            response.setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end("{\"name\": \"" + dummy.name + "\", \"value\": \"" + dummy.value + "\"}");
        } else {
            response.setStatusCode(204).end();
        }
    }
}
