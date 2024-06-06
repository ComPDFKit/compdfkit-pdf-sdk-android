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

import java.io.Serializable;


public class FormsAttr implements Serializable {

    private String fillColor;

    private String borderColor;

    private float borderWidth = 5;


    public String getFillColorHex() {
        return fillColor;
    }

    public int getFillColor() {
        try{
            return Color.parseColor(fillColor);
        }catch (Exception e){
            return Color.parseColor("#DDE9FF");
        }
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }


    public int getBorderColor() {
        try{
            return Color.parseColor(borderColor);
        }catch (Exception e){
            return Color.parseColor("#1460F3");
        }
    }

    public String getBorderColorHex() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
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
}
