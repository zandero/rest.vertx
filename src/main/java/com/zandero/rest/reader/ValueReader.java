package com.zandero.rest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import io.vertx.ext.web.RoutingContext;

/**
 * Request body reader interface
 * implement to read request body and converting request body to a given object type
 *
 * Use RestRouter.getReaders().register(...) to register a global reader
 * or use @RequestReader annotation to associate REST with given reader
 */
public interface ValueReader<T> {

	T read(String value, Class<T> type, RoutingContext context) throws Throwable;

	T read(String value, TypeReference<T> type, RoutingContext context) throws Throwable;

	T read(String value, JavaType jt, RoutingContext  context);

}
