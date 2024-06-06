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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;

class ColorRectSelector extends View {

    //半径
    public static float RADIUS = 30f;

    //整体半径
    public float radiusRectSelector;

    //选择器宽度
    public float levelOneWidth;
    public float levelTwoWidth;

    //Paint
    private Paint levelOnePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint levelTwoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private PointF currentPoint = new PointF();

    public ColorRectSelector(Context context) {
        this(context, null);
    }

    public ColorRectSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorRectSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        radiusRectSelector = RADIUS + CDimensUtils.px2dp(getContext(), 12);
        levelOneWidth = CDimensUtils.px2dp(getContext(), 20);
        levelTwoWidth = RADIUS + CDimensUtils.px2dp(getContext(), 10);

        levelOnePaint.setColor(Color.WHITE);
        levelOnePaint.setStyle(Paint.Style.STROKE);
        levelOnePaint.setStrokeWidth(levelOneWidth);

//        levelTwoPaint.setColor(0x29FFFFFF);
//        levelTwoPaint.setStyle(Paint.Style.STROKE);
//        levelTwoPaint.setStrokeWidth(levelTwoWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawCircle(currentPoint.x, currentPoint.y, RADIUS + (levelTwoWidth - levelOneWidth), levelTwoPaint);
        canvas.drawCircle(currentPoint.x, currentPoint.y, RADIUS, levelOnePaint);
    }

    public void setCurrentPoint(PointF currentPoint) {
        this.currentPoint = currentPoint;
        invalidate();
    }
}