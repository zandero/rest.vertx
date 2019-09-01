package com.zandero.rest.test;

import com.zandero.rest.annotation.Delete;
import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.annotation.RouteOrder;
import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestDummyWriter;

import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 */
@Path("/delete")
public class TestDeleteRest {

	@Delete("/it/:tokenReferenceId")
	@RequestReader(DummyBodyReader.class)
	@ResponseWriter(TestDummyWriter.class)
	public Dummy deleteWithBody(@PathParam("tokenReferenceId") String tokenReferenceId, Dummy postParam) {

		postParam.name = "Received-" + postParam.name;
		postParam.value = "Received-" + postParam.value;

		return postParam;
	}

	@Delete("/empty/:tokenReferenceId")
	public String deleteWithoutBody(@PathParam("tokenReferenceId") String tokenReferenceId) {
		return "success";
	}
}
