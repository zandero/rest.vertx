package com.zandero.rest.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.utils.StringUtils;
import com.zandero.rest.utils.extra.JsonUtils;
import io.vertx.core.json.jackson.DatabindCodec;

import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.Consumes;

/**
 * Converts request body to JSON
 */
@Consumes("application/json")
public class JsonValueReader<T> implements ValueReader<T> {

    @Override
    public T read(String value, Class<T> type, RoutingContext context) {

        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return null;
        }

        return JsonUtils.fromJson(value, type, DatabindCodec.mapper());
    }

    @Override
    public T read(String value, TypeReference<T> type, RoutingContext  context) throws Throwable
    {
        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return null;
        }
        return JsonUtils.fromJson(value, type);
    }

    @Override
    public T read(String value, JavaType jt, RoutingContext context)
    {
        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return null;
        }
        try
        {
            return JsonUtils.getObjectMapper().readValue(value, jt);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }


}
