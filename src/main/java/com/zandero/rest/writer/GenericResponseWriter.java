package com.zandero.rest.writer;

import com.zandero.rest.RestRouter;
import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.exception.*;
import com.zandero.rest.provisioning.ClassProducer;
import io.vertx.core.http.*;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.core.*;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Tries to find and utilize associated mime type / media type writer
 * If no writer found a generic Object.toString() write is triggered
 */
@SuppressWarnings("unchecked")
public class GenericResponseWriter<T> implements HttpResponseWriter<T> {

    private final static Logger log = LoggerFactory.getLogger(GenericResponseWriter.class);

    @Context
    RoutingContext context;

    @Override
    public void write(T result, HttpServerRequest request, HttpServerResponse response) throws Throwable {

        // Content-Type header is filled by HttpResponseWriter.addResponseHeaders() call
        String mediaType = response.headers().get(CONTENT_TYPE);
        if (mediaType == null) {
            log.trace("No Content-Type selected, defaulting to Content-Type='*/*'");
            mediaType = MediaType.WILDCARD;
        }

        HttpResponseWriter<T> writer;
        try {
            writer = (HttpResponseWriter<T>) ClassProducer.getMediaTypeClassInstance(MediaTypeHelper.valueOf(mediaType),
                                                                                  RestRouter.getWriters(),
                                                                                  RestRouter.getContextInjector(),
                                                                                  RestRouter.getInjectionProvider(),
                                                                                  context);
        } catch (ClassFactoryException | ContextException e) {
            log.warn("Failed to provide GenericResponseWriter: ", e);
            writer = null;
        }

        if (writer != null && !(writer instanceof GenericResponseWriter)) {
            writer.write(result, request, response);
        } else {
            log.warn(request.path() + " - no writer for Content-Type='" + mediaType + "', defaulting to toString() as output!");
            MediaType type = MediaTypeHelper.valueOf(mediaType);
            if (type != null) {
                // TODO ... correct response headers should already be set according to ACCEPT header
                response.headers().remove(CONTENT_TYPE.toString());
                response.headers().add(CONTENT_TYPE.toString(), MediaTypeHelper.toString(type));
            }

            if (result != null) {
                response.end(result.toString());
            } else {
                response.end();
            }
        }
    }
}
