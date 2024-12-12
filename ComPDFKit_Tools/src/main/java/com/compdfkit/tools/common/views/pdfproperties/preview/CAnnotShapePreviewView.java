/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.compdfkit.core.annotation.CPDFAnnotation.CPDFBorderEffectType;


public class CAnnotShapePreviewView extends CBasicAnnotPreviewView {

  private CShapeView shapeView;

  public CAnnotShapePreviewView(@NonNull Context context) {
    super(context);
  }

  public CAnnotShapePreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CAnnotShapePreviewView(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
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
  public void setShapeType(CShapeView.ShapeType shapeType) {
    if (shapeView != null) {
      shapeView.setShapeType(shapeType);
    }
  }

  @Override
  public void setBorderEffectType(CPDFBorderEffectType type) {
    if (shapeView != null) {
      shapeView.setBorderEffectType(type);
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

    private CPDFBorderEffectType effectType = CPDFBorderEffectType.CPDFBorderEffectTypeSolid;

    private int diameter = 20;

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
      setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      borderPaint.setColor(borderColor);
      borderPaint.setAlpha(borderColorOpacity);
      borderPaint.setStyle(Paint.Style.STROKE);
      borderPaint.setStrokeWidth(borderWidth);
      borderPaint.setStrokeCap(Paint.Cap.SQUARE);
      borderPaint.setDither(true);
      borderPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
      if (mDashGap != 0) {
        borderPaint.setPathEffect(
            new DashPathEffect(new float[]{mDashWidth, mDashGap + borderWidth}, 0));
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
        borderPaint.setPathEffect(
            new DashPathEffect(new float[]{mDashWidth, mDashGap + borderWidth}, 0));
      }
      invalidate();
    }

    public void setBorderWidth(int borderWidth) {
      this.borderWidth = borderWidth;
      borderPaint.setStrokeWidth(borderWidth);
      if (mDashGap == 0) {
        borderPaint.setPathEffect(null);
      } else {
        borderPaint.setPathEffect(
            new DashPathEffect(new float[]{mDashWidth, mDashGap + borderWidth}, 0));
      }
      invalidate();
    }

    public void setBorderEffectType(CPDFBorderEffectType effectType) {
      this.effectType = effectType;
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
          if (effectType == CPDFBorderEffectType.CPDFBorderEffectTypeCloudy) {
            drawCloudySquare(canvas);
          } else {
            drawSolidSquare(canvas);
          }
          break;

        case CIRCLE:
          if (effectType == CPDFBorderEffectType.CPDFBorderEffectTypeCloudy) {
            drawCloudyCircle(canvas);
          } else {
            drawSolidCircle(canvas);
          }

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

    private void drawSolidSquare(Canvas canvas) {
      float width = getHeight() * SQUARE_RATIO;
      float left = (float) getWidth() / 2F - (width / 2);

      sizeRect.set((int) left,
          0,
          (int) (left + width),
          getHeight());

      fillRectF.set(sizeRect.left + borderWidth, sizeRect.top + borderWidth,
          sizeRect.right - borderWidth, sizeRect.bottom - borderWidth);
      canvas.drawRect(fillRectF, fillPaint);

      borderRectF.set(fillRectF.left - (borderWidth / 2F),
          fillRectF.top - (borderWidth / 2F),
          fillRectF.right + (borderWidth / 2F),
          fillRectF.bottom + (borderWidth / 2F));
      canvas.drawRect(borderRectF, borderPaint);
    }

    private void drawCloudySquare(Canvas canvas) {
      float width = getHeight() * SQUARE_RATIO;
      float left = (float) getWidth() / 2F - (width / 2);

      sizeRect.set((int) left,
          0,
          (int) (left + width),
          getHeight());

      fillRectF.set(sizeRect.left + borderWidth, sizeRect.top + borderWidth,
          sizeRect.right - borderWidth, sizeRect.bottom - borderWidth);
      canvas.drawRect(fillRectF, fillPaint);

      borderRectF.set(fillRectF.left,
          fillRectF.top,
          fillRectF.right,
          fillRectF.bottom);

      drawRoundedCorner(canvas, borderRectF.left, borderRectF.top, 90, 270); // 左上角
      drawRoundedCorner(canvas, borderRectF.left, borderRectF.bottom - diameter, 0, 270); // 左下角
      drawRoundedCorner(canvas, borderRectF.right - diameter, borderRectF.top, 180, 270); // 右上角
      drawRoundedCorner(canvas, borderRectF.right - diameter, borderRectF.bottom - diameter, 270,
          270); // 右下角

      int verRectCount = (int) (borderRectF.height() - (diameter * 2) / this.diameter);
      float leftRectStartPoint = borderRectF.left;
      float leftRectEndPoint = borderRectF.left + diameter;
      float leftRectTopPoint = borderRectF.top + diameter;

      float rightRectStartPoint = borderRectF.right - diameter;
      float rightRectEndPoint = borderRectF.right;

      for (int i = 0; i < verRectCount; i++) {
        float currentTop = leftRectTopPoint + (i * this.diameter);
        float currentBottom = Math.min(borderRectF.bottom - diameter, currentTop + this.diameter);

        if (currentBottom - currentTop < 5) {
          continue;
        }

        RectF leftCloudyRectF = new RectF(leftRectStartPoint, currentTop, leftRectEndPoint,
            currentBottom);
        canvas.drawArc(leftCloudyRectF, 90, 180, false, borderPaint);

        RectF rightCloudyRectF = new RectF(rightRectStartPoint, currentTop, rightRectEndPoint,
            currentBottom);
        canvas.drawArc(rightCloudyRectF, 270, 180, false, borderPaint);
      }
      int horRectCount = (int) ((borderRectF.width() - (diameter)) / diameter);
      float topRectTopPoint = borderRectF.top;
      float topRectBottomPoint = topRectTopPoint + diameter;

      float bottomRectTopPoint = borderRectF.bottom - diameter;
      float bottomRectBottomPoint = borderRectF.bottom;

      for (int i = 0; i < horRectCount; i++) {
        float currentLeft = borderRectF.left + diameter + (i * diameter);
        float currentRight = Math.min(borderRectF.right - diameter, currentLeft + diameter);
        if (currentRight - currentLeft < 5) {
          continue;
        }
        RectF topCloudyRectF = new RectF(currentLeft, topRectTopPoint, currentRight,
            topRectBottomPoint);
        canvas.drawArc(topCloudyRectF, 180, 180, false, borderPaint);

        RectF bottomCloudyRectF = new RectF(currentLeft, bottomRectTopPoint, currentRight,
            bottomRectBottomPoint);
        canvas.drawArc(bottomCloudyRectF, 0, 180, false, borderPaint);
      }

    }

    private void drawRoundedCorner(Canvas canvas, float left, float top, float startAngle,
        float sweepAngle) {
      RectF cornerRect = new RectF(left, top, left + diameter, top + diameter);
      canvas.drawArc(cornerRect, startAngle, sweepAngle, false, borderPaint);
    }


    private void drawSolidCircle(Canvas canvas) {
      int padding = 10;
      float circleWidth = getHeight() - padding;
      float circleLeft = (float) getWidth() / 2F - (circleWidth / 2);
      sizeRect.set((int) circleLeft,
          padding / 2,
          (int) (circleLeft + circleWidth),
          getHeight() - (padding /2));
      fillRectF.set(sizeRect.left + borderWidth, sizeRect.top + borderWidth,
          sizeRect.right - borderWidth, sizeRect.bottom - borderWidth);
      canvas.drawOval(fillRectF, fillPaint);
      borderRectF.set(fillRectF.left - (borderWidth / 2F),
          fillRectF.top - (borderWidth / 2F),
          fillRectF.right + (borderWidth / 2F),
          fillRectF.bottom + (borderWidth / 2F));
      canvas.drawOval(borderRectF, borderPaint);

    }



    private void drawCloudyCircle(Canvas canvas) {
      int padding = 10;
      float circleWidth = getHeight() - padding;
      float circleLeft = (float) getWidth() / 2F - (circleWidth / 2);
      sizeRect.set((int) circleLeft,
          padding / 2,
          (int) (circleLeft + circleWidth),
          getHeight() - (padding /2));
      fillRectF.set(sizeRect.left + borderWidth, sizeRect.top + borderWidth,
          sizeRect.right - borderWidth, sizeRect.bottom - borderWidth);
      canvas.drawOval(fillRectF, fillPaint);
      borderRectF.set(fillRectF.left - (borderWidth / 2F),
          fillRectF.top - (borderWidth / 2F),
          fillRectF.right + (borderWidth / 2F),
          fillRectF.bottom + (borderWidth / 2F));

      drawCirclesOnBorder(canvas);
    }

    private void drawCirclesOnBorder(Canvas canvas) {

      float smallCircleDiameter = diameter;
      float smallCircleRadius = smallCircleDiameter / 2;
      RectF borderRectF = new RectF(
          fillRectF.left + smallCircleRadius,
          fillRectF.top + smallCircleRadius ,
          fillRectF.right -smallCircleRadius,
          fillRectF.bottom - smallCircleRadius
      );

      float circleWidth = borderRectF.width();
      float centerX = borderRectF.centerX();
      float centerY = borderRectF.centerY();
      float radius = circleWidth / 2;

      float borderWidth = this.borderWidth;
      float borderRadius = radius + (borderWidth / 2);

      double circumference = 2 * Math.PI * borderRadius;

      int numberOfCircles = (int) (circumference / smallCircleDiameter);

      float angleInterval = 360f / numberOfCircles;

      for (int i = 0; i < numberOfCircles; i++) {
        float angle = angleInterval * i;
        float x = centerX + borderRadius * (float) Math.cos(Math.toRadians(angle));
        float y = centerY + borderRadius * (float) Math.sin(Math.toRadians(angle));

        RectF smallCircleRect = new RectF(
            x - smallCircleRadius,
            y - smallCircleRadius,
            x + smallCircleRadius,
            y + smallCircleRadius
        );
        float startAngle = 280 + angle;
        canvas.drawArc(smallCircleRect, startAngle, 170, false, borderPaint);
      }
    }

  }


}
