/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.stamp;

import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;


public class CPDFStaticLayout extends StaticLayout {

    private CharSequence content;

    private float fontSize;

    private TextPaint textPaint;

    public CPDFStaticLayout(CharSequence source, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        super(source, paint, width, align, spacingmult, spacingadd, includepad);
        this.content = source;
        this.fontSize = paint.getTextSize();
        this.textPaint = paint;
    }

    public CPDFStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        super(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad);
        this.content = source;
        this.fontSize = paint.getTextSize();
        this.textPaint = paint;
    }

    public CPDFStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        super(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth);
        this.content = source;
        this.fontSize = paint.getTextSize();
        this.textPaint = paint;
    }

    public String getLineFeedContent() {
        float width = getWidth();
        StringBuilder lineFeedContent = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        int index = 0;
        while (index < content.length()) {
            char c = content.charAt(index);
            if (c == '\n') {
                lineFeedContent.append(temp.toString() + '\n');
                temp.delete(0, temp.length());
                index++;
                continue;
            }
            temp.append(c);
            if (textPaint.measureText(temp.toString()) > width && temp.length() > 1) {
                lineFeedContent.append(temp.substring(0, temp.length() - 1) + '\n');
                temp.delete(0, temp.length());
                temp.append(c);
            } else if (textPaint.measureText(temp.toString()) > width && temp.length() == 1) {
                lineFeedContent.append(temp.toString() + '\n');
                temp.delete(0, temp.length());
            }
            index++;
        }
        lineFeedContent.append(temp);

        return lineFeedContent.toString();
    }
}
