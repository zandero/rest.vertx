package com.zandero.rest.test.exceptions;

/**
 *
 */
public class InheritedFromInheritedException extends InheritedBaseException {

    public InheritedFromInheritedException(String message) {
        super("INHERITED FROM: " + message);
    }
}
