/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class CAnnotShapePreviewView extends CBasicAnnotPreviewView {

    private CShapeView shapeView;

    public CAnnotShapePreviewView(@NonNull Context context) {
        super(context);
    }

    public CAnnotShapePreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CAnnotShapePreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void bindView() {
        shapeView = new CShapeView(getContext());
        addView(shapeView);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void setColor(int color) {
        super.setColor(color);
        if (shapeView != null) {
            shapeView.setFillColor(color);
        }
    }

    @Override
    public void setOpacity(int opacity) {
        super.setOpacity(opacity);
        if (shapeView != null) {
            shapeView.setFillColorOpacity(opacity);
        }
    }

    @Override
    public void setBorderColor(int color) {
        super.setBorderColor(color);
        if (shapeView != null) {
            shapeView.setBorderColor(color);
        }
    }

    @Override
    public void setBorderColorOpacity(int opacity) {
        super.setBorderColorOpacity(opacity);
        if (shapeView != null) {
            shapeView.setBorderColorOpacity(opacity);
        }
    }

    @Override
    public void setBorderWidth(int borderWidth) {
        super.setBorderWidth(borderWidth);
        if (shapeView != null) {
            shapeView.setBorderWidth(borderWidth);
        }
    }

    @Override
    public void setDashedGsp(int dashedGap) {
        if (shapeView != null) {
            shapeView.setDashGap(dashedGap);
        }
    }

    @Override
    public void setShapeType(CShapeView.ShapeType shapeType){
        if (shapeView != null){
            shapeView.setShapeType(shapeType);
        }
    }

    static class CShapeView extends View {

        enum ShapeType {
            SQUARE,
            CIRCLE,
            LINE,
            ARROW,
        }

        public static final float SQUARE_RATIO = 27F / 20F;
        private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private ShapeType shapeType = ShapeType.SQUARE;

        private int borderColor = Color.BLACK;

        private int fillColor = Color.GRAY;

        private int borderColorOpacity = 255;

        private int fillColorOpacity = 255;

        private int borderWidth = 0;

        private RectF borderRectF = new RectF();

        private RectF fillRectF = new RectF();

        private int mDashWidth = 8;

        private int mDashGap = 0;

        private Rect sizeRect = new Rect();

        public CShapeView(Context context) {
            this(context, null);
        }

        public CShapeView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {

            borderPaint.setColor(borderColor);
            borderPaint.setAlpha(borderColorOpacity);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderWidth);
            borderPaint.setStrokeCap(Paint.Cap.SQUARE);
            if (mDashGap != 0){
                borderPaint.setPathEffect(new DashPathEffect(new float[]{mDashWidth, mDashGap + borderWidth}, 0));
            }

            fillPaint.setColor(fillColor);
            fillPaint.setAlpha(fillColorOpacity);
            fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        public void setBorderColor(@ColorInt int color) {
            this.borderColor = color;
            borderPaint.setColor(color);
            borderPaint.setAlpha(borderColorOpacity);
            invalidate();
        }

        public void setBorderColorOpacity(@IntRange(from = 0, to = 255) int opacity) {
            this.borderColorOpacity = opacity;
            borderPaint.setAlpha(borderColorOpacity);
            invalidate();
        }

        public void setFillColor(@ColorInt int color) {
            this.fillColor = color;
            fillPaint.setColor(fillColor);
            fillPaint.setAlpha(fillColorOpacity);
            invalidate();
        }

        public void setFillColorOpacity(@IntRange(from = 0, to = 255) int opacity) {
            this.fillColorOpacity = opacity;
            fillPaint.setAlpha(fillColorOpacity);
            invalidate();
        }

        public void setDashGap(@IntRange(from = 0, to = 8) int dashGap) {
            this.mDashGap = dashGap;
            if (dashGap == 0) {
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setPathEffect(null);
            } else {
                borderPaint.setPathEffect(new DashPathEffect(new float[]{mDashWidth, mDashGap + borderWidth}, 0));
            }
            invalidate();
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            borderPaint.setStrokeWidth(borderWidth);
            if (mDashGap == 0){
                borderPaint.setPathEffect(null);
            }else {
                borderPaint.setPathEffect(new DashPathEffect(new float[]{mDashWidth, mDashGap + borderWidth}, 0));
            }
            invalidate();
        }

        public void setShapeType(ShapeType shapeType) {
            this.shapeType = shapeType;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            switch (shapeType) {
                case SQUARE:
                    float width = getHeight() * SQUARE_RATIO;
                    float left = (float) getWidth() / 2F - (width / 2);

                    sizeRect.set((int) left,
                            0,
                            (int) (left + width),
                            getHeight());

                    fillRectF.set(sizeRect.left + borderWidth, sizeRect.top + borderWidth, sizeRect.right - borderWidth, sizeRect.bottom - borderWidth);
                    canvas.drawRect(fillRectF, fillPaint);

                    borderRectF.set(fillRectF.left - (borderWidth / 2F),
                            fillRectF.top - (borderWidth / 2F),
                            fillRectF.right + (borderWidth / 2F),
                            fillRectF.bottom + (borderWidth / 2F));
                    canvas.drawRect(borderRectF, borderPaint);
                    break;

                case CIRCLE:
                    float circleWidth = getHeight();
                    float circleLeft = (float) getWidth() / 2F - (circleWidth / 2);
                    sizeRect.set((int) circleLeft,
                            0,
                            (int) (circleLeft + circleWidth),
                            getHeight());
                    fillRectF.set(sizeRect.left + borderWidth, sizeRect.top + borderWidth, sizeRect.right - borderWidth, sizeRect.bottom - borderWidth);
                    canvas.drawOval(fillRectF, fillPaint);
                    borderRectF.set(fillRectF.left - (borderWidth / 2F),
                            fillRectF.top - (borderWidth / 2F),
                            fillRectF.right + (borderWidth / 2F),
                            fillRectF.bottom + (borderWidth / 2F));
                    canvas.drawOval(borderRectF, borderPaint);
                    break;
                case LINE:
                    float lineWidth = getHeight();
                    float startX = (float) getWidth() / 2F - (lineWidth / 2);
                    float startY = (getHeight() / 2F) + borderWidth;
                    float stopX = startX + getHeight();
                    float stopY = (getHeight() / 2F) + borderWidth;
                    canvas.drawLine(startX, startY, stopX, stopY, borderPaint);
                    break;
                case ARROW:

                    break;
                default:
                    break;
            }
        }
    }
}
