package com.zandero.rest.reader;

import com.zandero.utils.JsonUtils;
import com.zandero.utils.StringUtils;

/**
 * Converts request body to JSON
 */
public class JsonBodyReader implements HttpRequestBodyReader {

	@Override
	public Object read(String value, Class<?> type) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return null;
		}

		return JsonUtils.fromJson(value, type);
	}
}
