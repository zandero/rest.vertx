package com.zandero.rest.annotation;

import com.zandero.rest.authorization.RoleBasedUserAuthorizationProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;

import java.lang.annotation.*;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Authorize {

    Class<? extends AuthorizationProvider> value() default RoleBasedUserAuthorizationProvider.class;

    /**
     * Optional, mainly for back compatibility purposes to provide roles to RoleBasedAuthorizationProvider
     * @return list of allowed roles
     */
    String[] role() default "";
}
