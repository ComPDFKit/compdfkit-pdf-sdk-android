/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.annot;


import android.graphics.Color;

import androidx.annotation.FloatRange;

import com.compdfkit.core.annotation.CPDFAnnotation;

import java.io.Serializable;

public class AnnotShapeAttr extends AnnotAttr {

    private String fillColor;

    private String borderColor;

    private int borderAlpha = 255;

    private int colorAlpha = 255;

    private float borderWidth = 10;

    private AnnotLineType startLineType = AnnotLineType.None;

    private AnnotLineType tailLineType = AnnotLineType.None;

    private ShapeBorderStyle borderStyle = new ShapeBorderStyle();

    private CPDFAnnotation.CPDFBorderEffectType borderEffectType = CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeSolid;

    public String getFillColorHex() {
        return fillColor;
    }

    public int getFillColor() {
        try{
            return Color.parseColor(fillColor);
        }catch (Exception e){
            return Color.parseColor(DEFAULT_COLOR);
        }
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getBorderColorHex() {
        return borderColor;
    }

    public int getBorderColor() {
        try{
            return Color.parseColor(borderColor);
        }catch (Exception e){
            return Color.parseColor(DEFAULT_COLOR);
        }
    }

    public void setBorderAlpha(int borderAlpha) {
        if (borderAlpha < 0){
            borderAlpha = 0;
        }
        this.borderAlpha = Math.min(borderAlpha, 255);
    }

    public void setColorAlpha(int colorAlpha) {
        if (colorAlpha < 0){
            colorAlpha = 0;
        }
        this.colorAlpha = Math.min(colorAlpha, 255);
    }

    public int getColorAlpha() {
        return colorAlpha;
    }

    public int getBorderAlpha() {
        return borderAlpha;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(@FloatRange(from = 0.0) float borderWidth) {
        if (borderWidth < 0){
            borderWidth = 0;
        }
        this.borderWidth = borderWidth;
    }

    public AnnotLineType getStartLineType() {
        return startLineType;
    }

    public void setStartLineType(AnnotLineType startLineType) {
        this.startLineType = startLineType;
    }

    public AnnotLineType getTailLineType() {
        return tailLineType;
    }

    public void setTailLineType(AnnotLineType tailLineType) {
        this.tailLineType = tailLineType;
    }

    public ShapeBorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(ShapeBorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }

    public void setBorderEffectType(CPDFAnnotation.CPDFBorderEffectType borderEffectType) {
        this.borderEffectType = borderEffectType;
    }

    public CPDFAnnotation.CPDFBorderEffectType getBorderEffectType() {
        return borderEffectType;
    }

    public static class ShapeBorderStyle implements Serializable {

        public StyleType borderStyle = StyleType.Solid;

        public float dashWidth = 8.0F;

        public float dashGap = 0.0F;

        public enum StyleType {

            Unknown(-1),

            Solid(0),

            Dashed(1),

            Beveled(2),

            Inset(3),

            Underline(4);

            public int id;

            StyleType(int id){
                this.id = id;
            }

            public static  StyleType fromString(String str) {
                try {
                    String firstLetter = str.substring(0, 1).toUpperCase();
                    String result = firstLetter + str.substring(1);
                    return StyleType.valueOf(result);
                } catch (Exception e) {
                    return null;
                }
            }
        }
    }

    public enum AnnotLineType{

        None(0),

        OpenArrow(1),

        ClosedArrow(2),

        Square(3),

        Circle(4),

        Diamond(5),

        Butt(6),

        Ropenarrow(7),

        Slash(9);

        public final int id;

        private AnnotLineType(int id){
            this.id = id;
        }

        public static AnnotLineType fromString(String str) {
            try {
                String firstLetter = str.substring(0, 1).toUpperCase();
                String result = firstLetter + str.substring(1);
                return AnnotLineType.valueOf(result);
            } catch (Exception e) {
                return AnnotLineType.None;
            }
        }
    }
}

