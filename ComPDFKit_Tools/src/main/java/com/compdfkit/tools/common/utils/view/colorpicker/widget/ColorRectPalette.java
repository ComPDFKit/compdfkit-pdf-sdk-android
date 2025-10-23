/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.colorpicker.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

class ColorRectPalette extends View {
    private RectF rect = new RectF(0f, 0f, 0f, 0f);
    private Paint backPaint = new Paint();
    private Paint verticalPaint = new Paint();

    public ColorRectPalette(Context context) {
        this(context, null);
    }

    public ColorRectPalette(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorRectPalette(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForceDarkAllowed(false);
        }
        backPaint.setAntiAlias(true);
        backPaint.setStyle(Paint.Style.FILL);
        verticalPaint.setAntiAlias(true);
        verticalPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        rect = new RectF(0f, 0f, w, h);
        backPaint.setShader(new LinearGradient(0f, 0f, w, 0f, generateStretchColorArray(),
                null, Shader.TileMode.CLAMP));
        verticalPaint.setShader(new LinearGradient(0f, 0f, 0f, h, Color.TRANSPARENT, Color.WHITE,
                Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(rect, backPaint);
        canvas.drawRect(rect, verticalPaint);
    }

    private int[] generateStretchColorArray() {
        int[] colorArray = new int[361];
        int count = 0;
        int i = colorArray.length - 1;
        while (i >= 0) {
            colorArray[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
            i--;
            count++;
        }
        return colorArray;
    }
}