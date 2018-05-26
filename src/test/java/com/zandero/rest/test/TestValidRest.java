package com.zandero.rest.test;

import com.zandero.rest.annotation.Get;
import com.zandero.rest.annotation.Post;
import com.zandero.rest.test.json.ValidDummy;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 *
 */
@Path("check")
public class TestValidRest {

	@Post(value = "dummy", produces = "application/json")
	public String echo(@Valid ValidDummy dummy) {
		return dummy.name;
	}

	@Get("that")
	public String thatOne(@QueryParam("one") String one) {
		return one;
	}

	@Get("this")
	public String thisOne(@NotNull @QueryParam("one") String one) {
		return one;
	}
}
