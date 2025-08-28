package com.compdfkit.tools.common.utils;

import java.util.regex.Pattern;


public class CStringUtils {
  public static final Pattern EMOJI_PATTERN = Pattern.compile(
      "[\uD83C-\uDBFF\uDC00-\uDFFF]+|" +
          "[\u2600-\u27FF]|" +
          "[\uFE0F]"
  );

  public static String filterEmoji(String source) {
    if (source == null) {
      return null;
    }
    return EMOJI_PATTERN.matcher(source).replaceAll("");
  }

  public static boolean containsEmoji(String input) {
    if (input == null) return false;

    int len = input.length();
    for (int i = 0; i < len; ) {
      int codePoint = input.codePointAt(i);
      if (isEmoji(codePoint)) {
        return true;
      }
      i += Character.charCount(codePoint);
    }
    return false;
  }

  private static boolean isEmoji(int codePoint) {
    return (codePoint >= 0x1F600 && codePoint <= 0x1F64F)   // Emoticons
        || (codePoint >= 0x1F300 && codePoint <= 0x1F5FF)   // Misc Symbols and Pictographs
        || (codePoint >= 0x1F680 && codePoint <= 0x1F6FF)   // Transport & Map
        || (codePoint >= 0x2600 && codePoint <= 0x26FF)     // Misc symbols
        || (codePoint >= 0x2700 && codePoint <= 0x27BF)     // Dingbats
        || (codePoint >= 0x1F900 && codePoint <= 0x1F9FF)   // Supplemental Symbols
        || (codePoint >= 0x1FA70 && codePoint <= 0x1FAFF);  // Extended pictographs
  }

}
