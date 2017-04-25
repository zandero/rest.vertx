package com.zandero.rest.exception;

/**
 *
 */
public class ExecuteException extends Exception {

	private final int statusCode;

	public ExecuteException(int status, Throwable exception) {

		super(exception.getMessage(), exception);
		statusCode = status;
	}

	public ExecuteException(int status, String message) {

		super(message);
		statusCode = status;
	}

	public int getStatusCode() {

		return statusCode;
	}
}
