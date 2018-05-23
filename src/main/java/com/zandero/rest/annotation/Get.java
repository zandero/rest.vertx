package com.zandero.rest.annotation;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("GET")
@Documented
public @interface Get {

	String value() default ""; // @Path to trace if not given in @Path

	String[] produces() default "*/*";

	String[] consumes() default "*/*";
}
