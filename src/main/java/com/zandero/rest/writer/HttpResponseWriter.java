package com.zandero.rest.writer;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.Header;
import com.zandero.rest.data.*;
import io.vertx.core.http.*;
import org.slf4j.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Response writer interface to implement
 * use RestRouter.getWriters().register(...) to register a global writer
 * or use @ResponseWriter annotation to associate REST with given writer
 */
public interface HttpResponseWriter<T> {

    Logger log = LoggerFactory.getLogger(HttpResponseWriter.class);

    void write(T result, HttpServerRequest request, HttpServerResponse response) throws Throwable;

    /**
     * @param definition       route definition
     * @param requestMediaType client requested media type .. accept header or null for all
     * @param response         response to add response headers
     */
    default void addResponseHeaders(RouteDefinition definition, MediaType requestMediaType, HttpServerResponse response) {

        if (!response.ended()) {


            Map<String, String> headers = new HashMap<>();

            // collect all headers to put into response ...

            // 1. add definition headers
            headers = join(headers, definition.getHeaders());

            // 2. add REST produces
            headers = join(headers, requestMediaType, definition.getProduces());

            // 3. add / override Writer headers
            Header writerHeader = this.getClass().getAnnotation(Header.class);
            if (writerHeader != null && writerHeader.value().length > 0) {
                headers = join(headers, AnnotationProcessor.getNameValuePairs(writerHeader.value()));
            }

            // 4. add / override with Writer produces
            Produces writerProduces = this.getClass().getAnnotation(Produces.class);
            if (writerProduces != null && writerProduces.value().length > 0) {
                headers = join(headers, requestMediaType, MediaTypeHelper.getMediaTypes(writerProduces.value()));
            }

            // 5. add wildcard if no content-type present
            if (!headers.containsKey(HttpHeaders.CONTENT_TYPE.toString())) {
                headers.put(HttpHeaders.CONTENT_TYPE.toString(), MediaType.WILDCARD);
            }

            String selectedContentType = headers.get(HttpHeaders.CONTENT_TYPE.toString());
            if (requestMediaType == null) {
                log.trace("No 'Accept' header present in request using first @Produces Content-Type='" + selectedContentType + "', for: " + definition.getPath());
            } else {
                log.trace("Selected Content-Type='" + selectedContentType + "', for: " + definition.getPath());
            }

            // add all headers not present in response
            for (String name : headers.keySet()) {
                if (!response.headers().contains(name)) {
                    response.headers().add(name, headers.get(name));
                }
            }
        }
    }

    default Map<String, String> join(Map<String, String> original, Map<String, String> additional) {
        if (additional != null && additional.size() > 0) {
            original.putAll(additional);
        }

        original.remove(HttpHeaders.CONTENT_TYPE.toString()); // remove content-type headers from output
        return original;
    }

    default Map<String, String> join(Map<String, String> original, MediaType contentMediaType, MediaType[] additional) {
        if (contentMediaType == null) {
            contentMediaType = MediaType.WILDCARD_TYPE;
        }

        MediaType first = null; // to be used in case no content type matches requested content type
        if (additional != null && additional.length > 0) {

            for (MediaType produces : additional) {
                if (first == null) {
                    first = produces;
                }

                if (MediaTypeHelper.matches(contentMediaType, produces)) {
                    original.put(HttpHeaders.CONTENT_TYPE.toString(), MediaTypeHelper.toString(produces));
                    break;
                }
            }
        }

        if (original.get(HttpHeaders.CONTENT_TYPE.toString()) == null && first != null) {
            original.put(HttpHeaders.CONTENT_TYPE.toString(), MediaTypeHelper.toString(first));
        }

        return original;
    }
}
