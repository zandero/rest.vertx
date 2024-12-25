package com.zandero.rest.exception;

import com.zandero.rest.annotation.*;
import io.vertx.core.http.*;

/**
 *
 */
@Header("X-Status-Reason: Validation failed")
public class ValidationConstraintExceptionHandler implements ExceptionHandler<ValidationConstraintException> {

    @Override
    public void write(ValidationConstraintException result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(400); // to be discussed ... 400 or should use 422 instead?
        response.end(result.getMessage());
    }
}
