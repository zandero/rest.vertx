package com.zandero.rest.data;

import com.zandero.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts {path}/{subPath} into Vert.X path matching format
 */
public final class PathConverter {

	private static final Pattern REG_EX = Pattern.compile("(\\{[^}]+\\})");

	private static final Pattern VERTX_REG_EX = Pattern.compile("(:*+)");

	private PathConverter() {
		// hide constructor
	}

	/**
	 * Converts {@code {path}} definitions to vert.x {@code :path}
	 *
	 * @param path to be converted
	 * @return vertx. path format
	 */
	public static String convert(String path) {

		StringBuffer sb = new StringBuffer();
		Matcher matcher = REG_EX.matcher(path);

		while (matcher.find()) {
			String item = matcher.group(1);
			item = converSub(item);

			if (item != null) {
				matcher.appendReplacement(sb, item);
			}
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private static String converSub(String path) {

		if (path.startsWith("{") && path.endsWith("}")) {

			// remove {}
			path = path.substring(1, path.length() - 1);
			return ":" + path;
		}

		// TODO

		return path;
	}

	public static List<MethodParameter> extract(String path) {

		List<MethodParameter> output = new ArrayList<>();

		String[] items = path.split("/");
		if (items.length > 0) {
			for (String item : items) {
				item = StringUtils.trimToNull(item);

				if (item != null && item.startsWith(":")) {
					item = item.substring(1);
					output.add(new MethodParameter(ParameterType.path, item));
				}
			}
		}

		return output;
	}
}
