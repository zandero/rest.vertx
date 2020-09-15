package com.zandero.rest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.StringUtils;

import javax.ws.rs.*;

/**
 *
 */
@Path("query")
public class TestQueryRest {

    @GET
    @Path("add")
    public int add(@QueryParam("one") int one, @QueryParam("two") int two) {

        return one + two;
    }

    @GET
    @Path("invert")
    public float add(@QueryParam("negative") boolean negative, @QueryParam("value") float value) {

        if (negative) {
            return -value;
        }

        return value;
    }

    @GET
    @Path("/json")
    @Consumes("application/json;charset=UTF-8") // should be ignored
    @Produces("application/json;charset=UTF-8")
    public Dummy echoGetDummy(@QueryParam("dummy") @RequestReader(DummyBodyReader.class) Dummy dummy, @HeaderParam("X-Test") String testHeader) {

        return dummy;
    }

    @GET
    @Path("/empty")
    public String echoGetDummy(@QueryParam("empty") @DefaultValue("true") Boolean empty) {

        if (empty == null) {
            return "null";
        }

        return empty ? "true" : "false";
    }

    @GET
    @Path("/decode")
    public String echoGetQuery(@QueryParam("query") String query,
                               @QueryParam("original") @Raw String original) {

        original = StringUtils.trimToNull(original) == null ? "" : original;
        query = StringUtils.trimToNull(query) == null ? "" : query;
        return query + original;
    }
}
