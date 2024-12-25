package com.zandero.rest.jakarta.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.writer.*;
import jakarta.ws.rs.*;

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
