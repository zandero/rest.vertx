package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import io.vertx.core.http.*;

/**
 *
 */
public class MyExceptionHandler implements ExceptionHandler<MyExceptionClass> {

    @Override
    public void write(MyExceptionClass result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(result.getStatus());
        response.end("Exception: " + result.getError());
    }
}
