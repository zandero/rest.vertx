package com.zandero.rest.test;

import javax.ws.rs.*;

/**
 *
 */
@Path("matrix")
public class TestMatrixParamRest {

    @GET
    @Path("extract/{param}")
    public String add(@PathParam("param") String param, @MatrixParam("one") int one, @MatrixParam("two") int two) {

        int result = one + two;
        return param + "=" + result;
    }

    @GET
    @Path("direct/{placeholder:.*}")
    public int add(@MatrixParam("one") int one, @MatrixParam("two") int two) {

        return one + two;
    }
}
