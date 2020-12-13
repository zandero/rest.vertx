package com.zandero.rest.test;

import com.zandero.rest.cache.ExceptionHandlerCache;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

public class LogHandler implements Handler<RoutingContext> {

    private final static Logger log = LoggerFactory.getLogger(ExceptionHandlerCache.class);

    @Override
    public void handle(RoutingContext context) {
        log.info("Path: " + context.toString());
        context.next();
    }
}
