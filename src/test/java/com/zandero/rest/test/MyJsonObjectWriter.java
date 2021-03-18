package com.zandero.rest.test;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;

public class MyJsonObjectWriter implements HttpResponseWriter<JsonObject> {

    @Override
    public void write(JsonObject result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        String value = result.getString("user");
        response.end(value);
    }
}
