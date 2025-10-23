package com.compdfkit.tools.common.utils;

import android.media.MediaCodec.CryptoInfo;
import java.util.regex.Pattern;

/**
 * @classname:
 * @author: LiuXiaoLong
 * @date: 2025/7/17 description:
 */
public class CStringUtils {
  // 正则表达式：匹配所有常见的 emoji 字符
  public static final Pattern EMOJI_PATTERN = Pattern.compile(
      "[\uD83C-\uDBFF\uDC00-\uDFFF]+|" + // surrogate pair 区间
          "[\u2600-\u27FF]|" +                // 杂项符号及象形文字
          "[\uFE0F]"                          // emoji 变体选择符
  );

  /**
   * 过滤字符串中的 emoji 字符
   *
   * @param source 输入字符串
   * @return 过滤后的字符串
   */
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
