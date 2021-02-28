package com.zandero.rest.annotation;

import com.zandero.rest.authentication.RestAuthenticationProvider;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Authenticate {

    Class<? extends RestAuthenticationProvider> value();
}
