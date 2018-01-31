package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.json.User;
import com.zandero.rest.writer.MyXmlWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("xml")
public class TestWithXmlRest {

	@GET
	@Path("/h1")
	@Produces({MediaType.APPLICATION_XML})
	@ResponseWriter(MyXmlWriter.class)
	public Response h1() {
		User u = new User("test");
		return Response.ok(u).build();
	}
}
