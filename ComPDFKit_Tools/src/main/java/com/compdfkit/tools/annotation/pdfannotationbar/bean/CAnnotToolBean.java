/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationbar.bean;


import android.graphics.Color;

import androidx.annotation.DrawableRes;

import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;

public class CAnnotToolBean {

    private CAnnotationType type;

    @DrawableRes
    private int iconResId;

    private boolean select;

    private int bgColor = Color.TRANSPARENT;

    private int colorOpacity = 255;

    public CAnnotToolBean(CAnnotationType type, @DrawableRes int iconResId){
        this.type = type;
        this.iconResId = iconResId;
    }

    public CAnnotToolBean(CAnnotationType type, @DrawableRes int iconResId, int bgColor, int colorOpacity){
        this.type = type;
        this.iconResId = iconResId;
        this.bgColor = bgColor;
        this.colorOpacity = colorOpacity;
    }

    public CAnnotationType getType() {
        return type;
    }

    public void setType(CAnnotationType type) {
        this.type = type;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setColorOpacity(int colorOpacity) {
        this.colorOpacity = colorOpacity;
    }

    public int getColorOpacity() {
        return colorOpacity;
    }
}
