package com.zandero.rest.exception;

/**
 * Exception thrown when class could not be instantiated
 */
public class ClassFactoryException extends Exception {

	public ClassFactoryException(String message, Throwable e) {
		super(message, e);
	}
}
