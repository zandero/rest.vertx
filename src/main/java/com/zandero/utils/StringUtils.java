package com.zandero.utils;

import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

/**
 * Utilities to manipulate and check strings
 */
public final class StringUtils {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\p{L}+\\b");
    private static final String PUNCTUATION_PATTERN = "\\p{P}";
    private static final String WHITE_SPACE_PATTERN = "\\s+";
    private static final String TRIM_END_PATTERN = "\\s+$";
    private static final String TRIM_START_PATTERN = "^\\s+";

    private StringUtils() { /* hiding constructor */ }

    /**
     * Checks if string is <code>null</code> or <code>empty == ""</code>
     *
     * @param value string to test
     * @return <code>true</code> if <code>null</code> or empty, <code>false</code> otherwise.
     */
    public static boolean isNullOrEmpty(String value) {

        return (value == null || value.length() == 0);
    }

    /**
     * Checks if string is null, empty or contains only spaces
     *
     * @param value string to test
     * @return <code>true</code> if <code>null</code>, empty or spaces only, <code>false</code> otherwise.
     */
    public static boolean isNullOrEmptyTrimmed(String value) {

        return (value == null || value.trim().length() == 0);
    }

    /**
     * Checks if non null string is empty
     *
     * @param value string to test
     * @return <code>true</code> if <code>empty</code>, <code>false</code> if null or not empty.
     */
    public static boolean isEmpty(String value) {
        return (value != null && value.length() == 0);
    }

    /**
     * Checks if non null string is empty when trimmed
     *
     * @param value string to test
     * @return <code>true</code> if <code>empty when trimmed</code>, <code>false</code> if null or not empty.
     */
    public static boolean isEmptyTrimmed(String value) {
        return (value != null && value.trim().length() == 0);
    }

    /**
     * compares two strings if equal ({@code String.equals} method)
     *
     * @param original string to compare against
     * @param compare  string to compare to
     * @return <code>true</code> if equal or both <code>null</code>, <code>false</code> otherwise
     */
    public static boolean equals(String original, String compare) {

        return equals(original, compare, false);
    }

    /**
     * compares two strings if equal ({@code String.equals} or
     * {@code String.equalsIgnoreCase} method)
     *
     * @param original   string to compare against
     * @param compare    string to compare to
     * @param ignoreCase <code>true</code> equalsIgnoreCase is used, <code>false</code> equals is used
     * @return <code>true</code> if equal or both <code>null</code>, <code>false</code> otherwise
     */
    public static boolean equals(String original, String compare, boolean ignoreCase) {

        if (original == null && compare == null) {
            return true;
        }

        if (original != null) {
            if (ignoreCase) {
                return original.equalsIgnoreCase(compare);
            } else {
                return original.equals(compare);
            }
        }

        return false;
    }

    /**
     * Compares two strings with <code>String.compare()</code>. Method allows
     * <code>null</code> values.
     *
     * @param one to compare
     * @param two to compare
     * @return <b>0</b> if both <code>null</code>, <b>-1</b> if <code>one</code> is <code>null</code>, <b>1</b> if <code>two</code> is <code>null</code>, otherwise <code>String.compare()</code> is
     * called
     */
    public static int compare(String one, String two) {

        if (one == null && two == null) {
            return 0;
        }

        if (one == null) {
            return -1;
        }

        if (two == null) {
            return 1;
        }

        return one.compareTo(two);
    }

    /**
     * null resilient trim
     *
     * @param value trims value or returns null if null
     * @return null or trimmed value (empty string is left empty)
     */
    public static String trim(String value) {

        return value != null ? value.trim() : null;
    }

    /**
     * Additionally to outer <code>trim()</code> double spaces are removed.
     *
     * @param value to remove double spaces from
     * @return value with single spaces if any
     */
    public static String trimDoubleSpaces(String value) {

        if (isNullOrEmpty(value)) {
            return value;
        }

        value = value.trim();
        return value.replaceAll(WHITE_SPACE_PATTERN, " ");
    }

    /**
     * Additionally to outer <code>trim()</code> all inner spaces are removed.
     *
     * @param value to remove inner spaces from
     * @return value with single spaces if any
     */
    public static String trimInner(String value) {

        if (isNullOrEmpty(value)) {
            return value;
        }

        value = value.trim();
        return value.replaceAll(WHITE_SPACE_PATTERN, "");
    }

