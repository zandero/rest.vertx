package com.zandero.rest.authentication;

import com.zandero.rest.context.ContextProvider;
import io.vertx.ext.auth.authentication.Credentials;

/**
 * Must be implemented in order to provide Credentials for Authentication
 */
public interface CredentialsProvider extends ContextProvider<Credentials> {

}
