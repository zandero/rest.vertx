package com.zandero.resttest.writer;

import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.*;

import jakarta.ws.rs.Produces;

/**
 *
 */
@Produces("application/xml")
public class TestXmlResponseWriter implements HttpResponseWriter<String> {

    @Override
    public void write(String result, HttpServerRequest request, HttpServerResponse response) {
        response.end("<xml>" + result + "</xml>");
    }
}
