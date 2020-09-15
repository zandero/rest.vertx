package com.zandero.rest.test.data;

import com.zandero.rest.annotation.Raw;

import javax.ws.rs.*;

public class MyComplexBean {

    private final boolean header;

    private final String path;

    private final int query;

    private final String cookie;

    @MatrixParam("enum")
    private MyEnum enumValue;

    public MyComplexBean(@PathParam("path") String path,
                         @HeaderParam("MyHeader") boolean header,
                         @QueryParam("query") @Raw int query,
                         @CookieParam("chocolate") String cookie) {

        this.path = path;
        this.header = header;
        this.query = query;
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "Header: " + header +
                   ", Path: " + path +
                   ", Query: " + query +
                   ", Cookie: " + cookie +
                   ", Matrix: " + enumValue;
    }
}
