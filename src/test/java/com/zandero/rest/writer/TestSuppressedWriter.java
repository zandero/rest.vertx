package com.zandero.rest.writer;

import com.zandero.rest.annotation.SuppressCheck;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.http.*;

/**
 *
 */
@SuppressCheck
public class TestSuppressedWriter implements HttpResponseWriter<Dummy> {

    @Override
    public void write(Dummy result, HttpServerRequest request, HttpServerResponse response) {
        response.end(result.name);
    }
}
