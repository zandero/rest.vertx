package com.zandero.resttest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.reader.ValueReader;
import com.zandero.resttest.test.data.Token;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class TokenReader implements ValueReader<Token> {

    @Override
    public Token read(String value, Class<Token> type, RoutingContext context) throws Throwable {
        return new Token(value);
    }

    @Override
    public Token read(String value, TypeReference<Token> type, RoutingContext context) throws Throwable {
        return null;
    }

    @Override
    public Token read(String value, JavaType jt, RoutingContext context) {
        return null;
    }
}
