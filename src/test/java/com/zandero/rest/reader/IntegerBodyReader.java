package com.zandero.rest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class IntegerBodyReader implements ValueReader<Integer> {

    @Override
    public Integer read(String value, Class<Integer> type, RoutingContext context) throws Throwable {
        return Integer.parseInt(value);
    }

    @Override
    public Integer read(String value, TypeReference<Integer> type, RoutingContext context) throws Throwable {
        return 0;
    }

    @Override
    public Integer read(String value, JavaType jt, RoutingContext context) {
        return 0;
    }
}
