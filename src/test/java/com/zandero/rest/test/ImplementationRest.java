package com.zandero.rest.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

/**
 *
 */
@Path("implementation")
public class ImplementationRest extends AbstractRest {

	@Consumes("html/text") // override abstract
	@Override
	public String get(String id) {
		return id;
	}
}
