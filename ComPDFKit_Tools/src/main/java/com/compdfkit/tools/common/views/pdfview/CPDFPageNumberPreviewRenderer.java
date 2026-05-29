/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES. This notice
 * may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.Nullable;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.ui.widget.CPDFPageNavigator;

final class CPDFPageNumberPreviewRenderer implements CPDFPageNavigator.DragPreviewRenderer {

  private static final float DEFAULT_TEXT_SP = 14F;

  private final Paint textBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final float cornerRadius;

  CPDFPageNumberPreviewRenderer(int backgroundColor, int textSize, int cornerRadius) {
    this.cornerRadius = cornerRadius;
    textBgPaint.setStyle(Paint.Style.FILL);
    textBgPaint.setColor(backgroundColor);
    textPaint.setStyle(Paint.Style.FILL);
    textPaint.setColor(0xFFFFFFFF);
    textPaint.setTextSize(textSize);
    textPaint.setTextAlign(Paint.Align.CENTER);
  }

  static int getDefaultTextSizePx(Context context) {
    return (int) (DEFAULT_TEXT_SP * context.getResources().getDisplayMetrics().scaledDensity);
  }

  static int calculatePreviewWidth(Context context, CPDFDocument document, int textSizePx) {
    int labelWidth = CDimensUtils.dp2px(context, 56);
    Paint measurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    measurePaint.setTextSize(textSizePx);
    int maxPageNumberWidth = (int) measurePaint.measureText(String.valueOf(document.getPageCount()));
    return Math.max(labelWidth, maxPageNumberWidth + CDimensUtils.dp2px(context, 24));
  }

  @Override
  public void draw(Canvas canvas, RectF rectF, int pageIndex, int pageCount, @Nullable Bitmap bitmap,
      CPDFPageNavigator.NavigatorPosition navigatorPosition) {
    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, textBgPaint);

    String content = String.valueOf(pageIndex + 1);
    Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
    float baseline = rectF.centerY() - (fontMetrics.ascent + fontMetrics.descent) / 2F;
    canvas.drawText(content, rectF.centerX(), baseline, textPaint);
  }
}