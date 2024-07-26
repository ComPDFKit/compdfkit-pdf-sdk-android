package com.compdfkit.tools.common.views.pdfproperties.preview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.tools.R;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CAnnotLineTypePreviewView extends CBasicAnnotPreviewView {

    private CLineTypeView lineTypeView;

    public CAnnotLineTypePreviewView(@NonNull Context context) {
        this(context, null);
    }

    public CAnnotLineTypePreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CAnnotLineTypePreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void bindView() {
        lineTypeView = new CLineTypeView(getContext());
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        lineTypeView.setLayoutParams(layoutParams);
        addView(lineTypeView);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void setStartLineType(CPDFLineAnnotation.LineType lineType) {
        if (lineTypeView != null) {
            lineTypeView.setStartLineType(lineType);
        }
    }

    @Override
    public void setTailLineType(CPDFLineAnnotation.LineType lineType) {
        if (lineTypeView != null) {
            lineTypeView.setTailLineType(lineType);
        }
    }

    @Override
    public void setBorderColor(int color) {
        super.setBorderColor(color);
        if (lineTypeView != null) {
            lineTypeView.setColor(color);
        }
    }

    @Override
    public void setBorderColorOpacity(int colorOpacity) {
        super.setBorderColorOpacity(colorOpacity);
        lineTypeView.setColorOpacity(colorOpacity);
    }

    @Override
    public void setBorderWidth(int borderWidth) {
        if (lineTypeView != null) {
            lineTypeView.setBorderWidth(borderWidth);
        }
    }

    @Override
    public void setDashedGsp(int dashedSpace) {
        if (lineTypeView != null) {
            lineTypeView.setDashedGsp(dashedSpace);
        }
    }

    public class CLineTypeView extends View {

        private CPDFLineAnnotation.LineType startLineType = CPDFLineAnnotation.LineType.LINETYPE_NONE;
        private CPDFLineAnnotation.LineType tailLineType = CPDFLineAnnotation.LineType.LINETYPE_NONE;

        private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Path linePath = new Path();

        private int color = Color.BLACK;

        private int lineWidth = 3;

        private int mDashWidth = 8;

        private int mDashGap = 0;

        private DashPathEffect dashPathEffect = null;

        public CLineTypeView(Context context) {
            this(context, null);
        }

        public CLineTypeView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CLineTypeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            color = MaterialColors.getColor(context, R.attr.colorOnPrimary, ContextCompat.getColor(context, R.color.tools_on_primary));
            linePaint.setColor(color);
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setStrokeWidth(lineWidth);
            linePaint.setAntiAlias(true);
        }

        public void setStartLineType(CPDFLineAnnotation.LineType startLineType) {
            this.startLineType = startLineType;
            invalidate();
        }

        public void setTailLineType(CPDFLineAnnotation.LineType tailLineType) {
            this.tailLineType = tailLineType;
            invalidate();
        }

        public void setColor(int color) {
            this.color = color;
            linePaint.setColor(color);
            invalidate();
        }

        public void setColorOpacity(@IntRange(from = 0, to = 255) int opacity) {
            linePaint.setAlpha(opacity);
            invalidate();
        }

        public void setBorderWidth(int borderWidth) {
            lineWidth = borderWidth;
            linePaint.setStrokeWidth(lineWidth);
            invalidate();
        }

        public void setDashedGsp(int dashedSpace) {
            this.mDashGap = dashedSpace;
            if (dashedSpace == 0) {
                dashPathEffect = null;
            } else {
                dashPathEffect = new DashPathEffect(new float[]{mDashWidth, mDashGap + lineWidth / 2F}, 0);
            }
            invalidate();
        }


        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            linePaint.setPathEffect(null);
            List<Float> startPoint = drawStartArrow(canvas);
            List<Float> tailPoint = drawTailArrow(canvas);

            if (startPoint != null && startPoint.size() == 2
                    && tailPoint != null && tailPoint.size() == 2) {
                linePaint.setPathEffect(dashPathEffect);
                canvas.drawLine(tailPoint.get(0), tailPoint.get(1), startPoint.get(0), startPoint.get(1), linePaint);
            }
        }


        private List<Float> drawStartNoneLine(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            return Arrays.asList(0.1F * mWidth, 0.9F * mHeight);
        }

        private List<Float> drawTailNoneLine(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            return Arrays.asList(0.9F * mWidth, 0.1F * mHeight);
        }

        private List<Float> drawStartArrow(Canvas canvas) {
            switch (startLineType) {
                case LINETYPE_NONE:
                    return drawStartNoneLine(canvas);
                case LINETYPE_SQUARE:
                    return drawStartSquareArrow(canvas);
                case LINETYPE_CIRCLE:
                    return drawStartCircleArrow(canvas);
                case LINETYPE_DIAMOND:
                    return drawStartDiamondArrow(canvas);
                case LINETYPE_ARROW:
                    return drawStartNormalArrow(canvas);
                case LINETYPE_CLOSEDARROW:
                    return drawStartClosedArrow(canvas);
                default:
                    return new ArrayList<>();
            }
        }

        private List<Float> drawTailArrow(Canvas canvas) {
            switch (tailLineType) {
                case LINETYPE_NONE:
                    return drawTailNoneLine(canvas);
                case LINETYPE_SQUARE:
                    return drawTailSquareArrow(canvas);
                case LINETYPE_CIRCLE:
                    return drawTailCircleArrow(canvas);
                case LINETYPE_DIAMOND:
                    return drawTailDiamondArrow(canvas);
                case LINETYPE_ARROW:
                    return drawTailNormalArrow(canvas);
                case LINETYPE_CLOSEDARROW:
                    return drawTailClosedArrow(canvas);
                default:
                    return new ArrayList<>();
            }
        }

        private List<Float> drawStartNormalArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            canvas.drawLine(0.1f * mWidth, 0.9f * mHeight, 0.15f * mWidth, 0.6f * mHeight, linePaint);
            canvas.drawLine(0.1f * mWidth, 0.9f * mHeight, 0.4f * mWidth, 0.85f * mHeight, linePaint);
            return Arrays.asList(0.1f * mWidth, 0.9f * mHeight);
        }

        private List<Float> drawTailNormalArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            canvas.drawLine(0.6f * mWidth, 0.15f * mHeight, 0.9f * mWidth, 0.1f * mHeight, linePaint);
            canvas.drawLine(0.85f * mWidth, 0.4f * mHeight, 0.9f * mWidth, 0.1f * mHeight, linePaint);
            return Arrays.asList(0.9f * mWidth, 0.1F * mHeight);
        }

        private List<Float> drawStartClosedArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            linePath.reset();
            linePath.moveTo(0.4f * mWidth, mHeight);
            linePath.lineTo(0.0f, mHeight);
            linePath.lineTo(0.0f, 0.6f * mHeight);
            linePath.close();
            canvas.drawPath(linePath, linePaint);
            return Arrays.asList(0.2F * mWidth, 0.8F * mHeight);
        }

        private List<Float> drawTailClosedArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            linePath.reset();
            linePath.moveTo(0.6f * mWidth, 0f);
            linePath.lineTo(mWidth, 0f);
            linePath.lineTo(mWidth, 0.4f * mHeight);
            linePath.close();
            canvas.drawPath(linePath, linePaint);
            return Arrays.asList(0.8F * mWidth, 0.2F * mHeight);
        }

        private List<Float> drawStartSquareArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            linePath.reset();
            linePath.moveTo(0f, 0.85f * mHeight);
            linePath.lineTo(0.15f * mWidth, mHeight);
            linePath.lineTo(0.3f * mWidth, 0.85f * mHeight);
            linePath.lineTo(0.15f * mWidth, 0.7f * mHeight);
            linePath.close();
            canvas.drawPath(linePath, linePaint);
            return Arrays.asList(0.225f * mWidth, 0.775f * mHeight);
        }

        private List<Float> drawTailSquareArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            linePath.reset();
            linePath.moveTo(0.85f * mWidth, 0f);
            linePath.lineTo(mWidth, 0.15f * mHeight);
            linePath.lineTo(0.85f * mWidth, 0.3f * mHeight);
            linePath.lineTo(0.7f * mWidth, 0.15f * mHeight);
            linePath.close();
            canvas.drawPath(linePath, linePaint);
            return Arrays.asList(0.775f * mWidth, 0.225F * mHeight);
        }

        private List<Float> drawStartCircleArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            canvas.drawCircle(0.15f * mWidth, 0.85f * mHeight, 0.15f * mHeight, linePaint);
            return Arrays.asList(0.225f * mHeight, 0.775f * mHeight);
        }

        private List<Float> drawTailCircleArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            canvas.drawCircle(0.85f * mWidth, 0.15f * mHeight, 0.15f * mHeight, linePaint);
            return Arrays.asList(0.775f * mHeight, 0.225f * mHeight);
        }

        private List<Float> drawStartDiamondArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            linePath.reset();
            linePath.moveTo(0F, 0.75F * mHeight);
            linePath.lineTo(0.25F * mWidth, 0.75F * mHeight);
            linePath.lineTo(0.25F * mWidth, mHeight);
            linePath.lineTo(0F, mHeight);
            linePath.close();
            canvas.drawPath(linePath, linePaint);
            return Arrays.asList(0.25F * mWidth, 0.75F * mHeight);
        }

        private List<Float> drawTailDiamondArrow(Canvas canvas) {
            int mHeight = getHeight();
            int mWidth = mHeight;
            linePath.reset();
            linePath.moveTo(0.75f * mWidth, 0f);
            linePath.lineTo(mWidth, 0f);
            linePath.lineTo(mWidth, 0.25f * mHeight);
            linePath.lineTo(0.75f * mWidth, 0.25f * mHeight);
            linePath.close();
            canvas.drawPath(linePath, linePaint);
            return Arrays.asList(0.75f * mHeight, 0.25f * mHeight);
        }
    }
}
