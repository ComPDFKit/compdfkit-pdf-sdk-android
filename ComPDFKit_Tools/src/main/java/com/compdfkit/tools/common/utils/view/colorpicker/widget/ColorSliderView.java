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
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.compdfkit.tools.common.interfaces.COnColorSelectListener;
import com.compdfkit.tools.common.utils.view.colorpicker.interfaces.CMotionEventUpdatable;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

public class ColorSliderView extends View implements CMotionEventUpdatable {

    private float circleWidth;

    private float selectorRadiusPx;

    private float selectorHalfWidth;

    private int baseColor = Color.BLUE;

    private Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint outPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float currentValue = 1f;

    private boolean onlyUpdateOnTouchEventUp = false;

    private CThrottledTouchEventHandler handler;

    private PointF currentPoint;

    private COnColorSelectListener onColorSelectListener;

    private RectF roundRect = new RectF();

    public ColorSliderView(Context context) {
        this(context, null);
    }

    public ColorSliderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForceDarkAllowed(false);
        }
        handler = new CThrottledTouchEventHandler(this);
        circleWidth = CDimensUtils.px2dp(context, 12);
        selectorRadiusPx = CDimensUtils.px2dp(context, 80);
        selectorHalfWidth = CDimensUtils.px2dp(context, 30);

        currentPoint = new PointF(selectorHalfWidth, ((float) getHeight() / 2));

        selectorPaint.setColor(Color.WHITE);
        selectorPaint.setStyle(Paint.Style.STROKE);
        selectorPaint.setStrokeWidth(circleWidth);

        outPaint.setColor(0x29000000);
        outPaint.setStyle(Paint.Style.STROKE);
        outPaint.setStrokeWidth(CDimensUtils.px2dp(context, 2));
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
        if (getWidth() != 0) {
            configurePaint();
            float[] hsv = new float[3];
            Color.colorToHSV(this.baseColor, hsv);
            currentValue = hsv[2];
            currentPoint.x = selectorHalfWidth + ((float) getWidth() - 2 * selectorHalfWidth) * (1 - currentValue);
            if (currentPoint.y == 0F) {
                currentPoint.y = getHeight() / 2F;
            }
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        configurePaint();
        if (getWidth() != 0) {
            configurePaint();
            float[] hsv = new float[3];
            Color.colorToHSV(baseColor, hsv);
            currentValue = hsv[2];
            currentPoint.x = selectorHalfWidth + ((float) getWidth() - 2 * selectorHalfWidth) * (1 - currentValue);
            if (currentPoint.y == 0f) {
                currentPoint.y = getHeight() / 2f;
            }
            invalidate();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        roundRect.set(0F, 0F, (float) getWidth(), (float) getHeight());
        canvas.drawRoundRect(roundRect, 90f, 90f, colorPaint);
        canvas.drawCircle(currentPoint.x, ((float) getHeight() / 2), selectorRadiusPx, selectorPaint);
        canvas.drawCircle(currentPoint.x,  ((float) getHeight() / 2), selectorRadiusPx + circleWidth, outPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                handler.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
                update(event);
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void configurePaint() {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] = 1f;
        int startColor = Color.HSVToColor(hsv);
        hsv[2] = 0f;
        int endColor = Color.HSVToColor(hsv);
        LinearGradient shader = new LinearGradient(0f, 0f, (float) getWidth(), (float) getHeight(), startColor, endColor, Shader.TileMode.MIRROR);
        colorPaint.setShader(shader);
    }


    private void updateValue(float eventX_) {
        float eventX = eventX_;
        float left = selectorHalfWidth;
        float right = (float) getWidth() - selectorHalfWidth;
        if (eventX < left) {
            eventX = left;
        }
        if (eventX > right) {
            eventX = right;
        }
        currentValue = (eventX - left) / (right - left);
        currentPoint.x = eventX;
        currentPoint.y = getHeight() / 2F;
        invalidate();
    }

    private int assembleColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] = 1 - currentValue;
        return Color.HSVToColor(hsv);
    }


    @Override
    public void update(MotionEvent event) {
        if (event == null) {
            return;
        }
        updateValue(event.getX());
        boolean isTouchUpEvent = event.getActionMasked() == MotionEvent.ACTION_UP;
        if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
            if (onColorSelectListener != null) {
                onColorSelectListener.color(assembleColor());
            }
        }
    }

    public void setOnColorSelectListener(COnColorSelectListener onColorSelectListener) {
        this.onColorSelectListener = onColorSelectListener;
    }

    public void setOnlyUpdateOnTouchEventUp(boolean onlyUpdateOnTouchEventUp) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp;
    }
}
