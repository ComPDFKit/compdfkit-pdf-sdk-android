/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config.annot;


import androidx.annotation.FloatRange;

public class AnnotInkAttr extends AnnotAttr {

    private float borderWidth = 10;

    private float eraserWidth = 10;

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(@FloatRange(from = 0.0) float borderWidth) {
        if (borderWidth > 10){
            borderWidth = 10;
        }
        if (borderWidth <= 0){
            borderWidth = 0;
        }
        this.borderWidth = borderWidth;
    }

    public float getEraserWidth() {
        return eraserWidth;
    }

    public void setEraserWidth(@FloatRange(from = 0.0) float eraserWidth) {
        if (eraserWidth > 10){
            eraserWidth = 10;
        }
        if (eraserWidth <= 0){
            eraserWidth = 0;
        }
        this.eraserWidth = eraserWidth;
    }
}
