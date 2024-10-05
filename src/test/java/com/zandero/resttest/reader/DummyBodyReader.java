package com.zandero.resttest.reader;

import com.zandero.rest.reader.JsonValueReader;
import com.zandero.resttest.test.json.Dummy;

import jakarta.ws.rs.Consumes;

/**
 *
 */
@Consumes("application/json")
public class DummyBodyReader extends JsonValueReader<Dummy> {
}
