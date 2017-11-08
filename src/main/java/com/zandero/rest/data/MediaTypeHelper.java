package com.zandero.rest.data;

import com.zandero.utils.StringUtils;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public final class MediaTypeHelper {

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

		if (StringUtils.isNullOrEmptyTrimmed(mediaType)) {
			return null;
		}

		try {
			return MediaType.valueOf(mediaType);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}
}
