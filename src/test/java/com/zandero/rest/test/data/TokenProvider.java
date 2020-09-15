package com.zandero.rest.test.data;

import com.zandero.rest.context.ContextProvider;
import io.vertx.core.http.HttpServerRequest;

/**
 *
 */
public class TokenProvider implements ContextProvider<Token> {

    @Override
    public Token provide(HttpServerRequest request) {
        String token = request.getHeader("X-Token");
        if (token != null) {
            return new Token(token);
        }

        return null;
    }
}
