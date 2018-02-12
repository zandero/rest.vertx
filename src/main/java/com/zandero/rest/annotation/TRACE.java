package com.zandero.rest.annotation;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("TRACE")
@Documented
public @interface TRACE {

	String value() default ""; // @Path to trace if not given in @Path
}
