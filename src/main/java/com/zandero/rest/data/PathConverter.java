package com.zandero.rest.data;

import com.zandero.utils.StringUtils;
import com.zandero.utils.extra.ValidatingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts {path}/{subPath} into Vert.X path matching format
 * Also handles regular expression cases
 */
public final class PathConverter {

	private PathConverter() {
		// hide constructor
	}

	/**
	 * Extract method argument parameters from path definition
	 *
	 * @param path to extract from
	 * @return list of method parameters or empty list if none found
	 */
	static List<MethodParameter> extract(String path) {

		List<MethodParameter> output = new ArrayList<>();

		List<String> items = split(path);

		if (items.size() > 0) {

			int pathIndex = 0;
			int index = 0;
			for (String item : items) {

				MethodParameter param = getParamFromPath(item, index, pathIndex); // set negative index until confirmed that needed

				if (param != null) {
					output.add(param);
					index++;
				}

				pathIndex++;
			}
		}

		return output;
	}

	static List<String> split(String path) {

		List<String> out = new ArrayList<>();

		char[] chars = path.toCharArray();

		int bracket = 0;

		//StringBuilder out = new StringBuilder();
		String collected = "";

		for (char c : chars) {

			if (c != '/' || bracket > 0) {
				collected += c;
			}

			switch (c) {
				case '{':
					bracket++;
					break;

				case '}':
					bracket--;
					break;

				// split on new path except when in {}
				case '/':
					if (bracket == 0) {
						out.add(collected);
						collected = "";
					}
					break;
			}
		}

		if (collected.length() > 0) {
			out.add(collected);
		}

		return out;
	}

	static String convert(String path) {

		// 1. split into groups with /{} (if any)
		List<String> paths = split(path);

		// 2. convert each group
		paths = paths.stream().map(PathConverter::convertSub).collect(Collectors.toList());

		// 3. join back together into single path
		return StringUtils.join(paths, "/");
	}


	/**
	 * Converts {@code {path}} definitions to vert.x {@code :path}
	 * also takes care of regular expressions in case they are present in path
	 *
	 * @param path to be converted
	 * @return vertx. path format
	 *//*
	static String convertSubPath(String path) {

		StringBuilder out = new StringBuilder();
		String[] items = path.split("/");
		if (items.length > 0) {

			boolean addTrailing = path.endsWith("/");

			for (int index = 0; index < items.length; index++) {

				String item = items[index];
				item = convertSub(item);

				out.append(item);
				if (index + 1 < items.length) {
					out.append("/");
				}
			}

			if (addTrailing) {
				out.append("/");
			}
		}

		return out.toString();
	}*/

	private static MethodParameter getParamFromPath(String path, int regExIndex, int pathIndex) {

		if (StringUtils.isNullOrEmptyTrimmed(path)) {
			return null;
		}

		if (isRestEasyPath(path)) {
			return getRestEasyParam(path, regExIndex, pathIndex);
		}

		// Regular named parameter
		int index = path.lastIndexOf(":");
		if (index == 0) {
			path = path.substring(1); // is vert.x path
			MethodParameter parameter = new MethodParameter(ParameterType.path, path);
			parameter.setPathIndex(pathIndex);
			return parameter;
		}

		// VertX definition of RegEx
		if (ValidatingUtils.isRegEx(path)) {

			String name = "param" + regExIndex; // Vert.X name ... no other option here
			MethodParameter parameter = new MethodParameter(ParameterType.path, name);
			parameter.setPathIndex(pathIndex);
			parameter.setRegEx(path, regExIndex);
			return parameter;
		}

		return null;
	}


	private static MethodParameter getRestEasyParam(String path, int paramIndex, int pathIndex) {

		// remove front and back {}
		path = path.substring(1, path.length() - 1); // create vert.x form
		int index = path.lastIndexOf(":");
		// RestEasy definition of RexEx
		if (index <= 0) {
			MethodParameter parameter = new MethodParameter(ParameterType.path, path);
			parameter.setPathIndex(pathIndex);
			return parameter;
		}

		// regular expression
		String name = path.substring(0, index);
		String regEx = path.substring(index + 1);

		MethodParameter parameter = new MethodParameter(ParameterType.path, name);
		parameter.setPathIndex(pathIndex);
		parameter.setRegEx(regEx, paramIndex);
		return parameter;
	}

	/**
	 * Converts from RestEasy to Vert.X format if necessary
	 *
	 * @param path to be converted
	 * @return vert.x path format
	 */
	private static String convertSub(String path) {

		if (isRestEasyPath(path)) {

			// remove {}
			path = path.substring(1, path.length() - 1);

			int index = path.lastIndexOf(":");  // check for regular expression
			if (index > 0 && index + 1 < path.length()) {
				return path.substring(index + 1); // make regular expression the path part
			}

			return ":" + path; // put : in front of param name
		}

		return path;
	}

	private static boolean isRestEasyPath(String path) {

		path = StringUtils.trimToNull(path);
		return path != null && path.startsWith("{") && path.endsWith("}") && path.length() > 2;
	}

	/**
	 * Removes double "//" if any ... and trims whitespace
	 *
	 * @param path to clean
	 * @return cleaned up path
	 */
	public static String clean(String path) {

		path = path.replaceAll("//", "/");
		return StringUtils.trimToNull(path);
	}
}
