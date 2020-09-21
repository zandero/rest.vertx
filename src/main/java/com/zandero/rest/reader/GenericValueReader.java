package com.zandero.rest.reader;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.ClassFactoryException;

import javax.ws.rs.Consumes;

/**
 *
 */
@Consumes("*/*")
public class GenericValueReader implements ValueReader<Object> {

    @Override
    public Object read(String value, Class<Object> type) {

        try {
            return ClassFactory.constructType(type, value);
        } catch (ClassFactoryException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
