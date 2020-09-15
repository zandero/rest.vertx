package com.zandero.rest.writer;

import io.vertx.core.http.*;

/**
 *
 */
public class TestCustomWriter implements HttpResponseWriter<String> {

    @Override
    public void write(String result, HttpServerRequest request, HttpServerResponse response) {

        response.end("<custom>" + result + "</custom>");
    }
}
