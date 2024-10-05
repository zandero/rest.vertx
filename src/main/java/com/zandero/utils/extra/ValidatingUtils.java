package com.zandero.utils.extra;


import com.zandero.utils.StringUtils;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.UrlValidator;

public final class ValidatingUtils {
  private static final String REG_EX_CHARS = "'[{'\\^$.|?*+(";
  private static final Pattern IP_V4_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
  
  private ValidatingUtils() {
  }

  public static boolean isUrl(String value) {
    return !StringUtils.isNullOrEmptyTrimmed(value) && UrlValidator.getInstance().isValid(value);
  }
  
  
}