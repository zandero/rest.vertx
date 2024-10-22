package com.zandero.resttest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.reader.ValueReader;
import io.vertx.ext.web.RoutingContext;

import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoField;

/**
 * Converts String to Instant
 * tries to use different formats to be successful
 */
public class InstantReader implements ValueReader<Instant> {

    private static final DateTimeFormatter FORMAT_DATE = new DateTimeFormatterBuilder()
                                                             .appendPattern("[yyyy-MM-dd]" +
                                                                                "[yyyy-MM]" +
                                                                                "[yyyy]")
                                                             .optionalStart()
                                                             .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                                                             .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                                                             .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                                                             .optionalEnd()
                                                             .toFormatter()
                                                             .withZone(ZoneOffset.UTC);

    private static final DateTimeFormatter FORMAT_DATE_TIME = new DateTimeFormatterBuilder()
                                                                  .appendPattern("[yyyy-MM-dd'T'HH:mm:ss]" +
                                                                                     "[yyyy-MM-dd'T'HH:mm]" +
                                                                                     "[yyyy-MM-dd'T'HH]")
                                                                  .optionalStart()
                                                                  .parseLenient()
                                                                  .parseDefaulting(ChronoField.MINUTE_OF_DAY, 0)
                                                                  .parseDefaulting(ChronoField.SECOND_OF_DAY, 0)
                                                                  .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                                                                  .appendOffset("+HH", "Z")
                                                                  .optionalEnd()
                                                                  .toFormatter()
                                                                  .withZone(ZoneOffset.UTC);

    @Override
    public Instant read(String value, Class<Instant> type, RoutingContext context) throws Throwable {

        Instant timestamp = InstantTimeUtils.getTimestamp(value,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ISO_DATE_TIME,
                FORMAT_DATE,
                FORMAT_DATE_TIME);

        if (timestamp == null) {
            throw new IllegalArgumentException("Invalid time stamp: '" + value + "', expected format: yyyy-MM-dd'T'hh:mm:ss");
        }

        return timestamp;
    }

    @Override
    public Instant read(String value, TypeReference<Instant> type, RoutingContext context) throws Throwable {
        return null;
    }

    @Override
    public Instant read(String value, JavaType jt, RoutingContext context) {
        return null;
    }
}
