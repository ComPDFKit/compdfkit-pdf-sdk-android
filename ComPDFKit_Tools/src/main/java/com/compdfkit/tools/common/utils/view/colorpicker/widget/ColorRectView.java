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
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.compdfkit.tools.common.interfaces.COnColorSelectListener;
import com.compdfkit.tools.common.utils.view.colorpicker.interfaces.CMotionEventUpdatable;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

public class ColorRectView extends FrameLayout implements CMotionEventUpdatable {

    private PointF currentPoint = new PointF();

    private int currentColor = Color.MAGENTA;

    private boolean onlyUpdateOnTouchEventUp = false;

    private COnColorSelectListener colorSelectListener;

    private ColorRectSelector selector;

    private CThrottledTouchEventHandler handler;

    public ColorRectView(Context context) {
        this(context, null);
    }

    public ColorRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selector = new ColorRectSelector(context);
        handler = new CThrottledTouchEventHandler(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForceDarkAllowed(false);
        }

        int padding = CDimensUtils.px2dp(context, 26);
        ColorRectPalette palette = new ColorRectPalette(context);
        palette.setPadding(padding, padding, padding, padding);
        ViewGroup.LayoutParams paletteLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(palette, paletteLayoutParams);

        ViewGroup.LayoutParams selectorLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(selector, selectorLayoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = maxWidth;
        int mHeight = maxHeight;
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setColor(currentColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (event.getX() - selector.radiusRectSelector <= 0){
                    event.setLocation(selector.radiusRectSelector, event.getY());
                } else if (event.getX() + selector.radiusRectSelector > getWidth()) {
                    event.setLocation(getWidth() - selector.radiusRectSelector, event.getY());
                } else if (event.getY() - selector.radiusRectSelector <= 0) {
                    event.setLocation(event.getX(), selector.radiusRectSelector);
                } else if (event.getY() + selector.radiusRectSelector > getHeight()) {
                    event.setLocation(event.getX(), getHeight() - selector.radiusRectSelector);
                }
                handler.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
                if (event.getX() - selector.radiusRectSelector <= 0){
                    event.setLocation(selector.radiusRectSelector, event.getY());
                } else if (event.getX() + selector.radiusRectSelector > getWidth()) {
                    event.setLocation(getWidth() - selector.radiusRectSelector, event.getY());
                } else if (event.getY() - selector.radiusRectSelector <= 0) {
                    event.setLocation(event.getX(), selector.radiusRectSelector);
                } else if (event.getY() + selector.radiusRectSelector > getHeight()) {
                    event.setLocation(event.getX(), getHeight() - selector.radiusRectSelector);
                }
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
        boolean isTouchUpEvent = event.getActionMasked() == MotionEvent.ACTION_UP;
        if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
            if (colorSelectListener != null) {
                colorSelectListener.color(getColorAtPoint(event.getX(), event.getY()));
            }
        }
        updateSelector(event.getX(), event.getY());
    }

    private int getColorAtPoint(float eventX, float eventY){
        float pointX = eventX;
        if (eventX - selector.radiusRectSelector <= 0 ){
            pointX = selector.radiusRectSelector;
        } else if (eventX + selector.radiusRectSelector >= getWidth()) {
            pointX = getWidth() - selector.radiusRectSelector;
        }else {
            pointX = eventX;
        }
        float x = 360F - pointX * 360F / getWidth();

        float pointY = eventY;
        if (eventY - selector.radiusRectSelector <= 0){
            pointY = 0F;
        } else if (eventY + selector.radiusRectSelector >= getHeight()) {
            pointY = getHeight() - selector.radiusRectSelector;
        } else {
            pointY = eventY;
        }
        float y = 1F - (pointY / (getHeight() - selector.radiusRectSelector));

        if (y == 0F){
            return Color.WHITE;
        }else {
            return Color.HSVToColor(new float[]{x, y, 1F});
        }
    }


    public void  setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        Color.HSVToColor(color, hsv);
        updateSelector((getWidth() - (getWidth() * hsv[0]) / 360), ((1 - hsv[1]) * getHeight()));
        currentColor = color;
    }

    private void updateSelector(float eventX,float eventY) {
        if (eventX - selector.radiusRectSelector <= 0){
            currentPoint.x = selector.radiusRectSelector;
        } else if (eventX + selector.radiusRectSelector >= getWidth()) {
            currentPoint.x = getWidth() + selector.radiusRectSelector;
        } else {
            currentPoint.x = eventX;
        }

        if (eventY - selector.radiusRectSelector <= 0){
            currentPoint.y = selector.radiusRectSelector;
        } else if (eventY + selector.radiusRectSelector >= getHeight()) {
            currentPoint.y = getHeight() - selector.radiusRectSelector;
        }else {
            currentPoint.y = eventY;
        }
        selector.setCurrentPoint(currentPoint);
    }

    public void setOnlyUpdateOnTouchEventUp(boolean onlyUpdateOnTouchEventUp) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp;
    }

    public void setColorSelectListener(COnColorSelectListener colorSelectListener) {
        this.colorSelectListener = colorSelectListener;
    }
}
