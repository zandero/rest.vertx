package com.zandero.rest.events;

/**
 * This is a placeholder exception for RestEvent default exception annotation
 * this exception should never be triggered as it is considered a NOOP exception
 */
public class NoRestException extends Exception {

	private NoRestException() {

		super("Exception place holder!");
	}
}
