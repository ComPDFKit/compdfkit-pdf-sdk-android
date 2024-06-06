package com.compdfkit.tools.viewer.pdfsearch.bean;

import android.content.Context;
import android.text.TextUtils;

import com.compdfkit.core.edit.CPDFEditFindSelection;
import com.compdfkit.core.edit.CPDFEditRange;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextRange;

public class CEditSearchReplaceInfo extends CSearchTextInfo {
    public CPDFEditFindSelection selection;

    public CEditSearchReplaceInfo(Context context, int page, String keyword, boolean head, int textRangeIndex, CPDFEditFindSelection content) {
        super(context, page, keyword,  textRangeIndex, head);
        this.selection = content;
    }

    public void initHighLightTextData(Context context, CPDFTextPage textPage){
        if (textPage == null || context == null) {
            return;
        }
        CPDFEditRange textRange = this.selection.getRange();
        if (textRange == null) {
            return;
        }

        int targetStart = textRange.getLocation() - 20;
        int length;
        if (targetStart > 0) {
            length = textRange.getLength() + 40;
        } else {
            length = textRange.getLength() + 40 + targetStart;
            targetStart = 0;
        }
        CPDFTextRange targetTextRange = new CPDFTextRange(targetStart, length);
        String target = textPage.getText(targetTextRange);
        if (target == null) {
            return;
        }
        target = target.replaceAll("(\\r\\n)+ ", "");
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        this.stringBuilder = highlight(context, target, keyword);
    }
}
