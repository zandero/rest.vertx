package com.zandero.resttest.reader;

import com.zandero.rest.reader.JsonValueReader;
import com.zandero.resttest.test.json.Dummy;
import com.zandero.rest.utils.extra.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class JsonBodyReaderTest {

    @Test
    public void convertToJson() {

        Dummy test = new Dummy("Hello", "World");
        String value = JsonUtils.toJson(test);

        JsonValueReader reader = new JsonValueReader();
        Object item = reader.read(value, Dummy.class,null);

        assertTrue(item instanceof Dummy);

        Dummy dummy = (Dummy) item;
        assertEquals("Hello", dummy.name);
        assertEquals("World", dummy.value);
    }
}