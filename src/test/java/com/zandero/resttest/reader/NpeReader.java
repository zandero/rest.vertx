package com.zandero.resttest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.reader.ValueReader;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class NpeReader implements ValueReader<String> {

    @Override
    public String read(String value, Class<String> type, RoutingContext context) throws Throwable {
        throw new NullPointerException("OH SHIT!");
    }

    @Override
    public String read(String value, TypeReference<String> type, RoutingContext context) throws Throwable {
        return "";
    }

    @Override
    public String read(String value, JavaType jt, RoutingContext context) {
        return "";
    }
}
