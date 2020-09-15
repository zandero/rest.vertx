package com.zandero.rest.test;

import com.zandero.rest.annotation.RouteOrder;

import javax.ws.rs.*;

/**
 *
 */
@Path("order")
public class TestOrderRest {

    @RouteOrder(20)
    @GET
    @Path("/test")
    public String third() {

        return "third";
    }

    @RouteOrder(10)
    @GET
    @Path("/test")
    public String first() {
        return "first";
    }

    @RouteOrder(15)
    @GET
    @Path("/test")
    public String second() {
        return "second";
    }
}
