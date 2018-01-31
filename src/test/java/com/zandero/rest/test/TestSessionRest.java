package com.zandero.rest.test;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
