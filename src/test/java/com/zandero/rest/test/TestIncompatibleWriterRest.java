package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.writer.TestDummyWriter;

import javax.ws.rs.*;

/**
 *
 */
@Path("/incompatible")
public class TestIncompatibleWriterRest {

    @GET
    @Path("ouch")
    @ResponseWriter(TestDummyWriter.class)
    public String returnOuch() {

        return "should not work!";
    }
}
