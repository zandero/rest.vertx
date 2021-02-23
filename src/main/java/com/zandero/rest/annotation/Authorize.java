package com.zandero.rest.annotation;

import io.vertx.ext.auth.authorization.AuthorizationProvider;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Authorize {

    Class<? extends AuthorizationProvider> value();
}
