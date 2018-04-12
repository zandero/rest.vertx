package com.zandero.rest.writer;

import com.zandero.rest.test.json.User;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.Produces;

/**
 *
 */
@Produces("text/xml") // we can annotate request writers with produces
public class MyXmlWriter implements HttpResponseWriter<User> {

	@Override
	public void write(User result, HttpServerRequest request, HttpServerResponse response) {

		response.end("<u name=\"" + result.name  + "\" />");
	}
}
