package com.zandero.resttest.test;

import com.zandero.rest.annotation.RequestReader;
import com.zandero.resttest.reader.DummyBodyReader;
import com.zandero.resttest.reader.NpeReader;
import com.zandero.resttest.reader.TokenReader;
import com.zandero.resttest.test.data.Token;
import com.zandero.resttest.test.json.Dummy;

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
