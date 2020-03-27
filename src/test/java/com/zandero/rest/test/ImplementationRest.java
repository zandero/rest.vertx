package com.zandero.rest.test;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 *
 */
@Path("implementation")
public class ImplementationRest extends AbstractRest {

	@RolesAllowed("test")
	@Consumes("html/text") // override abstract
	@Override
	public String get(String id, @QueryParam("additional") String add) {
		return id + add;
	}

	@GET
	@Path("other")
	@PermitAll				// override abstract "admin" role
	public String other() {
		return "other";
	}
}
