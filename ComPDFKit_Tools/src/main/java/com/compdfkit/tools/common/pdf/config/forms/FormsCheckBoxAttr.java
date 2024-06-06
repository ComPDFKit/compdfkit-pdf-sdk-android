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

import com.compdfkit.core.annotation.form.CPDFWidget;

public class FormsCheckBoxAttr extends FormsAttr {

    public boolean isChecked;

    public CPDFWidget.CheckStyle checkedStyle = CPDFWidget.CheckStyle.CK_Check;

    private String checkedColor;

    public String getCheckedColorHex() {
        return checkedColor;
    }

    public int getCheckedColor() {
        try {
            return Color.parseColor(checkedColor);
        }catch (Exception e){
            return Color.parseColor("#43474D");
        }
    }

    public void setCheckedColor(String checkedColor) {
        this.checkedColor = checkedColor;
    }

    public enum CheckedStyle{
        Check,

        Circle,

        Cross,

        Diamond,

        Square,

        Star;

        public static CheckedStyle fromString(String str) {
            try {
                String firstLetter = str.substring(0, 1).toUpperCase();
                String result = firstLetter + str.substring(1);
                return CheckedStyle.valueOf(result);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
