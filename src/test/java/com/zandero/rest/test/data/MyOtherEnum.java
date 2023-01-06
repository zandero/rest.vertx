package com.zandero.rest.test.data;

import com.zandero.utils.*;

/**
 *
 */
public enum MyOtherEnum {

	one(1),
	two(2),
	three(3);

	private final String parseValue;

	MyOtherEnum(int value) {
		this.parseValue = "" + value;
	}

	public static MyOtherEnum fromString(String value) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return null;
		}

		for (MyOtherEnum item: values()) {
			if (item.parseValue.equals(value)) {
				return item;
			}
		}

		return valueOf(value);
	}
}
