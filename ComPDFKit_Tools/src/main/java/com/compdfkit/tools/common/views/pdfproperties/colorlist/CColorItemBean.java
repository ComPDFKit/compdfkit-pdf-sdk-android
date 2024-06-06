/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.colorlist;


import android.graphics.Color;

import androidx.annotation.ColorInt;

class CColorItemBean {

    private int color;

    private boolean isSelect;

    private boolean isColorPicker;

    public CColorItemBean(@ColorInt int color){
        this.color = color;
    }

    public static CColorItemBean colorPickerItem(){
        CColorItemBean bean = new CColorItemBean(Color.WHITE);
        bean.setColorPicker(true);
        return bean;
    }

    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isColorPicker() {
        return isColorPicker;
    }

    public void setColorPicker(boolean colorPicker) {
        isColorPicker = colorPicker;
    }
}
