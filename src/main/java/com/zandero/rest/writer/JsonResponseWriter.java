package com.zandero.rest.writer;

import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.http.*;
import io.vertx.core.json.jackson.DatabindCodec;

/**
 * Converts result into JSON object if not null
 */
// @Produces("application/json")
public class JsonResponseWriter<T> implements HttpResponseWriter<T> {

    // TODO: add custom mapper ... to override vertx.mapper if desired

    @Override
    public void write(T result, HttpServerRequest request, HttpServerResponse response) {

        if (result != null) {
            response.end(JsonUtils.toJson(result, DatabindCodec.mapper()));
        } else {
            response.end();
        }
    }
}
