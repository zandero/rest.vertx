package com.zandero.resttest.writer;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.*;

import jakarta.ws.rs.Produces;

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
