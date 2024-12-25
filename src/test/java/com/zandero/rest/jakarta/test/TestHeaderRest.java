package com.zandero.rest.jakarta.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.reader.*;
import com.zandero.rest.test.data.*;
import com.zandero.rest.test.json.*;
import jakarta.ws.rs.*;

/**
 *
 */
@Path("header")
public class TestHeaderRest {

    @GET
    @Path("/dummy")
    public String getExtendedDummyFromHeader(@HeaderParam("dummy") Dummy dummy) {

        return dummy.name + "=" + dummy.value;
    }

    // utilize different readers for different params
    @POST
    @Path("/dummy")
    @RequestReader(DummyBodyReader.class)
    public String getExtendedDummyFromHeader(Dummy dummy,
                                             @HeaderParam("token") @RequestReader(TokenReader.class) Token token,
                                             @HeaderParam("other") String other) {

        return dummy.name + "=" + dummy.value + ", " + token.token + " " + other;
    }

    @GET
    @Path("/npe")
    public String npeException(@HeaderParam("dummy") @RequestReader(NpeReader.class) String dummy) {

        return dummy;
    }
}
