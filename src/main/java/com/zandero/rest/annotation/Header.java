package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * To be used in combination with response writers to provide static response headers
 * Can be also used on REST APIs as an alternative to @Produces or to complement @Produces annotation
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {

	/**
	 * Name value header pair to be added to output
	 * Can be applied to RESTs or response writers
	 *
	 * example: "X-Auth: test", or "X-Auth test" ... will add a "X-Auth" header with value "test"
	 * @return one or more headers ...
	 */
	String[] value() default "";
}
