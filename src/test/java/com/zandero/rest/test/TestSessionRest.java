package com.zandero.rest.test;

import io.vertx.ext.web.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 *
 */
@Path("session")
public class TestSessionRest {

    @GET
    @Path("/echo")
    @Produces(MediaType.TEXT_HTML)
    public String echo(@Context RoutingContext routingContext) {
        Session session = routingContext.session();
        return session.id();
    }
}
