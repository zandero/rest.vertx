package com.zandero.rest.writer;

import io.vertx.core.http.*;

import javax.ws.rs.core.Response;

/**
 * Generic not found response writer for last() route
 */
public abstract class NotFoundResponseWriter implements HttpResponseWriter<Void> {

    @Override
    public void write(Void result, HttpServerRequest request, HttpServerResponse response) {

        // pre-fill 404 for convenience
        response.setStatusCode(Response.Status.NOT_FOUND.getStatusCode());

        // wrapped call to simplify implementation
        write(request, response);
    }

    /**
     * 404 response to be implemented
     *
     * @param request  that could not be served
     * @param response to fill up
     */
    public abstract void write(HttpServerRequest request, HttpServerResponse response);
}
