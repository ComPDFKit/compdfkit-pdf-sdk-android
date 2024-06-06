/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.colorpicker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.compdfkit.tools.R;

public class ColorRectShowView extends View {
    private Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF colorRectF = new RectF();

    private Bitmap bgBitmap;

    private Paint bitmapPaint = new Paint();

    private ColorPickerData colorPickerData = new ColorPickerData(Color.WHITE, 255);

    public ColorRectShowView(Context context) {
        super(context);
        init();
    }

    public ColorRectShowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorRectShowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForceDarkAllowed(false);
        }
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tools_color_picker_bg_color_rect);
    }


    public void changeColor(int color) {
        colorPickerData.color = color;
        invalidate();
    }

    public void changeAlpha(int alpha) {
        colorPickerData.alpha = alpha;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (bgBitmap != null) {
            colorRectF.set(0F, 0F, getWidth(), getHeight());
            canvas.drawBitmap(bgBitmap, null, colorRectF, bitmapPaint);
            canvas.drawRect(colorRectF, backPaint);
        }

    }

    @Override
    public void invalidate() {
        backPaint.setColor(colorPickerData != null ? colorPickerData.color : Color.WHITE);
        backPaint.setAlpha(colorPickerData != null ? colorPickerData.alpha : 255);
        super.invalidate();
    }

    public int getColor(){
        return colorPickerData.color;
    }

    public int getColorAlpha(){
        return colorPickerData.alpha;
    }


}
