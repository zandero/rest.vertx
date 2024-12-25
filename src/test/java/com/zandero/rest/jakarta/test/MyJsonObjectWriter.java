package com.zandero.rest.jakarta.test;

import com.zandero.rest.writer.*;
import io.vertx.core.http.*;
import io.vertx.core.json.*;

public class MyJsonObjectWriter implements HttpResponseWriter<JsonObject> {

    @Override
    public void write(JsonObject result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        String value = result.getString("user");
        response.end(value);
    }
}
