package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.test.exceptions.InheritedBaseException;
import io.vertx.core.http.*;

/**
 *
 */
public class InheritedBaseExceptionHandler implements ExceptionHandler<InheritedBaseException> {

    @Override
    public void write(InheritedBaseException result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        response.end(this.getClass().getSimpleName() + ": " + result.getMessage());
    }
}