    /**
     * Trims only end of text
     *
     * @param text to be trimmed at the end
     * @return trimmed text or original if no changes were applied
     */
    public static String trimEnd(String text) {

        if (isNullOrEmpty(text)) {
            return text;
        }

        return text.replaceAll(TRIM_END_PATTERN, "");
    }

    /**
     * Trims spaces from start of string
     *
     * @param text to be trimmed
     * @return trimmed string
     */
    public static String trimStart(String text) {

        if (isNullOrEmpty(text)) {
            return text;
        }

        return text.replaceAll(TRIM_START_PATTERN, "");
    }

    /**
     * Trims text of given pattern
     *
     * @param text        to be trimmed
     * @param toBeTrimmed pattern to search for
     * @return trimmed text
     */
    public static String trimAll(String text, String toBeTrimmed) {

        if (isNullOrEmpty(text) || isNullOrEmpty(toBeTrimmed)) {
            return text;
        }

        return text.replaceAll(toBeTrimmed, "");
    }

    /**
     * Trims down text to null if empty
     *
     * @param text to be trimmed
     * @return text or null if empty
     */
    public static String trimToNull(String text) {

        text = trim(text);

        if (text == null || text.isEmpty()) {
            return null;
        }

        return text;
    }

    /**
     * Capitalizes first character in given string
     *
     * @param input to capitalize first character
     * @return capitalized string or null if null
     */
    public static String capitalize(String input) {

        if (input == null) {
            return null;
        }

        if (input.length() > 1) {
            for (int i = 0; i < input.length(); i++) {
                if (Character.isAlphabetic(input.charAt(i))) {
                    return input.substring(0, i) + Character.toString(input.charAt(i)).toUpperCase() + input.substring(i + 1);
                }
            }
        }

        return input.toUpperCase();
    }

    /**
     * Joins list of string items to a single string, where items are separated
     * with a defined separator.
     *
     * @param list      to join into string
     * @param separator to be used between elements
     * @return items joined into a single string
     */
    public static String join(List<?> list, String separator) {

        return join(list, separator, null);
    }

    /**
     * Joins list of string items to a single string, where items are separated
     * with a defined separator.
     *
     * @param list      to join into string
     * @param separator to be used between elements
     * @param limit     max items to consider when joining, null or 0 to join all
     * @return items joined into a single string
     */
    public static String join(List<?> list, String separator, Integer limit) {

        if (separator == null || separator.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("Missing separator!");
        }

        StringBuilder output = new StringBuilder();
        if (limit == null || limit <= 0) {
            limit = list.size();
        }

        if (list != null && limit > 0) {
            for (int i = 1; i <= limit; i++) {

                if (i > list.size()) {
                    break;
                }

                output.append(list.get(i - 1));

                if (i == limit && list.size() > limit) {
                    // ok list is larger than limit .. and we hit the limit
                    output.append(separator)
                        .append("... [")
                        .append(limit)
                        .append("/")
                        .append(list.size())
                        .append("]");
                }

                if (i < limit) {
                    output.append(separator);
                }
            }
        }

        return output.toString();
    }

    /**
     * Joins list of string items to a single string, where items are separated
     * with a defined separator.
     *
     * @param args      to join into string
     * @param separator to be used between elements
     * @param limit     max items to consider when joining, null or 0 to join all
     * @return items joined into a single string
     */
    public static String join(Object[] args, String separator, Integer limit) {

        if (separator == null || separator.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("Missing separator!");
        }

        if (args != null && args.length > 0) {
            List<Object> array = Arrays.asList(args);
            return join(array, separator, limit);
        }

        return "";
    }

    /**
     * Joins array of objects into string, where items are separated
     * with a defined separator.
     *
     * @param args      array of Object
     * @param separator to be used between elements
     * @return items joined into a single string
     */
    public static String join(Object[] args, String separator) {

        return join(args, separator, null);
    }

    /**
     * Joins list of string items to a single string, where items are separated
     * with a defined separator.
     *
     * @param items     to join into string
     * @param separator to be used between elements
     * @param limit     max items to consider when joining, null or 0 to join all
     * @return items joined into a single string
     */
    public static String join(Set<?> items, String separator, Integer limit) {

        if (separator == null || separator.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("Missing separator!");
        }

        if (items != null && items.size() > 0) {
            Object[] array = items.toArray();
            return join(array, separator, limit);
        }

        return "";
    }

