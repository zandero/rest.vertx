package com.zandero.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/html")
public class TestHtmlRest {

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("body")
	public String returnBody() {

		return "body";
	}
}
