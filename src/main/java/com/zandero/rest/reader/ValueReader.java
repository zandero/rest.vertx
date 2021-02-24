package com.zandero.rest.reader;

/**
 * Request body reader interface
 * implement to read request body and converting request body to a given object type
 *
 * Use RestRouter.getReaders().register(...) to register a global reader
 * or use @RequestReader annotation to associate REST with given reader
 */
public interface ValueReader<T> {

	T read(String value, Class<T> type) throws Throwable;
}
