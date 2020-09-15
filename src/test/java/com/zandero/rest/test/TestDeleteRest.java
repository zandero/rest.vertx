package com.zandero.rest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestDummyWriter;

import javax.ws.rs.*;

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
