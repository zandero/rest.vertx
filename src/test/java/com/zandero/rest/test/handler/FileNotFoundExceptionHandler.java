package com.zandero.rest.test.handler;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.test.exceptions.InheritedBaseException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.FileNotFoundException;

public class FileNotFoundExceptionHandler implements ExceptionHandler<FileNotFoundException> {
    @Override
    public void write(FileNotFoundException result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(404);
        response.end(result.getMessage());
    }
}
