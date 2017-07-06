package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.writer.TestDummyWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/incompatible")
public class IncompatibleWriterRest {

	@GET
	@Path("ouch")
	@ResponseWriter(TestDummyWriter.class)
	public String returnOuch() {

		return "should not work!";
	}
}
