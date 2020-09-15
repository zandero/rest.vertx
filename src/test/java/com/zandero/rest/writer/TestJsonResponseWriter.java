package com.zandero.rest.writer;

import io.vertx.core.http.*;

import javax.ws.rs.Produces;

/**
 *
 */
@Produces("application/json")
public class TestJsonResponseWriter implements HttpResponseWriter<String> {

    @Override
    public void write(String result, HttpServerRequest request, HttpServerResponse response) {

        response.end("{\"text\": \"" + result + "\"}");
    }
}
