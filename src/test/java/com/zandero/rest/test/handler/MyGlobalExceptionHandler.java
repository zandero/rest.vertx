package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import io.vertx.core.http.*;

/**
 *
 */
public class MyGlobalExceptionHandler implements ExceptionHandler<Throwable> {

    private final String name;

    public MyGlobalExceptionHandler(String name) {
        this.name = name;
    }

    @Override
    public void write(Throwable result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(400);
        response.end(name + " " + result.getMessage());
    }
}
