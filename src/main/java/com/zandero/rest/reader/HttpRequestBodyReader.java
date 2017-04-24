package com.zandero.rest.reader;

/**
 * Intended for reading request body and converting request body to given object type
 */
public interface HttpRequestBodyReader {

	Object read(String value, Class<?> type);
}
