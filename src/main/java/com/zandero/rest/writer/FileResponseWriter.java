package com.zandero.rest.writer;

import io.vertx.core.file.FileSystem;
import io.vertx.core.http.*;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.Context;
import java.io.FileNotFoundException;

/**
 * Serves static files / resources
 */
public class FileResponseWriter implements HttpResponseWriter<String> {

    @Context
    RoutingContext context;

    @Override
    public void write(String path, HttpServerRequest request, HttpServerResponse response) throws FileNotFoundException {

        if (!fileExists(context, path)) {
            throw new FileNotFoundException(path);
        }

        response.sendFile(path);
    }

    protected boolean fileExists(RoutingContext context, String file) {
        FileSystem fs = context.vertx().fileSystem();
        return fs.existsBlocking(file);
    }
}
