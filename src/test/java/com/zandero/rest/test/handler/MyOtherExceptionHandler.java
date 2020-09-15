package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import io.vertx.core.http.*;

/**
 *
 */
public class MyOtherExceptionHandler implements ExceptionHandler<Throwable> {

    @Override
    public void write(Throwable result, HttpServerRequest request, HttpServerResponse response) {

        response.end("Exception: " + result.getMessage());
    }
}
