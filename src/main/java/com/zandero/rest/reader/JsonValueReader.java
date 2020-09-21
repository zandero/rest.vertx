package com.zandero.rest.reader;

import com.zandero.utils.StringUtils;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.json.jackson.DatabindCodec;

import javax.ws.rs.Consumes;

/**
 * Converts request body to JSON
 */
@Consumes("application/json")
public class JsonValueReader<T> implements ValueReader<T> {

    @Override
    public T read(String value, Class<T> type) {

        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return null;
        }

        return JsonUtils.fromJson(value, type, DatabindCodec.mapper());
    }
}
