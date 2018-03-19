package com.zandero.rest.test;

import javax.ws.rs.*;

/**
 *
 */
@Path("abstract")
public abstract class AbstractRest implements InterfaceRest {

	@Produces("html/text") // override interface
	@Override
	public String echo(String name) {
		return name;
	}

	@GET
	@Consumes("application/json")
	@Produces("application/json")
	@Path("get/{id}")
	public abstract String get(@PathParam("id") String id);
}
