package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.test.exceptions.BaseException;
import io.vertx.core.http.*;

/**
 *
 */
public class BaseExceptionHandler implements ExceptionHandler<BaseException> {

    @Override
    public void write(BaseException result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        response.end(this.getClass().getSimpleName() + ": " + result.getMessage());
    }
}
