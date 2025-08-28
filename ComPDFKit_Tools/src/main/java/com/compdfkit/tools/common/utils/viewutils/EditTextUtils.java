/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.viewutils;

import static com.compdfkit.tools.common.utils.CStringUtils.EMOJI_PATTERN;

import android.text.InputFilter;


public class EditTextUtils {

    public static InputFilter inputRangeFilter(int minValue , int maxValue){
        return (source, start, end, dest, dstart, dend) -> {
            try {
                String newInput = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend);
                int input = Integer.parseInt(newInput);

                if (input >= minValue && input <= maxValue) {
                    return null;
                }
            } catch (NumberFormatException e) {
            }
            return "";
        };
    }

    public static InputFilter emojiFilter(){
        return (source, start, end, dest, dstart, dend) -> {
            if (source == null) return null;
            if (EMOJI_PATTERN.matcher(source).find()) {
                // 过滤 emoji
                return EMOJI_PATTERN.matcher(source).replaceAll("");
            }
            return null; // 保持原样
        };
    }

}
