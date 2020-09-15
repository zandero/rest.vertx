package com.zandero.rest.injection;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.*;

import javax.inject.Inject;

/**
 *
 */
public class GuicedResponseWriter implements HttpResponseWriter {

    @Inject
    DummyService dummyService;

    @Override
    public void write(Object result, HttpServerRequest request, HttpServerResponse response) {

        response.end(result + "=" + dummyService.get());
    }
}
