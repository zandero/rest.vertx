package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Test REST serving static files
 */
@Path("/")
public class TestFileRest {

	@GET
	@Path("{file}")
	public String serveFile(@PathParam("file") String file) {
		return file;
	}
}
