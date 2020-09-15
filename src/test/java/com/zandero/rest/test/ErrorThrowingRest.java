package com.zandero.rest.test;

import com.zandero.rest.annotation.CatchWith;
import com.zandero.rest.exception.*;
import com.zandero.rest.test.handler.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/throw")
@CatchWith(JsonExceptionHandler.class) // catch globally for whole root
public class ErrorThrowingRest {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ouch")
    public String returnOuch() {

        throw new IllegalArgumentException("Ouch!");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("bang")
    @CatchWith()
    public String returnBang() {

        throw new IllegalArgumentException("Bang!");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("multi/{bang}")
    @CatchWith({IllegalArgumentExceptionHandler.class, WebApplicationExceptionHandler.class, MyExceptionHandler.class, MyOtherExceptionHandler.class})
    public String returnMultiBang(@PathParam("bang") String bang) throws ExecuteException, MyExceptionClass {

        switch (bang) {
            case "one":
                throw new ExecuteException(405, "HTTP 405 Method Not Allowed");

            case "two":
            default:
                throw new IllegalArgumentException("Bang!");

            case "three":
                throw new NumberFormatException("WHAT!");

            case "four":
                throw new MyExceptionClass("ADIOS!", 500);
        }
    }

    // JsonExceptionHandler should catch those
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("big/{bang}")
    public String returnBigBang(@PathParam("bang") String bang) throws Throwable {

        switch (bang) {
            case "one":
                throw new ExecuteException(405, "HTTP 405 Method Not Allowed");
            case "two":
            default:
                throw new IllegalArgumentException("Bang!");

            case "three":
                throw new NumberFormatException("WHAT!");

            case "four":
                throw new MyExceptionClass("ADIOS!", 500);
        }
    }
}
