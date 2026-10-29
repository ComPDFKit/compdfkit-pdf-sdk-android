package com.compdfkit.tools.viewer.pdfsearch.bean;

import android.content.Context;
import android.text.TextUtils;

import com.compdfkit.core.edit.CPDFEditFindSelection;
import com.compdfkit.core.edit.CPDFEditRange;
import com.compdfkit.core.page.CPDFTextPage;

public class CEditSearchReplaceInfo extends CSearchTextInfo {
    public CPDFEditFindSelection selection;

    public CEditSearchReplaceInfo(Context context, int page, String keyword, boolean head, int textRangeIndex, CPDFEditFindSelection content) {
        super(context, page, keyword,  textRangeIndex, head);
        this.selection = content;
    }

    public void initHighLightTextData(Context context, CPDFTextPage textPage){
        if (textPage == null || context == null || selection == null || TextUtils.isEmpty(keyword)) {
            return;
        }
        CPDFEditRange textRange = this.selection.getRange();
        if (textRange == null) {
            return;
        }
        this.stringBuilder = buildHighlightedSnippet(context, textPage,
                textRange.getLocation(), textRange.getLength());
    }
}
