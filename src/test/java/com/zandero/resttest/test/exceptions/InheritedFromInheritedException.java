package com.zandero.resttest.test.exceptions;

/**
 *
 */
public class InheritedFromInheritedException extends InheritedBaseException {

    public InheritedFromInheritedException(String message) {
        super("INHERITED FROM: " + message);
    }
}
