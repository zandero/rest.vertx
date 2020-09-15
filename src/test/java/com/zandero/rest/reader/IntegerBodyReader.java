package com.zandero.rest.reader;

/**
 *
 */
public class IntegerBodyReader implements ValueReader<Integer> {

    @Override
    public Integer read(String value, Class<Integer> type) {

        return Integer.parseInt(value);
    }
}
