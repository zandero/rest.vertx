package com.zandero.rest.test.exceptions;

/**
 *
 */
public class BaseException extends Throwable {

    public BaseException(String message) {
        super("BASE: " + message);
    }
}