    /**
     * Joins set of items to a single string, where items are separated
     * with a defined separator.
     *
     * @param items     to join into string
     * @param separator to be used between elements
     * @return items joined into a single string
     */
    public static String join(Set<?> items, String separator) {

        return join(items, separator, null);
    }


    /**
     * Joins list of string items to a single string, where items are separated
     * with a defined separator.
     *
     * @param map       to join into key=value list
     * @param separator to be used between elements
     * @param <T>       key class type
     * @param <V>       value class type
     * @return map key values joined into a single string
     */
    public static <T, V> String join(Map<T, V> map, String separator) {

        if (separator == null || separator.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("Missing separator!");
        }

        StringBuilder output = new StringBuilder();
        if (map != null) {
            for (Object key : map.keySet()) {
                if (output.length() > 0) {
                    output.append(separator);
                }

                output.append(key).append("=").append(map.get(key));
            }
        }

        return output.toString();
    }

    /**
     * Extracts words from text removing non alpha characters
     *
     * @param text to extract words from
     * @return list of found words or empty list if none found
     */
    public static List<String> getWords(String text) {

        List<String> output = new ArrayList<>();
        if (isNullOrEmptyTrimmed(text)) {
            return output;
        }

        Matcher m = WORD_PATTERN.matcher(text);
        while (m.find()) {
            output.add(m.group());
        }

        return output;
    }

    /**
     * Removes punctuation from text
     *
     * @param text to remove punctuation from
     * @return cleaned up text or original if nothing to remove
     */
    public static String removePunctuation(String text) {

        if (text == null) {
            return null;
        }

        return text.replaceAll(PUNCTUATION_PATTERN, "");
    }

    /**
     * Wrapper around String.split(regEx) method
     *
     * @param text  to split
     * @param regEx regular expression to use for splitting
     * @return list of strings or empty list if could not split
     */
    public static List<String> split(String text, String regEx) {

        if (isNullOrEmptyTrimmed(text)) {
            return Collections.emptyList();
        }

        String[] items = text.trim().split(regEx);
        return Arrays.asList(items);
    }

    /**
     * Reduces text to max given size preserving words
     *
     * @param text to trim down
     * @param size max desired size
     * @return trimmed down text with "..." or original text if fitting size
     */
    public static String trimTextDown(String text, int size) {

        if (text == null || text.length() <= size) {
            return text;
        }

        int pos = text.lastIndexOf(" ", size);

        if (pos < 0) {
            return text.substring(0, size);
        }

        return text.substring(0, pos);
    }

    /**
     * Reduces text size to a given size
     *
     * @param text            to reduce
     * @param sizeMinusAppend size final string should be max long
     * @param append          string to append at the end to indicate trimming
     * @return trimmed text with appended text of given max size, or original text if shorter than desired
     */
    public static String trimTextDown(String text, int sizeMinusAppend, String append) {

        Assert.notNull(append, "Missing append!");

        if (text == null || text.length() <= sizeMinusAppend) {
            return text;
        }

        sizeMinusAppend = sizeMinusAppend - append.length();

        int pos = text.lastIndexOf(" ", sizeMinusAppend);

        if (pos < 0) {
            return text.substring(0, sizeMinusAppend) + append;
        }

        return text.substring(0, pos) + append;
    }

    /**
     * NPE safe toString call
     *
     * @param value object to call toString upon
     * @return toString value or null if object is null
     */
    public static String toStringOrNull(Object value) {

        return null != value ? value.toString() : null;
    }

    /**
     * Converts text to list of characters
     *
     * @param text to be converted
     * @return list of characters
     */
    public static List<String> asListOfChars(String text) {

        List<String> list = new ArrayList<>();

        if (StringUtils.isNullOrEmptyTrimmed(text)) {
            return list;
        }

        for (int i = 0; i < text.length(); i++) {
            list.add(Character.toString(text.charAt(i)));
        }

        return list;
    }

    /**
     * Checks if given string is a single word (doesn't accepts words with "-" as a single word!)
     *
     * @param word to test
     * @return true if word, false otherwise
     */
    public static boolean isWord(String word) {

        if (isNullOrEmptyTrimmed(word)) {
            return false;
        }

        List<String> list = getWords(word);
        return list.size() == 1;
    }

