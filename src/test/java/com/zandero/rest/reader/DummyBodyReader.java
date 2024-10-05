package com.zandero.rest.reader;

import com.zandero.rest.test.json.Dummy;

import jakarta.ws.rs.Consumes;

/**
 *
 */
@Consumes("application/json")
public class DummyBodyReader extends JsonValueReader<Dummy> {
}
