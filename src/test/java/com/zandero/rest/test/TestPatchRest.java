package com.zandero.rest.test;

import com.zandero.rest.annotation.Blocking;
import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.annotation.RouteOrder;
import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestDummyWriter;

import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 */
@Produces("application/json")
@Path("/patch")
public class TestPatchRest {

	@PATCH
	@Path("/it")
	@RequestReader(DummyBodyReader.class)
	@ResponseWriter(TestDummyWriter.class)
	@RouteOrder(20)
	@Blocking
	public Dummy echoJsonPatch(Dummy postParam) {

		postParam.name = "Received-" + postParam.name;
		postParam.value = "Received-" + postParam.value;

		return postParam;
	}
}
