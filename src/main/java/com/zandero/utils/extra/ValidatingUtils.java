package com.zandero.utils.extra;

import com.zandero.utils.StringUtils;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.validator.routines.UrlValidator;

public final class ValidatingUtils {
  private static final String REG_EX_CHARS = "'[{'\\^$.|?*+(";
  private static final Pattern IP_V4_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
  
  private ValidatingUtils() {
  }

  public static boolean isUrl(String value) {
    return !StringUtils.isNullOrEmptyTrimmed(value) && UrlValidator.getInstance().isValid(value);
  }

  /**
   * Utility to find out if given string is a regular expression
   * Strings without special regular expression characters are not considered regular expressions,
   * but they technically are
   *
   * @param value to check if regular expression
   * @return true if regular expression, false otherwise
   */
  public static boolean isRegEx(String value) {

    if (StringUtils.isNullOrEmptyTrimmed(value)) {
      return false;
    }

    if (value.chars().noneMatch(ch -> REG_EX_CHARS.indexOf(ch) > 0)) {
      return false;
    }

    try {
      Pattern.compile(value);
      return true;
    }
    catch (PatternSyntaxException e) {
      return false;
    }
  }
}