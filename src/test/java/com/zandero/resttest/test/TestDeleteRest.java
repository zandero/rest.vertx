package com.zandero.resttest.test;

import com.zandero.rest.annotation.*;
import com.zandero.resttest.reader.DummyBodyReader;
import com.zandero.resttest.test.json.Dummy;
import com.zandero.resttest.writer.TestDummyWriter;

import jakarta.ws.rs.*;

/**
 *
 */
@Path("/delete")
public class TestDeleteRest {

    @Delete("/it/:tokenReferenceId")
    @RequestReader(DummyBodyReader.class)
    @ResponseWriter(TestDummyWriter.class)
    public Dummy deleteWithBody(@PathParam("tokenReferenceId") String tokenReferenceId, Dummy postParam) {

        postParam.name = "Received-" + postParam.name;
        postParam.value = "Received-" + postParam.value;

        return postParam;
    }

    @Delete("/empty/:tokenReferenceId")
    public String deleteWithoutBody(@PathParam("tokenReferenceId") String tokenReferenceId) {
        return "success";
    }
}
