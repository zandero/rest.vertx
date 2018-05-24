package com.zandero.rest.test.data;

import com.zandero.utils.StringUtils;

/**
 *
 */
public enum MyOtherEnum {

	one,
	two,
	three;

	public static MyOtherEnum fromString(String value) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return null;
		}

		switch (value) {
			case "1" : return one;
			case "2" : return two;
			case "3" : return three;
		}

		return valueOf(value);
	}
}
