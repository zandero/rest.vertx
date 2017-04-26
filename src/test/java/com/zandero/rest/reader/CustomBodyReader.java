package com.zandero.rest.reader;

import com.zandero.utils.StringUtils;

import java.util.Collections;

/**
 * Produces list of String as output
 */
public class CustomBodyReader implements HttpRequestBodyReader {

	@Override
	public Object read(String value, Class<?> type) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return Collections.emptyList();
		}

		// extract words from value ... and return list
		return StringUtils.getWords(value);
	}
}
