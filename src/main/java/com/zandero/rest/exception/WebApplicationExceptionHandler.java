package com.zandero.rest.exception;

import io.vertx.core.http.*;

import javax.ws.rs.WebApplicationException;

/**
 *
 */
public class WebApplicationExceptionHandler implements ExceptionHandler<WebApplicationException> {

    @Override
    public void write(WebApplicationException result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(result.getResponse().getStatus());
        response.end(result.getMessage());
    }
}
