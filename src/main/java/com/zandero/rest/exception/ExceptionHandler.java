package com.zandero.rest.exception;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public interface ExceptionHandler<T extends Throwable> {

	void handle(T cause, HttpResponseWriter<T> writer, RoutingContext context);

	/*default boolean handles(Class<? extends Throwable> clazz) {

		return clazz.isInstance(this.getClass().getTypeParameters());
	}*/
}
