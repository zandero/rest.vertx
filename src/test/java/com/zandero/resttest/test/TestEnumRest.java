package com.zandero.resttest.test;

import com.zandero.resttest.test.data.MyEnum;
import com.zandero.resttest.test.data.MyOtherEnum;
import jakarta.ws.rs.*;

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
