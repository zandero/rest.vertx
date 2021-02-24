package com.zandero.rest.authorization;

import com.zandero.rest.authentication.CredentialsProvider;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.authentication.*;

public class MyCredentialProvider implements CredentialsProvider {

    @Override
    public Credentials provide(HttpServerRequest request) throws Throwable {
        String token = request.getHeader("X-Token");

        // read the token ...
        if (token != null) {
            return new TokenCredentials(token);
        }

        return null;
    }
}