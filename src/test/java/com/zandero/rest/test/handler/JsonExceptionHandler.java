package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.http.*;

/**
 *
 */
public class JsonExceptionHandler implements ExceptionHandler<Throwable> {

    @Override
    public void write(Throwable exception, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(406);

        ErrorJSON error = new ErrorJSON();
        error.code = response.getStatusCode();
        error.message = exception.getMessage();

        response.end(JsonUtils.toJson(error));
    }
}
