package com.zandero.rest.test;

import com.zandero.rest.test.data.ItemDto;

import javax.ws.rs.*;
import java.util.*;

@Produces("application/json")
@Path("/write")
public class TestWriterRest {

    @GET
    @Path("/one")
    public ItemDto one() {

        return new ItemDto("Hello world!");
    }

    @GET
    @Path("/many")
    public List<ItemDto> many() {

        return Arrays.asList(new ItemDto("One"),
                             new ItemDto("Two"));
    }
}

