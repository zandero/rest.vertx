package com.zandero.rest.test;

import com.zandero.rest.annotation.Get;
import com.zandero.rest.annotation.Post;
import com.zandero.rest.test.json.ValidDummy;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 *
 */
@Path("check")
public class TestValidRest {

	@Post(value = "dummy", produces = "application/json", consumes = "application/json")
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

	@Get("other")
	public int theOther(@Min(1) @Max(10) @QueryParam("one") int one,
	                    @Min(1) @QueryParam("two") int two,
	                    @Max(10) @QueryParam("three") int three) {
		return one + two + three;
	}
}
