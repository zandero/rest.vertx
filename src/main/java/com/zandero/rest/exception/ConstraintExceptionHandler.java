package com.zandero.rest.exception;

import com.zandero.rest.annotation.Header;
import io.vertx.core.http.*;

/**
 *
 */
@Header("X-Status-Reason: Validation failed")
public class ConstraintExceptionHandler implements ExceptionHandler<ConstraintException> {

    @Override
    public void write(ConstraintException result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(400); // to be discussed ... 400 or should use 422 instead?
        response.end(result.getMessage());
    }
}
