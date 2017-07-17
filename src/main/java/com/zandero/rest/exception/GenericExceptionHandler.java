package com.zandero.rest.exception;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class GenericExceptionHandler implements ExceptionHandler<Throwable> {

	@Override
	public void write(Throwable exception, HttpServerRequest request, HttpServerResponse response) {

		response.end(exception.getMessage());
	}
}
