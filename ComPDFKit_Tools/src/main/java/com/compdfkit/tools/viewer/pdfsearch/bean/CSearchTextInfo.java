/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
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
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;

import androidx.core.content.ContextCompat;

import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextRange;
import com.compdfkit.tools.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CSearchTextInfo {

    /** Characters of surrounding text kept on each side of a match in the result list. */
    protected static final int CONTEXT_CHARS = 20;

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
        this.stringBuilder = buildHighlightedSnippet(context, textPage, textRange.location, textRange.length);
    }

    /**
     * Build the context snippet shown in the search result list, highlighting <b>only</b> the
     * match at {@code [location, location + length)}.
     *
     * <p>The snippet is assembled from three separate reads — the leading context, the match
     * itself and the trailing context — so the highlight is anchored to the offsets the
     * searcher reported instead of being re-derived by scanning the snippet for the keyword.
     * Scanning would also light up any other occurrence that happens to fall inside the
     * surrounding {@value #CONTEXT_CHARS} characters.
     *
     * <p>If the text at those offsets does not turn out to be the keyword, the offsets cannot
     * be trusted (the range and the text page are indexed differently); the method then falls
     * back to the historical keyword-scanning behaviour rather than highlighting the wrong
     * span.
     *
     * @return the snippet, or {@code null} when there is nothing to render.
     */
    protected SpannableStringBuilder buildHighlightedSnippet(Context context, CPDFTextPage textPage, int location, int length) {
        if (context == null || textPage == null || location < 0 || length <= 0) {
            return null;
        }
        int prefixStart = Math.max(0, location - CONTEXT_CHARS);
        String prefix = prefixStart < location
                ? readText(textPage, prefixStart, location - prefixStart)
                : "";
        String match = readText(textPage, location, length);
        String suffix = readText(textPage, location + length, CONTEXT_CHARS);

        if (TextUtils.isEmpty(keyword) || !match.trim().equalsIgnoreCase(keyword.trim())) {
            // Offsets do not line up with this text page: keep the old behaviour so the row is
            // still readable, even though it may highlight more than the actual match.
            return highlight(context, clean(prefix + match + suffix), keyword);
        }

        prefix = clean(prefix);
        match = clean(match);
        suffix = clean(suffix);
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        spannable.append(prefix).append(match).append(suffix);
        if (match.length() > 0) {
            int backGroundColor = ContextCompat.getColor(context, R.color.tools_search_result_text_highlight);
            spannable.setSpan(new BackgroundColorSpan(backGroundColor), prefix.length(),
                    prefix.length() + match.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    private static String readText(CPDFTextPage textPage, int location, int length) {
        if (length <= 0) {
            return "";
        }
        String text = textPage.getText(new CPDFTextRange(location, length));
        return text == null ? "" : text;
    }

    /** Collapse the hard line wraps the text page reports so the snippet reads as one line. */
    private static String clean(String text) {
        return text.replaceAll("(\\r\\n)+ ", "");
    }

    /**
     * Highlight keywords
     *
     * @param text   The text to be displayed
     * @param target The keyword to be highlighted
     * @return spannable The processed result, remember not to use toString(), otherwise it won't work
     * @deprecated highlights every occurrence of {@code target} in {@code text}, not the match
     * the searcher actually found. Use
     * {@link #buildHighlightedSnippet(Context, CPDFTextPage, int, int)} instead; this remains
     * only as the fallback for ranges that cannot be mapped onto the text page.
     */
    @Deprecated
    protected SpannableStringBuilder highlight(Context context, String text, String target) {
        if (TextUtils.isEmpty(target)) {
            return new SpannableStringBuilder(text);
        }
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span;
        Pattern p = Pattern.compile(Pattern.quote(target), Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        int backGroundColor = ContextCompat.getColor(context, R.color.tools_search_result_text_highlight);
        while (m.find()) {
            span = new BackgroundColorSpan(backGroundColor);
            spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
}
