package com.zandero.resttest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import io.vertx.core.http.*;
import io.vertx.ext.web.RoutingContext;

import jakarta.ws.rs.core.Context;

/**
 *
 */
public class ContextExceptionHandler implements ExceptionHandler<IllegalArgumentException> {

    @Context
    RoutingContext context;

    @Override
    public void write(IllegalArgumentException result, HttpServerRequest request, HttpServerResponse response) {

        // only for context test purposes
        response.end("Failed on: " + context.request().path() + " " + result.getMessage());
    }
}
