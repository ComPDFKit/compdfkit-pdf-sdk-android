/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.annot;


import android.graphics.Color;

import androidx.annotation.IntRange;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;

public class AnnotFreetextAttr extends AnnotAttr {

    private String fontColor;

    private int fontColorAlpha = 255;

    private int fontSize = 20;

    private boolean isBold;

    private boolean isItalic;

    private Alignment alignment = Alignment.LEFT;

    private CPDFTextAttribute.FontNameHelper.FontType typeface = CPDFTextAttribute.FontNameHelper.FontType.Unknown;

    public String getFontColorHex() {
        return fontColor;
    }

    public int getFontColor() {
        try {
            return Color.parseColor(fontColor);
        } catch (Exception e) {
            return Color.parseColor(DEFAULT_COLOR);
        }
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public int getFontColorAlpha() {
        return fontColorAlpha;
    }

    public void setFontColorAlpha(@IntRange(from = 0, to = 255) int fontColorAlpha) {
        if (fontColorAlpha <0){
            fontColorAlpha = 0;
        }
        this.fontColorAlpha = Math.min(fontColorAlpha, 255);
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(@IntRange(from = 0) int fontSize) {
        if (fontSize < 0){
            fontSize = 0;
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

    public Alignment getAlignment() {
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

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public CPDFTextAttribute.FontNameHelper.FontType getTypeface() {
        return typeface;
    }

    public void setTypeface(CPDFTextAttribute.FontNameHelper.FontType typeface) {
        this.typeface = typeface;
    }

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT;

        public static Alignment fromString(String str) {
            try {
                return Alignment.valueOf(str.toUpperCase());
            } catch (Exception e) {
                return Alignment.LEFT;
            }
        }
    }

}
