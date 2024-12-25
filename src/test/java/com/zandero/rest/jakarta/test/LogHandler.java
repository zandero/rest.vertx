package com.zandero.rest.jakarta.test;

import com.zandero.rest.cache.*;
import io.vertx.core.*;
import io.vertx.ext.web.*;
import org.slf4j.*;

public class LogHandler implements Handler<RoutingContext> {

    private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerCache.class);

    @Override
    public void handle(RoutingContext context) {
        log.info("Path: " + context.toString());
        context.next();
    }
}
