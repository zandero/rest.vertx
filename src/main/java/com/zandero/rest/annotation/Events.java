package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * List of events
 *
 * Example:
 *
 * Events({
 *  Event(processor = Processor.class),
 *  Event(processor = SecondProcessor.class)
 * })
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Events {

	/**
	 * Array of events to be triggered
	 * @return array of RestEvents
	 */
	Event[] value() default {};
}
