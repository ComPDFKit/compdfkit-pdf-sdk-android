/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.span;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class CWavyUnderLineSpan implements LineBackgroundSpan {
    private int color;

    private int alpha = 255;

    private int lineWidth = 3;

    private int waveSize;

    public CWavyUnderLineSpan() {
        this(Color.RED, 1, 3);
    }

    public CWavyUnderLineSpan(int color, int alpha, int waveSize) {
        this.color = color;
        this.lineWidth = 3;
        this.alpha = alpha;
        this.waveSize = waveSize;
    }


    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lnum) {
        Paint p = new Paint(paint);
        p.setColor(color);
        p.setAlpha(alpha);
        p.setStrokeWidth(lineWidth);
        p.setStyle(Paint.Style.FILL);

        int width = (int) paint.measureText(text, start, end);
        int doubleWaveSize = waveSize * 2;
        for (int i = left; i < left + width; i += doubleWaveSize) {
            canvas.drawLine(i, bottom, i + waveSize, bottom - waveSize, p);
            canvas.drawLine(i + waveSize, bottom - waveSize, i + doubleWaveSize, bottom, p);
        }
    }
}