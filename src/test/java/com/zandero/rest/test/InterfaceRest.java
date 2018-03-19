package com.zandero.rest.test;

import javax.ws.rs.*;

/**
 *
 */
@Path("interface")
public interface InterfaceRest {

	@GET
	@Consumes("application/json")
	@Produces("application/json")
	@Path("echo")
	String echo(@QueryParam("name") String name);
}
