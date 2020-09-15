package com.zandero.rest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.test.json.ValidDummy;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.ws.rs.*;
import java.util.*;

/**
 *
 */
@Path("check")
public class TestValidRest {

    @Post(value = "dummy", produces = "application/json", consumes = "application/json")
    public String echo(@Valid ValidDummy dummy) {
        return dummy.name;
    }

    @Get("that")
    public String thatOne(@QueryParam("one") String one) {
        return one;
    }

    @Get("this")
    public String thisOne(@NotNull @QueryParam("one") String one) {
        return one;
    }

    @Get("result")
    @NotNull
    @Min(1)
    @Max(10)
    public Integer resultTest(@QueryParam("one") String one) {

        try {
            return Integer.valueOf(one);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Get("other")
    public int theOther(@Min(1) @Max(10) @QueryParam("one") int one,
                        @Min(1) @QueryParam("two") int two,
                        @Max(10) @QueryParam("three") int three) {
        return one + two + three;
    }

    @GET
    @Path("empty")
    public List<String> list() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        return list;
    }
}
