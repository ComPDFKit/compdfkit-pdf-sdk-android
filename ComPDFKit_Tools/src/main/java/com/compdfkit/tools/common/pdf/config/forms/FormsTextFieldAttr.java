/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.forms;


import android.graphics.Color;

import androidx.annotation.FloatRange;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.common.pdf.config.annot.AnnotFreetextAttr;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;


public class FormsTextFieldAttr extends FormsAttr {

    private String fontColor;

    private float fontSize;

    private boolean isBold;

    private boolean isItalic;

    private boolean multiline = true;

    private AnnotFreetextAttr.Alignment alignment = AnnotFreetextAttr.Alignment.LEFT;

    private CPDFTextAttribute.FontNameHelper.FontType typeface;

    public String getFontColorHex() {
        return fontColor;
    }

    public int getFontColor() {
        try {
            return Color.parseColor(fontColor);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(@FloatRange(from = 0.0) float fontSize) {
        if (fontSize > 100){
            fontSize = 100;
        }
        if (fontSize < 0){
            fontSize = 10;
        }
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public AnnotFreetextAttr.Alignment getAlignment() {
        return alignment;
    }

    public CAnnotStyle.Alignment getAnnotStyleAlignment() {
        switch (alignment) {
            case RIGHT:
                return CAnnotStyle.Alignment.RIGHT;
            case CENTER:
                return CAnnotStyle.Alignment.CENTER;
            default:
                return CAnnotStyle.Alignment.LEFT;
        }
    }

    public void setAlignment(AnnotFreetextAttr.Alignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public void setFillColor(String fillColor) {
        super.setFillColor(fillColor);
    }

    @Override
    public String getFillColorHex() {
        return super.getFillColorHex();
    }

    @Override
    public int getFillColor() {
        return super.getFillColor();
    }

    @Override
    public void setBorderColor(String borderColor) {
        super.setBorderColor(borderColor);
    }

    @Override
    public int getBorderColor() {
        return super.getBorderColor();
    }

    @Override
    public String getBorderColorHex() {
        return super.getBorderColorHex();
    }

    @Override
    public float getBorderWidth() {
        return super.getBorderWidth();
    }

    @Override
    public void setBorderWidth(float borderWidth) {
        super.setBorderWidth(borderWidth);
    }

    public void setTypeface(CPDFTextAttribute.FontNameHelper.FontType typeface) {
        this.typeface = typeface;
    }

    public CPDFTextAttribute.FontNameHelper.FontType getTypeface() {
        return typeface;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public boolean isMultiline() {
        return multiline;
    }
}
