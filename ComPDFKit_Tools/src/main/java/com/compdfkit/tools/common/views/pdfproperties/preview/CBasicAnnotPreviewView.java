/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFAnnotation.CPDFBorderEffectType;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;

abstract class CBasicAnnotPreviewView extends FrameLayout {

    public CBasicAnnotPreviewView(@NonNull Context context) {
        this(context, null);
    }

    public CBasicAnnotPreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CBasicAnnotPreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bindView();
        initView();
    }

    protected abstract void bindView();


    protected abstract void initView();

    public void setColor(@ColorInt int color) {
    }

    public void setOpacity(@IntRange(from = 0, to = 255) int colorOpacity) {
    }

    public void setBorderWidth(int borderWidth) {

    }

    public void setBorderColor(@ColorInt int color) {
    }

    public void setBorderColorOpacity(@IntRange(from = 0, to = 255) int colorOpacity) {
    }

    public void setDashedGsp(int dashedSpace) {
    }

    public void setShapeType(CAnnotShapePreviewView.CShapeView.ShapeType shapeType) {
    }

    public void setStartLineType(CPDFLineAnnotation.LineType lineType){}

    public void setTailLineType(CPDFLineAnnotation.LineType lineType){}

    public void setTextColor(int textColor){}

    public void setTextColorOpacity(int textColorOpacity){}

    public void setTextAlignment(CAnnotStyle.Alignment alignment){}

    public void setFontSize(int fontSize){}

    public void setMirror(CAnnotStyle.Mirror mirror){

    }

    public void setRotationAngle(float angle){

    }

    public void setFontPsName(String fontPsName){}

    public void setImage(Bitmap bitmap){}

    public void setBorderEffectType(CPDFBorderEffectType type){

    }
}
