package com.zandero.rest.reader;

/**
 *
 */
public class IntegerBodyReader implements HttpRequestBodyReader<Integer> {

	@Override
	public Integer read(String value, Class<Integer> type) {

		return Integer.parseInt(value);
	}
}
