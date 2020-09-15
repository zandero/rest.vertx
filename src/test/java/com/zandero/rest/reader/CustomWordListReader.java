package com.zandero.rest.reader;

import com.zandero.utils.StringUtils;

import java.util.*;

/**
 * Produces list of String as output
 */
public class CustomWordListReader implements ValueReader<List<String>> {

    @Override
    public List<String> read(String value, Class<List<String>> type) {
        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return Collections.emptyList();
        }

        // extract words from value ... and return list
        return StringUtils.getWords(value);
    }
}
