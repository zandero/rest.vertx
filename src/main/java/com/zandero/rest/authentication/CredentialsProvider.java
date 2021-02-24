package com.zandero.rest.authentication;

import com.zandero.rest.context.ContextProvider;
import io.vertx.ext.auth.authentication.Credentials;

public interface CredentialsProvider extends ContextProvider<Credentials> {

}
