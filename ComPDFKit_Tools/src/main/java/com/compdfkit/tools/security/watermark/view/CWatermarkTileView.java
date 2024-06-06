package com.compdfkit.tools.security.watermark.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.compdfkit.tools.R;

import java.util.ArrayList;
import java.util.List;


public class CWatermarkTileView extends View {

    private RectF sourceWatermarkRect = new RectF();

    private Paint bitmapPaint = new Paint();

    private Bitmap tileBitmap = null;

    private float mDegree = 1F;

    private Matrix transformationMatrix = new Matrix();

    private  Rect bitmapRect = new Rect();
    private float controlWidth;

    private float spacing;
    private float frameWidth;

    public CWatermarkTileView(Context context) {
        this(context, null);
    }

    public CWatermarkTileView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CWatermarkTileView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setColor(ContextCompat.getColor(getContext(), R.color.tools_annotation_markup_default_color));
        bitmapPaint.setStyle(Paint.Style.STROKE);
        bitmapPaint.setStrokeWidth(1);
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    public void setTileView(CWatermarkView watermarkView) {
        mDegree = watermarkView.getDegree();
        watermarkView.setDegree(0);

        controlWidth = watermarkView.getWatermarkPadding();
        frameWidth = watermarkView.getFrameWidth();
        this.sourceWatermarkRect.set(
                watermarkView.getLeft(),
                watermarkView.getTop() ,
                watermarkView.getRight() ,
                watermarkView.getBottom());
        parseTileRectFs();
        tileBitmap = Bitmap.createBitmap((int) sourceWatermarkRect.width(),
                (int) sourceWatermarkRect.height(), Bitmap.Config.ARGB_4444);
        watermarkView.setEditable(false);
        watermarkView.draw(new Canvas(tileBitmap));
        watermarkView.setDegree(mDegree);
        watermarkView.setEditable(true);

        bitmapRect.set(
                (int) (controlWidth - spacing),
                (int) (controlWidth - spacing),
                (int) (tileBitmap.getWidth() - controlWidth + spacing),
                (int) (tileBitmap.getHeight() - controlWidth + spacing)
        );

        transformationMatrix.reset();
        transformationMatrix.postRotate(mDegree, sourceWatermarkRect.centerX(), sourceWatermarkRect.centerY());
        invalidate();
    }

    public void clear() {
        tileRectFs.clear();
        try {
            if (tileBitmap != null) {
                tileBitmap.recycle();
                tileBitmap = null;
            }
        } catch (Exception e) {

        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.concat(transformationMatrix);
        for (RectF tileRectF : tileRectFs) {
            canvas.drawBitmap(tileBitmap, bitmapRect, tileRectF, bitmapPaint);
        }
    }

    private List<RectF> tileRectFs = new ArrayList<>();

    private void parseTileRectFs() {
        tileRectFs.clear();
        RectF realWatermarkRect = new RectF(sourceWatermarkRect);
        realWatermarkRect.set(
                sourceWatermarkRect.left + controlWidth,
                sourceWatermarkRect.top + controlWidth,
                sourceWatermarkRect.right - controlWidth,
                sourceWatermarkRect.bottom - controlWidth
        );
        realWatermarkRect.set(
                realWatermarkRect.left - spacing,
                realWatermarkRect.top - spacing,
                realWatermarkRect.right + spacing,
                realWatermarkRect.bottom + spacing
        );
        // parse top rectF array
        float tempTopBottom = realWatermarkRect.top;
        while (tempTopBottom > getTop() - realWatermarkRect.height()) {
            float top = tempTopBottom - realWatermarkRect.height() + frameWidth;
            float bottom = tempTopBottom - frameWidth;
            RectF rectF = new RectF(realWatermarkRect.left, top, realWatermarkRect.right, bottom);
            tempTopBottom = top;
            tileRectFs.add(rectF);
        }
        float tempBottom = realWatermarkRect.bottom;
        while (tempBottom < getBottom() + realWatermarkRect.height()) {
            float top = tempBottom - frameWidth;
            float bottom = top + realWatermarkRect.height() + frameWidth;
            RectF rectF = new RectF(realWatermarkRect.left,
                    top,
                    realWatermarkRect.right,
                    bottom);
            tempBottom = bottom;
            tileRectFs.add(rectF);
        }

        List<RectF> leftRectF = new ArrayList<>();
        List<RectF> rightRectF = new ArrayList<>();

        List<RectF> centerVerticalRectF = new ArrayList<>();
        centerVerticalRectF.addAll(tileRectFs);
        centerVerticalRectF.add(realWatermarkRect);
        // get left rectF array
        for (RectF tileRectF : centerVerticalRectF) {
            float tempRight = tileRectF.left;
            while (tempRight > getLeft() - (realWatermarkRect.width()* 3)) {
                float left = tempRight - tileRectF.width() + (frameWidth /2)  ;
                float right = tempRight +  (frameWidth /2) ;
                RectF rectF = new RectF(left, tileRectF.top, right, tileRectF.bottom);
                tempRight = left;
                leftRectF.add(rectF);
            }
            float tempLeft = tileRectF.right;
            while (tempLeft < getRight() + (realWatermarkRect.width() * 3)) {
                float right = tempLeft + tileRectF.width() - frameWidth;
                float left = tempLeft - frameWidth;
                RectF rectF = new RectF(left, tileRectF.top, right, tileRectF.bottom);
                tempLeft = right;
                rightRectF.add(rectF);
            }
        }
        tileRectFs.addAll(leftRectF);
        tileRectFs.addAll(rightRectF);
    }

}
