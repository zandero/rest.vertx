package com.zandero.rest.reader;

import com.zandero.utils.StringUtils;
import com.zandero.utils.extra.JsonUtils;

/**
 * Converts request body to JSON
 */
public class JsonBodyReader<T> implements HttpRequestBodyReader<T> {

	@Override
	public T read(String value, Class<T> type) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return null;
		}

		return JsonUtils.fromJson(value, type, io.vertx.core.json.Json.mapper);
	}
}
