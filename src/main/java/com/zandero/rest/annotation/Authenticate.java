package com.zandero.rest.annotation;

import com.zandero.rest.authentication.CredentialsProvider;
import io.vertx.ext.auth.authentication.AuthenticationProvider;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Authenticate {

    Class<? extends AuthenticationProvider> auth();

    Class<? extends CredentialsProvider> with();
}
