package com.zandero.rest.annotation;

import io.vertx.ext.auth.authentication.AuthenticationProvider;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Authenticate {

    Class<? extends AuthenticationProvider> value();
}
