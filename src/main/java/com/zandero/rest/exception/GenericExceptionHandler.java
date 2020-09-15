package com.zandero.rest.exception;

import com.zandero.utils.StringUtils;
import io.vertx.core.http.*;

/**
 *
 */
public class GenericExceptionHandler implements ExceptionHandler<Throwable> {

    @Override
    public void write(Throwable exception, HttpServerRequest request, HttpServerResponse response) {

        String message = exception.getMessage();
        if (StringUtils.isNullOrEmptyTrimmed(message)) {
            response.end(exception.toString());
        } else {
            response.end(message);
        }
    }
}
