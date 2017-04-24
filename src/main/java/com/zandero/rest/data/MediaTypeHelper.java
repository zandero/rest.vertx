package com.zandero.rest.data;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public final class MediaTypeHelper {

	public static String getKey(MediaType mediaType) {

		if (mediaType == null) {
			return MediaType.WILDCARD;
		}

		return mediaType.getType() + "/" + mediaType.getSubtype(); // remove charset if present when searching for
	}
}
