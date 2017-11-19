package com.zandero.rest.exception;

/**
 * Wrapper to transport exception when executing a REST call
 */
public class ExecuteException extends Exception {

	/**
	 * Holds HTTP error code
	 */
	private final int statusCode;

	public ExecuteException(int status, Throwable exception) {

		super(exception.getMessage(), exception);
		statusCode = status;
	}

	public ExecuteException(int status, String message) {

		super(message);
		statusCode = status;
	}

	public ExecuteException(int status, String message, Throwable cause) {

		super(message, cause);
		statusCode = status;
	}

	public int getStatusCode() {

		return statusCode;
	}
}
