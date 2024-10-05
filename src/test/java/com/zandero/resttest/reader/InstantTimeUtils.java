package com.zandero.resttest.reader;

import com.zandero.rest.utils.Assert;
import com.zandero.rest.utils.StringUtils;
import org.junit.jupiter.api.Assertions;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Java 8 date time utils ..
 * Replacing obsolete DateTimeUtils
 */
public final class InstantTimeUtils {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    public static final DateTimeFormatter SHORT_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z").withZone(ZoneOffset.UTC);

    public static final DateTimeFormatter RFC_2822_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z").withZone(ZoneOffset.UTC); // RFC-2822

    private InstantTimeUtils() {
        // hide constructor
    }

    /**
     * Formats time to String
     *
     * @param time to be formatted (UTC) in milliseconds
     * @return date time formatted to yyyy-MM-dd HH:mm:ss Z
     */
    public static String formatDateTime(long time) {

        Instant instant = Instant.ofEpochMilli(time);
        return formatDateTime(instant);
    }

    /**
     * Formats time to String
     *
     * @param time to be formatted (UTC) in milliseconds
     * @return date time formatted to yyyy-MM-dd HH:mm:ss Z
     */
    public static String formatDateTime(Instant time) {

        org.junit.jupiter.api.Assertions.assertNotNull(time, "Missing time!");
        return DATE_TIME_FORMAT.format(time);
    }

    /**
     * Formats time to String
     *
     * @param time to be formatted (UTC) in milliseconds
     * @return date time formatted to yyyy-MM-dd HH:mm
     */
    public static String formatDateTimeShort(long time) {

        Instant instant = Instant.ofEpochMilli(time);
        return formatDateTimeShort(instant);
    }

    /**
     * Formats time to String
     *
     * @param time to be formatted (UTC) in milliseconds
     * @return date time formatted to yyyy-MM-dd HH:mm
     */
    public static String formatDateTimeShort(Instant time) {

        Assert.notNull(time, "Missing time!");
        return SHORT_TIME_FORMAT.format(time);
    }

    /**
     * Formats time to date only
     *
     * @param time to be formatted (UTC) in milliseconds
     * @return date formatted yyyy-MM-dd
     */
    public static String formatDate(long time) {

        Instant instant = Instant.ofEpochMilli(time);
        return DATE_FORMAT.format(instant);
    }

    /**
     * Formats time to date only
     *
     * @param time to be formatted (UTC) in milliseconds
     * @return date formatted yyyy-MM-dd
     */
    public static String formatDate(Instant time) {

        Assert.notNull(time, "Missing time!");
        return DATE_FORMAT.format(time);
    }

    /**
     * Custom date time format
     *
     * @param time   to be formatted
     * @param format simple date time format, or null for default yyyy-MM-dd HH:mm:ss Z format
     * @return formated date time
     */
    public static String format(long time, DateTimeFormatter format) {

        return format(Instant.ofEpochMilli(time), format);
    }

    /**
     * Custom date time format
     *
     * @param time   to be formatted
     * @param format simple date time format, or null for default yyyy-MM-dd HH:mm:ss Z format
     * @return formated date time
     */
    public static String format(Instant time, DateTimeFormatter format) {

        if (format == null) {
            return formatDateTime(time);
        }

        Assert.notNull(time, "Missing time!");
        return format.format(time);
    }

    /**
     * Convert any given timestamp string to long
     * Note that in case conversion fails to produce a result a 0 timestamp is returned!
     *
     * @param value   to be converted
     * @param formats list of formats to try out
     * @return time stamp or 0 if conversion failed
     */
    public static Instant getTimestamp(String value, DateTimeFormatter... formats) {

        Assert.notNull(formats, "Missing date time formats");
        Assert.isTrue(formats.length > 0, "Missing date time formats");

        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return null;
        }

        // OK check if we have 6 digit milliseconds ... if so truncate
        if (value.matches(".*\\.\\d{6}.*")) {
            // remove 3 milliseconds places
            int index = value.indexOf(".");
            value = value.substring(0, index + 4) + value.substring(index + 7);
        }