    /**
     * Calculates matching relevance between given string and search expression
     *
     * @param value  to search in
     * @param search to search for
     * @return -1 not relevant, 0..N - where lower values represents more relevant results
     */
    public static int relevance(String value, String search) {

        if (StringUtils.isNullOrEmptyTrimmed(value) ||
                StringUtils.isNullOrEmptyTrimmed(search)) {
            return -1;
        }

        if (search.length() > value.length()) {
            return -1;
        }

        int relevance = -1; // start at -1 ... so -1 is returned for no result
        int delta = 1; // first delta at 1 ... producing a sum of 0 when first letter is found

        int len = value.length();
        int searchLen = search.length();
        int letterIndex = 0;

        for (int searchIndex = 0; searchIndex < searchLen; searchIndex++) {

            char match = search.charAt(searchIndex);

            while (letterIndex < len) {
                char letter = value.charAt(letterIndex);
                letterIndex++;

                if (match == letter) {
                    relevance = relevance + delta; // reverse to get higher value for better match
                    delta = 0;
                    break;
                } else {
                    delta++;
                }
            }

            // we matched all characters ... and found the last one ...
            if (delta == 0 && searchIndex == searchLen - 1) {
                return relevance;
            }

            if (letterIndex == len) {
                break;
            }
        }

        return -1;
    }

    /**
     * Removes all multiple-consecutive whitespace characters (space, tab, newline) and replaces them with single space.
     * Also removes leading and trailing spaces.
     *
     * @param input to be sanitized
     * @return sanitized output
     * @throws IllegalArgumentException in case input is null or empty
     */
    public static String sanitizeWhitespace(String input) {

        Assert.notNull(input, "Missing input!");
        input = input.replaceAll(WHITE_SPACE_PATTERN, " ");
        return input.trim();
    }

    /**
     * Removes all multiple-consecutive whitespace characters (space, tab, newline) and replaces them with single space.
     * Also removes leading and trailing spaces.
     *
     * @param input text to be sanitized as byte array
     * @return sanitized byte array
     */
    public static byte[] sanitizeWhitespace(byte[] input) {

        Assert.isFalse(input == null || input.length == 0, "Missing input!");

        String stringFileData = new String(input, StandardCharsets.UTF_8);
        return sanitizeWhitespace(stringFileData).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Simple enumeration: first, second, third ... 5th, 6th .. etc for given number
     *
     * @param number to be enumerated (must be grated than 0)
     * @return enumeration or null if not applicable
     */
    public static String enumerate(Integer number) {

        if (number == null || number <= 0) {
            return null;
        }

        int lastDigit = number % 10;

        switch (number) { // special case for first five
            case 1:
                return "first";
            case 2:
                return "second";
            case 3:
                return "third";
            case 4:
                return "fourth";
            case 5:
                return "fifth";

            default:
                switch (lastDigit) {
                    case 1:
                        return number + "st";
                    case 2:
                        return number + "nd";
                    case 3:
                        return number + "rd";

                    default:
                        return number + "th";
                }
        }
    }

    /**
     * Removes double or single quotes if any are present
     *
     * @param text to remove double quotes from
     * @return un quoted string
     */
    public static String unQuote(String text) {

        if (isNullOrEmptyTrimmed(text)) {
            return text;
        }

        if ((text.startsWith("\"") && text.endsWith("\"")) ||
                (text.startsWith("'") && text.endsWith("'"))) {
            return text.substring(1, text.length() - 1);
        }

        return text;
    }

    /**
     * Pads string on left side
     *
     * @param toBePadded    string to be padded
     * @param paddingChar   char to use for padding
     * @param desiredLength max desired length
     * @return padded string or original if padding is not possible
     */
    public static String padLeft(String toBePadded, char paddingChar, int desiredLength) {
        if (toBePadded == null || toBePadded.length() >= desiredLength) {
            return toBePadded;
        }

        desiredLength = desiredLength - toBePadded.length();
        toBePadded = String.valueOf(paddingChar).repeat(desiredLength) + toBePadded;
        return toBePadded;
    }

    /**
     * Pads string on right side
     *
     * @param toBePadded    string to be padded
     * @param paddingChar   char to use for padding
     * @param desiredLength max desired length
     * @return padded string or original if padding is not possible
     */
    public static String padRight(String toBePadded, char paddingChar, int desiredLength) {
        if (toBePadded == null || toBePadded.length() >= desiredLength) {
            return toBePadded;
        }

        desiredLength = desiredLength - toBePadded.length();
        toBePadded = toBePadded + String.valueOf(paddingChar).repeat(desiredLength);

        return toBePadded;
    }
}