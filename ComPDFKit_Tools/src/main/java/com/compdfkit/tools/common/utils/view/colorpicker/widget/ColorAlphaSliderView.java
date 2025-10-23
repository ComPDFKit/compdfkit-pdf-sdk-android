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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.interfaces.CMotionEventUpdatable;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

public class ColorAlphaSliderView extends View implements CMotionEventUpdatable {

    private float circleWidth;

    private float selectorRadiusPx;

    private float selectorHalfWidth;

    int baseColor = Color.BLACK;

    private Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint outPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean onlyUpdateOnTouchEventUp = false;

    private CThrottledTouchEventHandler handler;

    private OnColorOpacityChangeListener onColorOpacityChangeListener;

    private float percent = 0F;

    private RectF roundRectF = new RectF();

    private Bitmap alphaBitmap;

    private Paint bitmapPaint = new Paint();

    public ColorAlphaSliderView(Context context) {
        this(context, null);
    }

    public ColorAlphaSliderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorAlphaSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForceDarkAllowed(false);
        }
        handler = new CThrottledTouchEventHandler(this);
        circleWidth = CDimensUtils.px2dp(context, 20);
        selectorRadiusPx = CDimensUtils.px2dp(context, 80);
        selectorHalfWidth = CDimensUtils.px2dp(context, 30);
        selectorPaint.setColor(Color.WHITE);
        selectorPaint.setStyle(Paint.Style.STROKE);
        selectorPaint.setStrokeWidth(circleWidth);

        alphaBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tools_color_picker_bg_color_alpha);
    }

    private float getCurrentX() {
        return ((getWidth() - 2 * selectorHalfWidth) * percent + selectorHalfWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        configurePaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        roundRectF.set(0F, 0F, (float) getWidth(), (float) getHeight());
        if (alphaBitmap != null) {
            canvas.drawBitmap(alphaBitmap, null, roundRectF, bitmapPaint);
            canvas.drawRoundRect(roundRectF, 90f, 90f, colorPaint);
            canvas.drawCircle(getCurrentX(), (float) (getHeight() / 2), selectorRadiusPx, selectorPaint);
//            canvas.drawCircle(getCurrentX(), (float) (getHeight() / 2), selectorRadiusPx + circleWidth, outPaint);
        }
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

    @Override
    public void update(MotionEvent event) {
        if (event == null) {
            return;
        }
        updateValue(event.getX());
        boolean isTouchUpEvent = event.getActionMasked() == MotionEvent.ACTION_UP;
        if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
            if (onColorOpacityChangeListener != null) {
                onColorOpacityChangeListener.opacity((int) (percent * 255));
            }
        }
    }

    /**
     * 更新数据
     */
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
        percent = (eventX - left) / (right - left);
        invalidate();
    }

    private void configurePaint() {
        LinearGradient shader = new LinearGradient(0f, 0f, (float) getWidth(), (float) getHeight(), Color.TRANSPARENT, baseColor, Shader.TileMode.CLAMP);
        colorPaint.setShader(shader);
        invalidate();
    }

    public void setBaseColor(int color) {
        this.baseColor = color;
        configurePaint();
        invalidate();
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }

    public void setOnlyUpdateOnTouchEventUp(boolean onlyUpdateOnTouchEventUp) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp;
    }

    public void setColorOpacityChangeListener(OnColorOpacityChangeListener onColorOpacityChangeListener) {
        this.onColorOpacityChangeListener = onColorOpacityChangeListener;
    }

    public interface OnColorOpacityChangeListener {
        void opacity(int opacity);
    }
}

