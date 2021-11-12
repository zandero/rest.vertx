package com.zandero.rest.writer;

import com.zandero.utils.ResourceUtils;
import io.vertx.core.http.*;
import org.slf4j.*;

import static javax.ws.rs.core.Response.Status.*;

public class ResourceResponseWriter implements HttpResponseWriter<String> {

    Logger log = LoggerFactory.getLogger(ResourceResponseWriter.class);

    @Override
    public void write(String result, HttpServerRequest request, HttpServerResponse response) throws Throwable {

        try {
            String content = ResourceUtils.getResourceAsString(result);
            response.setStatusCode(OK.getStatusCode());
            response.end(content);
        }
        catch (Exception e) {
            log.error("Resource: '" + result + "', not found: ", e);
            response.setStatusCode(NOT_FOUND.getStatusCode());
            response.end();
        }
    }
}