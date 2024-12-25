package com.zandero.rest.writer;

import com.zandero.rest.*;
import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.utils.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.*;

import javax.ws.rs.core.*;
import java.util.*;

import static io.vertx.core.http.HttpHeaders.*;

/**
 * Produces vert.x response based on JAX-RS response builder output
 */
public class JaxResponseWriter implements HttpResponseWriter<Response> {

    @Context
    RoutingContext context;

    @SuppressWarnings("unchecked")
    @Override
    public void write(Response result, HttpServerRequest request, HttpServerResponse response) throws Throwable {

        Assert.notNull(result, "Expected result but got null!");

        response.setStatusCode(result.getStatus());
        addHeaders(result, response);

        if (result.getEntity() != null) {

            // try to find appropriate writer ...
            String mediaType = response.headers().get(CONTENT_TYPE);

            HttpResponseWriter writer;
            try {
                writer = (HttpResponseWriter) ClassFactory.get(mediaType, RestRouter.getWriters(), context);
            } catch (ClassFactoryException | ContextException e) {
                writer = null;
            }

            if (writer != null) {
                writer.write(result.getEntity(), request, response);
            } else {
                response.end(result.getEntity().toString());
            }
        } else {
            response.end();
        }
    }

    private static void addHeaders(Response jaxrsResponse, HttpServerResponse response) {

        if (jaxrsResponse.getMetadata() != null) {

            List<Object> cookies = jaxrsResponse.getMetadata().get(SET_COOKIE.toString());
            if (cookies != null) {

                for (Object next : cookies) {
                    if (next instanceof NewCookie) {
                        NewCookie cookie = (NewCookie) next;
                        response.putHeader(SET_COOKIE, cookie.toString());
                    }
                }

                if (cookies.isEmpty()) {
                    jaxrsResponse.getMetadata().remove(SET_COOKIE.toString());
                }
            }
        }

        if (jaxrsResponse.getMetadata() != null && !jaxrsResponse.getMetadata().isEmpty()) {

            for (String name : jaxrsResponse.getMetadata().keySet()) {
                List<Object> meta = jaxrsResponse.getMetadata().get(name);

                if (meta != null && !meta.isEmpty()) {
                    for (Object item : meta) {
                        if (item != null) {
                            response.putHeader(name, item.toString());
                        }
                    }
                }
            }
        }
    }
}
