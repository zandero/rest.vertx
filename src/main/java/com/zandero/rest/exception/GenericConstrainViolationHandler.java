package com.zandero.rest.exception;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import javax.validation.ConstraintViolationException;

/**
 *
 */

public class GenericConstrainViolationHandler implements ExceptionHandler<ConstraintViolationException> {

	@Override
	public void write(ConstraintViolationException result, HttpServerRequest request, HttpServerResponse response) {

		response.setStatusCode(400); // to be discussed ... 400 or should use 422 instead?
		response.putHeader("X-Status-Reason", "Validation failed");
		response.end(result.getMessage());
	}
}
