package com.zandero.rest.reader;

/**
 * Request body reader interface to implement
 * to read request body and converting request body to given object type
 *
 * Use RestRouter.getReaders().register(...) to register a global reader
 * or use @RequestReader annotation to associate REST with given reader
 */
public interface HttpRequestBodyReader {

	Object read(String value, Class<?> type);
}
