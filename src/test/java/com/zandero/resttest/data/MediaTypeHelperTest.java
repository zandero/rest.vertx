package com.zandero.resttest.data;


import com.zandero.rest.data.MediaTypeHelper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 */
class MediaTypeHelperTest {


    @Test
    void getKeyTest() {

        assertEquals("*/*", MediaTypeHelper.getKey(null));
        assertEquals("application/json", MediaTypeHelper.getKey(MediaType.APPLICATION_JSON_TYPE));
        assertEquals("application/xml", MediaTypeHelper.getKey(MediaType.APPLICATION_XML_TYPE));
    }

    @Test
    void getMediaTypeInvalidCharset() {
        MediaType type = MediaTypeHelper.valueOf("application/json;UTF-8");
        assertEquals(MediaTypeHelper.toString(MediaType.APPLICATION_JSON_TYPE), MediaTypeHelper.toString(type));
    }

    @Test
    void getMediaTypeTest() {

        assertNull(MediaTypeHelper.valueOf(null));
        assertNull(MediaTypeHelper.valueOf(""));
        assertNull(MediaTypeHelper.valueOf("  "));
        assertNull(MediaTypeHelper.valueOf(" tralala "));

        MediaType type = MediaTypeHelper.valueOf("application/json");
        assertEquals(MediaTypeHelper.toString(MediaType.APPLICATION_JSON_TYPE), MediaTypeHelper.toString(type));

        type = MediaTypeHelper.valueOf("application/json;charset=UTF-8");
        assertEquals(MediaTypeHelper.toString(MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8")), MediaTypeHelper.toString(type));
    }

    @Test
    void joinMediaTypes() {

        MediaType[] base = new MediaType[]{};
        MediaType[] additional = new MediaType[]{MediaTypeHelper.valueOf("application/json"),
            MediaTypeHelper.valueOf("application/json;charset=UTF-8")};

        MediaType[] out = MediaTypeHelper.join(base, additional);

        assertEquals(1, out.length);
        assertEquals("application", out[0].getType());
        assertEquals("json", out[0].getSubtype());
        assertEquals("UTF-8", out[0].getParameters().get("charset"));
    }

    @Test
    void joinMediaTypes_2() {

        MediaType[] base = new MediaType[]{MediaTypeHelper.valueOf("application/json;charset=UTF-8"),
            MediaTypeHelper.valueOf("text/html;charset=UTF-8")};
        MediaType[] additional = new MediaType[]{MediaTypeHelper.valueOf("text/html"),
            MediaTypeHelper.valueOf("application/json;charset=UTF-8")};

        MediaType[] out = MediaTypeHelper.join(base, additional);

        assertEquals(2, out.length);
        assertEquals("application", out[0].getType());
        assertEquals("json", out[0].getSubtype());
        assertEquals("UTF-8", out[0].getParameters().get("charset"));

        assertEquals("text", out[1].getType());
        assertEquals("html", out[1].getSubtype());
        assertEquals("UTF-8", out[1].getParameters().get("charset"));
    }

    @Test
    void getMediaTypesFromHeaders() {
        Map<String, String> headers = new HashMap<>();

        MediaType[] out = MediaTypeHelper.getMediaTypes(headers);
        assertEquals(out.length, 0);

        headers.put("Accept", "application/json");
        out = MediaTypeHelper.getMediaTypes(headers);
        assertEquals(out.length, 1);
        assertEquals(out[0], new MediaType("application", "json"));
    }
}