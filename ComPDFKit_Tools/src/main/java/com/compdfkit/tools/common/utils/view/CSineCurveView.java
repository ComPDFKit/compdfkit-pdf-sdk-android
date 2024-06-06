package com.compdfkit.tools.common.utils.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CSineCurveView extends View {

    enum DrawType {Normal, PenStroke}

    private int MAX_POINT_NUMBER = 100;

    private int HALF = 50;

    private float HALFF = 50f;

    private int lineColor = Color.RED;

    private int lineAlpha = 255;

    private float borderWidth = 20f;

    private DrawType drawType = DrawType.Normal;

    private Paint paint = new Paint();

    private float sineCurveHeight = 40F;

    private List<PointF> points = new ArrayList<>();

    public CSineCurveView(Context context) {
        this(context, null);
    }

    public CSineCurveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSineCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(lineColor);
        paint.setAlpha(lineAlpha);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        paint.setColor(lineColor);
        paint.setAlpha(lineAlpha);
        invalidate();
    }

    public void setBorderWidth(float lineWidth) {
        this.borderWidth = lineWidth;
        paint.setStrokeWidth(lineWidth);
        invalidate();

    }

    public void setLineAlpha(int lineAlpha) {
        this.lineAlpha = lineAlpha;
        paint.setAlpha(lineAlpha);
        invalidate();
    }

    public void setDrawType(DrawType type) {
        this.drawType = type;
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    public int getLineAlpha() {
        return lineAlpha;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Path path = new Path();
        int viewWidth = getWidth();
        float startX = 0F;
        if (drawType == DrawType.PenStroke) {
            startX = borderWidth;
            viewWidth = (int) (getWidth() - 2 * borderWidth);
        }
        /****** 起始位置  */
        path.moveTo(startX, ((float) getHeight() / 2));
        /****** rQuardto的位置是相对的  */
        path.rQuadTo(((float) viewWidth / 4), -sineCurveHeight, ((float) viewWidth / 2), 0F);
        path.rQuadTo(((float) viewWidth / 4), sineCurveHeight, ((float) viewWidth / 2), 0F);

        if (drawType == DrawType.Normal) {
            paint.setStrokeWidth(borderWidth);
            canvas.drawPath(path, paint);
        } else {
            PathMeasure measure = new PathMeasure(path, false);
            float distance = measure.getLength() / MAX_POINT_NUMBER;

            points.clear();
            for (int i = 0; i < MAX_POINT_NUMBER; i++) {
                float[] point = new float[2];
                measure.getPosTan(i * distance, point, null);
                points.add(new PointF(point[0], point[1]));
            }

            PointF lastPoint = points.get(0);
            for (int i = 0; i < points.size(); i++) {
                PointF currentPoint = points.get(i);
                float lWidth = 0f;
                if (i < HALF) {
                    lWidth = borderWidth;
                } else {
                    lWidth = borderWidth - (i - HALFF) / HALFF * borderWidth * 0.8F;
                }
                paint.setStrokeWidth(lWidth);
                canvas.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y, paint);
                lastPoint = currentPoint;
            }
        }
    }
}
