package com.zandero.rest.annotation;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("CONNECT")
@Documented
public @interface CONNECT {

	String value() default ""; // @Path to connect if not given in @Path
}
