package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import io.vertx.core.http.*;

/**
 *
 */
public class IllegalArgumentExceptionHandler implements ExceptionHandler<IllegalArgumentException> {

    @Override
    public void write(IllegalArgumentException result, HttpServerRequest request, HttpServerResponse response) {

        response.end("Huh this produced an error: '" + result.getMessage() + "'");
    }
}
