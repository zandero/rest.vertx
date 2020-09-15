package com.zandero.rest.reader;

import com.zandero.rest.test.data.Token;

/**
 *
 */
public class TokenReader implements ValueReader<Token> {

    @Override
    public Token read(String value, Class<Token> type) {
        return new Token(value);
    }
}
