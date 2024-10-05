package com.zandero.resttest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.utils.StringUtils;
import io.vertx.ext.web.RoutingContext;

import java.util.*;

/**
 * Produces list of String as output
 */
public class CustomWordListReader implements ValueReader<List<String>> {

    @Override
    public List<String> read(String value, Class<List<String>> type, RoutingContext context) throws Throwable {
        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return Collections.emptyList();
        }

        // extract words from value ... and return list
        return StringUtils.getWords(value);
    }

    @Override
    public List<String> read(String value, TypeReference<List<String>> type, RoutingContext context) throws Throwable {
        return List.of();
    }

    @Override
    public List<String> read(String value, JavaType jt, RoutingContext context) {
        return List.of();
    }
}
