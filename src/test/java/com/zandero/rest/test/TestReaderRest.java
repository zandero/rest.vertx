package com.zandero.rest.test;

import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.reader.CustomWordListReader;
import com.zandero.rest.test.json.*;
import com.zandero.utils.*;

import javax.ws.rs.*;
import java.time.Instant;
import java.util.*;

/**
 *
 */
@Path("/read")
public class TestReaderRest {

    @POST
    @Path("/custom")
    @RequestReader(CustomWordListReader.class) // use custom reader to convert body to list of String
    public String getWords(List<String> words) {

        return getString(words);
    }

    @POST
    @Path("/registered")
    public String getWords2(List<String> words) { // manually assign reader ....

        return getString(words);
    }

    private String getString(List<String> words) {

        Set<String> unique = new HashSet<>();
        for (String word : words) {
            unique.add(word.toLowerCase());
        }

        // return sorted list of words
        Object[] array = unique.toArray();
        Arrays.sort(array);

        return StringUtils.join(array, ",");
    }

    @POST
    @Path("/normal/dummy")
    @Consumes("application/json")
    public String getDummy(Dummy dummy) {

        return dummy.name + "=" + dummy.value;
    }

    @POST
    @Path("/extended/dummy")
    @Consumes("application/json;charset=UTF-8")
    public String getExtendedDummy(ExtendedDummy dummy) {

        return dummy.name + "=" + dummy.value + " (" + dummy.type + ")";
    }

    @GET
    @Path("/time")
    public String getInstant(@QueryParam("is") Instant time) {

        return InstantTimeUtils.formatDateTime(time);
    }
}
