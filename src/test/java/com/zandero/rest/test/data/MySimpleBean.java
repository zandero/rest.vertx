package com.zandero.rest.test.data;

import com.zandero.rest.annotation.*;

import javax.ws.rs.*;

public class MySimpleBean {

    @HeaderParam("MyHeader")
    private boolean header;

    @PathParam("param")
    private String path;

    @QueryParam("query")
    @Raw
    private String query;

    @CookieParam("chocolate")
    private String cookie;

    @MatrixParam("enum")
    private MyEnum enumValue;

    @MatrixParam("one")
    private int oneValue;

    @FormParam("form")
    private String form;

    @BodyParam
    @DefaultValue("empty")
    private String body;

  /*  @Context
    private Dummy dummy;*/

    @Override
    public String toString() {
        return "Header: " + header +
                   ", Path: " + path +
                   ", Query: " + query +
                   ", Cookie: " + cookie +
                   ", Matrix: " + enumValue +
                   ", one: " + oneValue +

                   //  ", Context: " + dummy.name + "/" + dummy.value +
                   ", Body: " + body;
    }
}
