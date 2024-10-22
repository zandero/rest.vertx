package com.zandero.resttest.test.data;

import com.zandero.rest.context.ContextProvider;
import com.zandero.resttest.test.json.Dummy;
import io.vertx.core.http.HttpServerRequest;

/**
 *
 */
public class DummyProvider implements ContextProvider<Dummy> {

    @Override
    public Dummy provide(HttpServerRequest request) throws Throwable {
        return new Dummy(request.getHeader("X-dummy-name"), request.getHeader("X-dummy-value"));
    }
}
