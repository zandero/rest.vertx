package com.zandero.rest.jakarta.test;

import com.zandero.rest.test.exceptions.*;
import com.zandero.rest.test.handler.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

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

    @GET
    @Path("myHandler")
    public String bang() {
        throw new RuntimeException("auch!");
    }
}
