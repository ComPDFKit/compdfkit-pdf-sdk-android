/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.config.forms;


import android.graphics.Color;

import androidx.annotation.FloatRange;

import com.compdfkit.core.annotation.CPDFTextAttribute;

public class FormsListBoxAttr extends FormsAttr {

    private String fontColor;

    private float fontSize = 20;

    private CPDFTextAttribute.FontNameHelper.FontType typeface;

    private boolean isBold;

    private boolean isItalic;

    public String getFontColorHex() {
        return fontColor;
    }

    public int getFontColor() {
        try {
            return Color.parseColor(fontColor);
        }catch (Exception e){
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
        if (fontSize < 0){
            fontSize = 0;
        }
        this.fontSize = fontSize;
    }

    public CPDFTextAttribute.FontNameHelper.FontType getTypeface() {
        return typeface;
    }

    public void setTypeface(CPDFTextAttribute.FontNameHelper.FontType typeface) {
        this.typeface = typeface;
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
}
