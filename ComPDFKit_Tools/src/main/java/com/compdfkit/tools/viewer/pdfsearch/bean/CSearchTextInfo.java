/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfsearch.bean;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;

import androidx.core.content.ContextCompat;

import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextRange;
import com.compdfkit.tools.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CSearchTextInfo {

    private Context context;

    public int page;

    public String keyword;

    public SpannableStringBuilder stringBuilder;

    public boolean isHeader;

    public int textRangeIndex;

    public CSearchTextInfo(Context context, int page, String keyword, int textRangeIndex, boolean isHeader) {
        this.context = context;
        this.page = page;
        this.keyword = keyword;
        this.isHeader = isHeader;
        this.textRangeIndex = textRangeIndex;
    }

    public void initHighLightTextData(Context context, CPDFTextPage textPage, CPDFTextRange textRange){
        if (textPage == null || textRange == null){
            return;
        }
        int targetStart = textRange.location - 20;
        int length;
        if (targetStart > 0) {
            length = textRange.length + 40;
        } else {
            length = textRange.length + 40 + targetStart;
            targetStart = 0;
        }
        CPDFTextRange targetTextRange = new CPDFTextRange(targetStart, length);
        String target = textPage.getText(targetTextRange);

        this.stringBuilder = highlight(context, target.toLowerCase(), keyword.toLowerCase());

    }

    /**
     * Highlight keywords
     *
     * @param text   The text to be displayed
     * @param target The keyword to be highlighted
     * @return spannable The processed result, remember not to use toString(), otherwise it won't work
     */
    private SpannableStringBuilder highlight(Context context, String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span;
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        int backGroundColor = ContextCompat.getColor(context, R.color.tools_search_result_text_highlight);
        while (m.find()) {
            span = new BackgroundColorSpan(backGroundColor);
            spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
}
