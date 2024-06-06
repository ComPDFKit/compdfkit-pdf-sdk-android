/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;


import android.net.Uri;

import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.annotation.form.CPDFWidget;

import java.util.Map;

public class CBasicOnStyleChangeListener  implements CAnnotStyle.OnAnnotStyleChangeListener {
    @Override
    public void onChangeColor(int color) {

    }

    @Override
    public void onChangeOpacity(int opacity) {

    }

    @Override
    public void onChangeBorderWidth(float borderWidth) {

    }

    @Override
    public void onChangeLineColor(int color) {

    }

    @Override
    public void onChangeLineColorOpacity(int opacity) {

    }

    @Override
    public void onChangeFillColor(int color) {

    }

    @Override
    public void onChangeFillColorOpacity(int opacity) {

    }

    @Override
    public void onChangeBorderStyle(CPDFBorderStyle style) {

    }

    @Override
    public void onChangeStartLineType(CPDFLineAnnotation.LineType lineType) {

    }

    @Override
    public void onChangeTailLineType(CPDFLineAnnotation.LineType lineType) {

    }

    @Override
    public void onChangeFontBold(boolean bold) {


    }

    @Override
    public void onChangeFontItalic(boolean italic) {

    }

    @Override
    public void onChangeTextColor(int textColor) {

    }

    @Override
    public void onChangeTextColorOpacity(int textColorOpacity) {

    }

    @Override
    public void onChangeFontSize(int fontSize) {

    }

    @Override
    public void onChangeFontType(CPDFTextAttribute.FontNameHelper.FontType fontType) {

    }

    @Override
    public void onChangeTextAlignment(CAnnotStyle.Alignment alignment) {

    }

    @Override
    public void onChangeAnnotExternFontType(String fontName) {

    }

    @Override
    public void onChangeImagePath(String imagePath, Uri imageUri) {

    }

    @Override
    public void onChangeTextStamp(CPDFStampAnnotation.TextStamp textStamp) {

    }

    @Override
    public void onChangeStandardStamp(CPDFStampAnnotation.StandardStamp standardStamp) {

    }

    @Override
    public void onChangeRotation(float rotation) {

    }

    @Override
    public void onChangeMirror(CAnnotStyle.Mirror mirror) {

    }

    @Override
    public void onChangeEditArea(CAnnotStyle.EditUpdatePropertyType type) {

    }

    @Override
    public void onChangeFieldName(String fieldName) {

    }

    @Override
    public void onChangeTextFieldDefaultValue(String defaultValue) {

    }

    @Override
    public void onChangeHideForm(boolean hide) {

    }

    @Override
    public void onChangeMultiLine(boolean multiLine) {

    }

    @Override
    public void onChangeCheckStyle(CPDFWidget.CheckStyle checkStyle) {

    }

    @Override
    public void onChangeIsChecked(boolean isChecked) {

    }

    @Override
    public void onChangeExtraMap(Map<String, Object> extraMap) {

    }

    @Override
    public void onChangeSignFieldsBorderStyle(CPDFWidget.BorderStyle borderStyle) {

    }
}