        // Go over all formats ... and if success return value
        for (DateTimeFormatter format : formats) {

            try {
                return format.parse(value, Instant::from);
            }
            catch (DateTimeParseException e) {
                // try next one ...
            }
        }

        return null;
    }


    /**
     * Converts timestamp into OffsetDatetime
     *
     * @param timestamp to be converted
     * @param zoneId    desired zone or null to take instant zone id
     * @return OffsetDatetime representation of timestamp
     */
    public static OffsetDateTime toOffsetDateTime(long timestamp, ZoneId zoneId) {

        Instant time = Instant.ofEpochMilli(timestamp);
        return toOffsetDateTime(time, zoneId);
    }

    /**
     * Converts timestamp into OffsetDatetime
     *
     * @param time   to be converted
     * @param zoneId desired zone or null to take instant zone id
     * @return OffsetDatetime representation of timestamp
     */
    public static OffsetDateTime toOffsetDateTime(Instant time, ZoneId zoneId) {

        Assert.notNull(time, "Missing time!");
        if (zoneId == null) {
            zoneId = ZoneId.from(time);
        }
        return OffsetDateTime.ofInstant(time, zoneId);
    }

    /**
     * Converts offset date time to long timestamp
     *
     * @param timestamp to be converted
     * @return time stamp in milliseconds
     */
    public static long fromOffsetToMilliseconds(OffsetDateTime timestamp) {

        Assert.notNull(timestamp, "Missing offset time stamp!");
        return timestamp.toInstant().toEpochMilli();
    }

    /**
     * Converts offset date time to long timestamp
     *
     * @param timestamp to be converted
     * @return time stamp in milliseconds
     */
    public static Instant fromOffsetToInstant(OffsetDateTime timestamp) {

        Assert.notNull(timestamp, "Missing offset time stamp!");
        return timestamp.toInstant();
    }

    /**
     * Gets first millisecond of first day in month
     *
     * @param time to get first millisecond
     * @return first millisecond of month for given time
     */
    public static Instant getMonthStart(Instant time) {

        Assert.notNull(time, "Missing date time");
        LocalDateTime dateTime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
        dateTime = dateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).with(ChronoField.MILLI_OF_SECOND, 0);
        return dateTime.toInstant(ZoneOffset.UTC);
    }

    /**
     * Returns last millisecond of last day in month ... +1 = next first day in month
     *
     * @param time to get last second in month
     * @return last millisecond of month for given time
     */
    public static Instant getMonthEnd(Instant time) {

        Assert.notNull(time, "Missing date time");
        LocalDateTime dateTime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
        dateTime = dateTime.withDayOfMonth(1).withHour(23).withMinute(59).withSecond(59).with(ChronoField.MILLI_OF_SECOND, 999);
        dateTime = dateTime.plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS);
        return dateTime.toInstant(ZoneOffset.UTC);
    }


    /**
     * Check is time periods overlap
     *
     * @param startOne start time of first period / null for full
     * @param endOne   end tme of first period / null for full
     * @param startTwo start time of second period / null for full
     * @param endTwo   end time of second period / null for full
     * @return true if periods overlap, false if they don't
     * @throws IllegalArgumentException in case start time is after end time of first/second period
     */
    public static boolean overlaps(Instant startOne, Instant endOne, Instant startTwo, Instant endTwo) {

        // (StartDate1 <= EndDate2) and (StartDate2 <= EndDate1)
        Instant startDate1 = startOne == null ? Instant.MIN : startOne;
        Instant endDate1 = endOne == null ? Instant.MAX : endOne;
        Assertions.assertTrue((startDate1.isBefore(endDate1)), "Start date one can't be after end date one!");

        Instant startDate2 = startTwo == null ? Instant.MIN : startTwo;
        Instant endDate2 = endTwo == null ? Instant.MAX : endTwo;
        Assertions.assertTrue(startDate2.isBefore(endDate2), "Start date two can't be after end date two!");

        return startDate1.compareTo(endDate2) <= 0 && startDate2.compareTo(endDate1) <= 0;
    }
}
