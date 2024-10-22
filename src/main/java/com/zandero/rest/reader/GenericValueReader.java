package com.zandero.rest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.ClassFactoryException;

import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.Consumes;

/**
 *
 */
@Consumes("*/*")
public class GenericValueReader implements ValueReader<Object> {

    @Override
    public Object read(String value, Class<Object> type, RoutingContext context) {

        try {
            return ClassFactory.constructType(type, value);
        } catch (ClassFactoryException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Object read(String value, TypeReference<Object> type, RoutingContext context) throws Throwable
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object read(String value, JavaType jt, RoutingContext  context)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }


}
