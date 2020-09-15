package com.zandero.rest.reader;

/**
 *
 */
public class NpeReader implements ValueReader<String> {
    @Override
    public String read(String value, Class<String> type) {
        throw new NullPointerException("OH SHIT!");
    }
}
