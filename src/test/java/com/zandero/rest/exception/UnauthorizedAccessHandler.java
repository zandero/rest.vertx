package com.zandero.rest.exception;

import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.http.*;

public class UnauthorizedAccessHandler implements ExceptionHandler<UnauthorizedException> {

    @Override
    public void write(UnauthorizedException exception, HttpServerRequest request, HttpServerResponse response) {

        String message = exception.getMessage();
        int code = exception.getStatusCode();

        if (exception.user instanceof SimulatedUser) {
            // special logic here ... in case we know the user ...
            message = "HTTP 403 Forbidden";
            code = 403;
        }

        response.setStatusCode(code);
        response.end(message);
    }
}
