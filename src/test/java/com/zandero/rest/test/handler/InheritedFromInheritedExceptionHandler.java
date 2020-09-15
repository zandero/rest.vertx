package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.test.exceptions.InheritedFromInheritedException;
import io.vertx.core.http.*;

/**
 *
 */
public class InheritedFromInheritedExceptionHandler implements ExceptionHandler<InheritedFromInheritedException> {

    @Override
    public void write(InheritedFromInheritedException result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        response.end(this.getClass().getSimpleName() + ": " + result.getMessage());
    }
}
