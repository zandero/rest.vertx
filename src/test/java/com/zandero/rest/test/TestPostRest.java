package com.zandero.rest.test;

import com.zandero.rest.annotation.Blocking;
import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.annotation.RouteOrder;
import com.zandero.rest.reader.JsonBodyReader;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.test.writer.TestCustomWriter;

import javax.ws.rs.*;

/**
 *
 */
@Produces("application/json")
@Path("/post")
public class TestPostRest {

	@POST
	@Path("/json")
	@Consumes("application/json; charset=utf-8")
	@Produces("application/json; charset=utf-8")
	@RouteOrder(10)
	public Dummy echoJsonPost(Dummy postParam, @HeaderParam("X-Test") String testHeader) {

		postParam.name = "Received-" + postParam.name;
		postParam.value = "Received-" + postParam.value;

		return postParam;
	}

	@PUT
	@Path("/json")
	@RequestReader(JsonBodyReader.class)
	@ResponseWriter(TestCustomWriter.class)
	@RouteOrder(20)
	@Blocking
	public Dummy echoJsonPut(Dummy postParam, @HeaderParam("X-Test") String testHeader) {

		postParam.name = "Received-" + postParam.name;
		postParam.value = "Received-" + postParam.value;

		return postParam;
	}
}
