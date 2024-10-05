package com.zandero.resttest.writer;

import com.zandero.rest.annotation.Header;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.resttest.test.json.User;
import io.vertx.core.http.*;

import jakarta.ws.rs.Produces;

/**
 *
 */
@Produces({"text/xml"}) // we can annotate request writers with produces
@Header({"Cache-Control: private,no-cache,no-store", "Content-Type: text/xml"})
// we can add additional static headers ...
public class MyXmlWriter implements HttpResponseWriter<User> {

    @Override
    public void write(User result, HttpServerRequest request, HttpServerResponse response) {

        // response.headers().set("Content-Type", "text/xml");
        // response.putHeader("Cache-Control", "private,no-cache,no-store");
        response.end("<u name=\"" + result.name + "\" />");
    }
}
