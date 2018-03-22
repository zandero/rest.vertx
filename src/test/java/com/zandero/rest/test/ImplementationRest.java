package com.zandero.rest.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 *
 */
@Path("implementation")
public class ImplementationRest extends AbstractRest {

	@Consumes("html/text") // override abstract
	@Override
	public String get(String id, @QueryParam("additional") String add) {
		return id + add;
	}
}
