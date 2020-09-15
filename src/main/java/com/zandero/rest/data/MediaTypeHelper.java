package com.zandero.rest.data;

import com.zandero.utils.*;
import org.slf4j.*;

import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 *
 */
public final class MediaTypeHelper {

    private final static Logger log = LoggerFactory.getLogger(MediaTypeHelper.class);

    private MediaTypeHelper() {
        // hide constructor
    }

    public static String getKey(MediaType mediaType) {

        if (mediaType == null) {
            return MediaType.WILDCARD;
        }

        return mediaType.getType() + "/" + mediaType.getSubtype(); // key does not contain any charset
    }

    public static MediaType valueOf(String mediaType) {

        try {
            return parse(mediaType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static MediaType[] getMediaTypes(String[] value) {

        MediaType[] types = new MediaType[]{};
        for (String item : value) {

            String[] items = item.split(","); // split by "," ...

            for (String single : items) {
                MediaType type = MediaTypeHelper.valueOf(single);
                if (type != null) {
                    types = join(types, type);
                }
            }
        }

        if (types.length == 0) {
            return null;
        }

        return types;
    }

    /**
     * Simple parsing utility to get MediaType
     *
     * @param mediaType to be parsed
     * @return media type of throw IllegalArgumentException if parsing fails
     */
    private static MediaType parse(String mediaType) {

        Assert.notNullOrEmptyTrimmed(mediaType, "Missing media type!");

        String type = null;
        String subType = null;
        Map<String, String> params = new HashMap<>();

        String[] parts = mediaType.split(";");
        for (int i = 0; i < parts.length; i++) {

            String part = StringUtils.trimToNull(parts[i]);
            if (part == null) {
                continue;
            }

            if (i == 0) {
                // get type and subtype
                String[] typeSubType = part.split("/");
                Assert.isTrue(typeSubType.length == 2, "Missing or invalid type with subType: '" + part + "'");

                type = StringUtils.trimToNull(typeSubType[0]);
                subType = StringUtils.trimToNull(typeSubType[1]);

                Assert.notNull(type, "Missing media type!");
                Assert.notNull(subType, "Missing media sub-type!");

            } else {

                try {
                    Pair<String, String> pair = getNameValue(part);
                    params.put(pair.getKey(), pair.getValue());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid media type option: ", e);
                }
            }
        }

        return new MediaType(type, subType, params);
    }

    private static Pair<String, String> getNameValue(String part) {

        String[] nameValue = part.split("=");
        Assert.isTrue(nameValue.length == 2, "Invalid param format: '" + part + "', expected name=value!");

        String name = StringUtils.trimToNull(nameValue[0]);
        String value = StringUtils.trimToNull(nameValue[1]);

        Assert.notNull(name, "Missing param name: '" + part + "'!");
        Assert.notNull(value, "Missing param value: '" + part + "'!");

        return new Pair<>(StringUtils.trim(nameValue[0]), nameValue[1]);
    }

    public static String toString(MediaType produces) {

        StringBuilder builder = new StringBuilder();
        builder.append(produces.getType()).append("/").append(produces.getSubtype());

        if (produces.getParameters().size() > 0) {
            for (String key : produces.getParameters().keySet()) {
                builder.append(";");
                builder.append(key).append("=").append(produces.getParameters().get(key));
            }
        }

        return builder.toString();
    }

    public static boolean notDefaultMediaType(MediaType[] values) {
        return !isDefaultMediaType(values);
    }

    public static boolean isDefaultMediaType(MediaType[] values) {
        return values != null &&
                   values.length == 1 &&
                   values[0].isWildcardType() &&
                   values[0].isWildcardSubtype();
    }

    public static boolean matches(MediaType desiredMediaType, MediaType producedMediaType) {
        if (desiredMediaType == null) {
            return false;
        }

        return desiredMediaType.isCompatible(producedMediaType);
    }

    public static MediaType[] join(MediaType[] base, MediaType additional) {
        if (additional == null) {
            return base;
        }

        return join(base, new MediaType[]{additional});
    }

    public static MediaType[] join(MediaType[] base, MediaType[] additional) {
        if (additional == null || additional.length == 0) {
            return base;
        }

        if (base == null || base.length == 0) {
            base = additional; // let's make sure we have no duplicates
        }

        Set<MediaType> baseSet = new LinkedHashSet<>(Arrays.asList(base));
        Set<MediaType> addSet = new LinkedHashSet<>(Arrays.asList(additional));

        HashMap<String, MediaType> joinedSet = new LinkedHashMap<>();
        for (MediaType baseItem : baseSet) {

            MediaType found = null;
            for (MediaType addItem : addSet) {
                if (baseItem.getType().equals(addItem.getType()) && baseItem.getSubtype().equals(addItem.getSubtype())) {
                    // join parameters
                    for (String key : addItem.getParameters().keySet()) {
                        String value = addItem.getParameters().get(key);

                        // join parameters ...
                        Map<String, String> params = new HashMap<>(baseItem.getParameters());
                        params.putIfAbsent(key, value);

                        baseItem = new MediaType(baseItem.getType(), baseItem.getSubtype(), params);
                    }

                    found = addItem;
                    break;
                }
            }

            if (found != null) {
                addSet.remove(found);
            }

            joinedSet.put(baseItem.getType() + "/" + baseItem.getSubtype(), baseItem);
        }

        // add remaining from addSet
        for (MediaType addItem : addSet) {
            joinedSet.put(addItem.getType() + "/" + addItem.getSubtype(), addItem);
        }

        return joinedSet.values().toArray(new MediaType[]{});
    }
}
