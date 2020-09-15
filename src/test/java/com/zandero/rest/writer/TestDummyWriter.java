package com.zandero.rest.writer;

import com.zandero.rest.test.json.Dummy;
import io.vertx.core.http.*;

/**
 *
 */
public class TestDummyWriter implements HttpResponseWriter<Dummy> {

    @Override
    public void write(Dummy data, HttpServerRequest request, HttpServerResponse response) {

        String out = data.name + "=" + data.value;
        response.end("<custom>" + out + "</custom>");
    }
}
