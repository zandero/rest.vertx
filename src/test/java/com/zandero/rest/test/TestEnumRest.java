package com.zandero.rest.test;

import com.zandero.rest.test.data.MyEnum;
import com.zandero.rest.test.data.MyOtherEnum;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */
@Path("enum")
public class TestEnumRest {

    @GET
    @Path("simple/{enum}")
    public String simple(@PathParam("enum") MyOtherEnum value) {
        return value.name();
    }

    @GET
    @Path("reader/{enum}")
    public String read(@PathParam("enum") MyEnum value) {
        return value.name();
    }

    @GET
    @Path("fromString/{enum}") // experimenting ... with path is value of method
    public String fromString(@PathParam("enum") MyOtherEnum value) {
        return value.name();
    }
}
