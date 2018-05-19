package com.zandero.rest.test;

import com.zandero.rest.test.exceptions.BaseException;
import com.zandero.rest.test.exceptions.InheritedBaseException;
import com.zandero.rest.test.exceptions.InheritedFromInheritedException;
import com.zandero.rest.test.handler.MyExceptionClass;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/throw")
public class ErrorThrowingRest2 {

	@GET
	@Path("unhandled")
	public String returnKabum() {

		throw new IllegalArgumentException("KABUM!");
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("exception/{bang}")
	public String returnException(@PathParam("bang") String bang) throws Throwable {

		switch (bang) {
			default:
			case "one":
				throw new BaseException("first");

			case "two":
				throw new InheritedBaseException("second");

			case "three":
				throw new InheritedFromInheritedException("third");

			case "four":
				throw new MyExceptionClass("four", 406);
		}
	}
}
