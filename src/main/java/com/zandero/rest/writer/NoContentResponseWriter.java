package com.zandero.rest.writer;

import io.vertx.core.http.*;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;

/**
 * Returns 204 response code
 */
public class NoContentResponseWriter implements HttpResponseWriter<Object> {

    @Override
    public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(NO_CONTENT.getStatusCode());
        response.end();
    }
}
