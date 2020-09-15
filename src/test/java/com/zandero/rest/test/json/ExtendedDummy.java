package com.zandero.rest.test.json;

/**
 *
 */
public class ExtendedDummy extends Dummy {

    public ExtendedDummy() {
    }

    public ExtendedDummy(String name, String value, String type) {
        super(name, value);
        this.type = type;
    }

    public String type;
}
