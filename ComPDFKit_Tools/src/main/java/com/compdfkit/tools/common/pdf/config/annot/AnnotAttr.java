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

import androidx.annotation.IntRange;

import java.io.Serializable;

public class AnnotAttr implements Serializable {
    protected static final String DEFAULT_COLOR = "#1460F3";

    private String color;


    private int alpha = 255;


    public String getColorHex() {
        return color;
    }

    public int getColor() {
        try{
            return Color.parseColor(color);
        }catch (Exception e){
            return Color.parseColor(DEFAULT_COLOR);
        }
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        if (alpha <0){
            alpha = 0;
        }
        this.alpha = Math.min(alpha, 255);
    }

}
