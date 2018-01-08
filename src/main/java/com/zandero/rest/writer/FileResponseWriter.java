package com.zandero.rest.writer;

import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Serves static files / resources
 */
public class FileResponseWriter implements HttpResponseWriter<String> {

	@Context
	RoutingContext context;

	@Override
	public void write(String path, HttpServerRequest request, HttpServerResponse response) {

		if (fileExists(context, path)) {
			response.sendFile(path);
		}
		else {
			response.setStatusCode(Response.Status.NOT_FOUND.getStatusCode());
		}

	}

	protected boolean fileExists(RoutingContext context, String file) {
		FileSystem fs = context.vertx().fileSystem();
		return fs.existsBlocking(file);
	}
}
