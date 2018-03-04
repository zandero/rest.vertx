package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public class MyExceptionHandler implements ExceptionHandler<MyExceptionClass> {

	@Override
	public void write(MyExceptionClass result, HttpServerRequest request, HttpServerResponse response) {

		response.end("Exception: " + result.getMessage());
	}
}
