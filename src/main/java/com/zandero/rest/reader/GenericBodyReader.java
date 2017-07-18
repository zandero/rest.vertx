package com.zandero.rest.reader;

import com.zandero.rest.data.ClassFactory;

/**
 *
 */
public class GenericBodyReader implements HttpRequestBodyReader<Object> {

    @Override
    public Object read(String value, Class<Object> type) {

        if (type.isPrimitive()) {
            Object out = ClassFactory.stringToPrimitiveType(value, type);
            return out == null ? value : out;
        }

        return value;
    }
}
