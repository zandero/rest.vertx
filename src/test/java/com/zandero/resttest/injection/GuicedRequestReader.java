package com.zandero.resttest.injection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.reader.ValueReader;
import com.zandero.resttest.test.json.Dummy;
import io.vertx.ext.web.RoutingContext;

import jakarta.inject.Inject;

/**
 *
 */
public class GuicedRequestReader implements ValueReader<Dummy> {

    @Inject
    OtherService other;

    @Override
    public Dummy read(String value, Class<Dummy> type, RoutingContext context) throws Throwable {
        return new Dummy(other.other(), value);
    }

    @Override
    public Dummy read(String value, TypeReference<Dummy> type, RoutingContext context) throws Throwable {
        return null;
    }

    @Override
    public Dummy read(String value, JavaType jt, RoutingContext context) {
        return null;
    }
}
