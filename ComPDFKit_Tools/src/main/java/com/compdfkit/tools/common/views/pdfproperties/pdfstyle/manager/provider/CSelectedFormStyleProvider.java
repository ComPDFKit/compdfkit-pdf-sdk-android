/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider;


import android.graphics.Color;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFTextAlignment;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.annotation.form.CPDFCheckboxWidget;
import com.compdfkit.core.annotation.form.CPDFComboboxWidget;
import com.compdfkit.core.annotation.form.CPDFListboxWidget;
import com.compdfkit.core.annotation.form.CPDFPushbuttonWidget;
import com.compdfkit.core.annotation.form.CPDFRadiobuttonWidget;
import com.compdfkit.core.annotation.form.CPDFTextWidget;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.proxy.form.CPDFCheckboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFComboboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFListboxWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFRadiobuttonWidgetImpl;
import com.compdfkit.ui.proxy.form.CPDFTextWidgetImpl;
import com.compdfkit.ui.reader.PageView;

import java.util.LinkedHashSet;


public class CSelectedFormStyleProvider implements CStyleProvider {

    private CPDFBaseAnnotImpl baseAnnotImpl;

    private PageView pageView;

    public CSelectedFormStyleProvider(CPDFBaseAnnotImpl baseAnnotImpl, PageView pageView) {
        this.baseAnnotImpl = baseAnnotImpl;
        this.pageView = pageView;
    }

    @Override
    public void updateStyle(CAnnotStyle annotStyle) {
        LinkedHashSet<CAnnotStyle> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(annotStyle);
        updateStyle(linkedHashSet);
    }

    @Override
    public void updateStyle(LinkedHashSet<CAnnotStyle> annotStyle) {
        for (CAnnotStyle style : annotStyle) {
            CPDFAnnotation pdfAnnotation = baseAnnotImpl.onGetAnnotation();
            if (baseAnnotImpl instanceof CPDFTextWidgetImpl) {
                CPDFTextWidget textWidget = (CPDFTextWidget) pdfAnnotation;
                textWidget.setFieldName(style.getFormFieldName());
                textWidget.setFontSize(style.getFontSize());
                textWidget.setFontColor(style.getTextColor());
                textWidget.setFillColor(style.getFillColor());
                textWidget.setBorderColor(style.getLineColor());
                textWidget.setBorderWidth(style.getBorderWidth());
                textWidget.setMultiLine(style.isFormMultiLine());
                textWidget.setHidden(style.isHideForm());
                String fontName = CPDFTextAttribute.FontNameHelper.obtainFontName(style.getFontType(), style.isFontBold(), style.isFontItalic());
                textWidget.setFontName(fontName);
                textWidget.setText(style.getFormDefaultValue());
                switch (style.getAlignment()) {
                    case LEFT:
                        textWidget.setTextAlignment(CPDFTextAlignment.ALIGNMENT_LEFT);
                        break;
                    case CENTER:
                        textWidget.setTextAlignment(CPDFTextAlignment.ALIGNMENT_CENTER);
                        break;
                    case RIGHT:
                        textWidget.setTextAlignment(CPDFTextAlignment.ALIGNMENT_RIGHT);
                        break;
                    default:
                        textWidget.setTextAlignment(CPDFTextAlignment.ALIGNMENT_UNKNOWN);
                        break;
                }
                textWidget.updateAp();
                baseAnnotImpl.onAnnotAttrChange();
                if (pageView != null) {
                    pageView.invalidate();
                }
            } else if (baseAnnotImpl instanceof CPDFCheckboxWidgetImpl) {
                CPDFCheckboxWidget checkboxWidget = (CPDFCheckboxWidget) pdfAnnotation;
                checkboxWidget.setCheckStyle(style.getCheckStyle());
                checkboxWidget.setColor(style.getColor());
                checkboxWidget.setBorderColor(style.getLineColor());
                checkboxWidget.setFillColor(style.getFillColor());
                checkboxWidget.setHidden(style.isHideForm());
                checkboxWidget.setFieldName(style.getFormFieldName());
                checkboxWidget.setChecked(style.isChecked());
                checkboxWidget.updateAp();
                baseAnnotImpl.onAnnotAttrChange();
                if (pageView != null) {
                    pageView.invalidate();
                }
            } else if (baseAnnotImpl instanceof CPDFRadiobuttonWidgetImpl) {
                CPDFRadiobuttonWidget radiobuttonWidget = (CPDFRadiobuttonWidget) pdfAnnotation;
                radiobuttonWidget.setCheckStyle(style.getCheckStyle());
                radiobuttonWidget.setColor(style.getColor());
                radiobuttonWidget.setBorderColor(style.getLineColor());
                radiobuttonWidget.setFillColor(style.getFillColor());
                radiobuttonWidget.setHidden(style.isHideForm());
                radiobuttonWidget.setFieldName(style.getFormFieldName());
                radiobuttonWidget.setChecked(style.isChecked());
                radiobuttonWidget.updateAp();
                baseAnnotImpl.onAnnotAttrChange();
                if (pageView != null) {
                    pageView.invalidate();
                }
            } else if (baseAnnotImpl instanceof CPDFListboxWidgetImpl) {
                CPDFListboxWidget listBoxWidget = (CPDFListboxWidget) pdfAnnotation;
                listBoxWidget.setFieldName(style.getFormFieldName());
                listBoxWidget.setHidden(style.isHideForm());
                listBoxWidget.setBorderWidth(2);
                listBoxWidget.setFontSize(style.getFontSize());
                listBoxWidget.setFontColor(style.getTextColor());
                listBoxWidget.setFillColor(style.getFillColor());
                if (style.getLineColor() == Color.TRANSPARENT){
                    listBoxWidget.setBorderWidth(0);
                }
                listBoxWidget.setBorderColor(style.getLineColor());
                String fontName = CPDFTextAttribute.FontNameHelper.obtainFontName(style.getFontType(), style.isFontBold(), style.isFontItalic());

                listBoxWidget.setFontName(fontName);
                listBoxWidget.updateAp();
                baseAnnotImpl.onAnnotAttrChange();
                ((CPDFListboxWidgetImpl) baseAnnotImpl).refresh();
                if (pageView != null) {
                    pageView.invalidate();
                }
            }  else if (baseAnnotImpl instanceof CPDFComboboxWidgetImpl) {
                CPDFComboboxWidget comboBoxWidget = (CPDFComboboxWidget) pdfAnnotation;
                comboBoxWidget.setFieldName(style.getFormFieldName());
                comboBoxWidget.setHidden(style.isHideForm());
                comboBoxWidget.setBorderWidth(2);
                comboBoxWidget.setFontSize(style.getFontSize());
                comboBoxWidget.setFontColor(style.getTextColor());
                comboBoxWidget.setFillColor(style.getFillColor());
                if (style.getLineColor() == Color.TRANSPARENT){
                    comboBoxWidget.setBorderWidth(0);
                }
                comboBoxWidget.setBorderColor(style.getLineColor());
                String fontName = CPDFTextAttribute.FontNameHelper.obtainFontName(style.getFontType(), style.isFontBold(), style.isFontItalic());
                comboBoxWidget.setFontName(fontName);
                comboBoxWidget.updateAp();
                baseAnnotImpl.onAnnotAttrChange();
                if (pageView != null) {
                    pageView.invalidate();
                }
            } else if (baseAnnotImpl instanceof CPDFPushbuttonWidgetImpl){
                CPDFPushbuttonWidget pushButtonWidget = (CPDFPushbuttonWidget) pdfAnnotation;
                pushButtonWidget.setFieldName(style.getFormFieldName());
                pushButtonWidget.setHidden(style.isHideForm());
                pushButtonWidget.setBorderWidth(2);
                pushButtonWidget.setFontSize(style.getFontSize());
                pushButtonWidget.setFontColor(style.getTextColor());
                pushButtonWidget.setFillColor(style.getFillColor());
                pushButtonWidget.setBorderColor(style.getLineColor());
                String fontName = CPDFTextAttribute.FontNameHelper.obtainFontName(style.getFontType(), style.isFontBold(), style.isFontItalic());
                pushButtonWidget.setFontName(fontName);
                pushButtonWidget.setButtonTitle(style.getFormDefaultValue());
                pushButtonWidget.updateAp();
                baseAnnotImpl.onAnnotAttrChange();
                if (pageView != null) {
                    pageView.invalidate();
                }
            }
        }
    }

    @Override
    public CAnnotStyle getStyle(CStyleType type) {
        CAnnotStyle style = new CAnnotStyle(type);
        CPDFAnnotation pdfAnnotation = baseAnnotImpl.onGetAnnotation();
        if (baseAnnotImpl instanceof CPDFTextWidgetImpl) {
            //text fields
            CPDFTextWidget textWidget = (CPDFTextWidget) pdfAnnotation;
            style.setFormFieldName(textWidget.getFieldName());
            style.setHideForm(textWidget.isHidden());
            style.setFormMultiLine(textWidget.isMultiLine());
            style.setFontColor(textWidget.getFontColor());
            style.setBorderColor(textWidget.getBorderColor());
            style.setFillColor(textWidget.getFillColor());
            CPDFTextAttribute.FontNameHelper.FontType fontType = CPDFTextAttribute.FontNameHelper.getFontType(textWidget.getFontName());
            style.setFontType(fontType);
            style.setBorderWidth(textWidget.getBorderWidth());
            style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(textWidget.getFontName()));
            style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(textWidget.getFontName()));
            style.setFontSize((int) textWidget.getFontSize());
            style.setFormDefaultValue(textWidget.getText());
            switch (textWidget.getTextAlignment()) {
                case ALIGNMENT_LEFT:
                    style.setAlignment(CAnnotStyle.Alignment.LEFT);
                    break;
                case ALIGNMENT_CENTER:
                    style.setAlignment(CAnnotStyle.Alignment.CENTER);
                    break;
                case ALIGNMENT_RIGHT:
                    style.setAlignment(CAnnotStyle.Alignment.RIGHT);
                    break;
                default:
                    style.setAlignment(CAnnotStyle.Alignment.UNKNOWN);
                    break;
            }
        } else if (baseAnnotImpl instanceof CPDFCheckboxWidgetImpl) {
            CPDFCheckboxWidget textWidget = (CPDFCheckboxWidget) pdfAnnotation;
            style.setColor(textWidget.getColor());
            style.setBorderColor(textWidget.getBorderColor());
            style.setFillColor(textWidget.getFillColor());
            style.setCheckStyle(textWidget.getCheckStyle());
            style.setFormFieldName(textWidget.getFieldName());
            style.setHideForm(textWidget.isHidden());
            style.setChecked(textWidget.isChecked());
        } else if (baseAnnotImpl instanceof CPDFRadiobuttonWidgetImpl){
            CPDFRadiobuttonWidget textWidget = (CPDFRadiobuttonWidget) pdfAnnotation;
            style.setColor(textWidget.getColor());
            style.setBorderColor(textWidget.getBorderColor());
            style.setFillColor(textWidget.getFillColor());
            style.setCheckStyle(textWidget.getCheckStyle());
            style.setFormFieldName(textWidget.getFieldName());
            style.setHideForm(textWidget.isHidden());
            style.setChecked(textWidget.isChecked());
        } else if (baseAnnotImpl instanceof CPDFListboxWidgetImpl){
            CPDFListboxWidget listBoxWidget = (CPDFListboxWidget) pdfAnnotation;
            style.setBorderWidth(2);
            style.setBorderColor(listBoxWidget.getBorderColor());
            style.setFillColor(listBoxWidget.getFillColor());
            style.setFontColor(listBoxWidget.getFontColor());
            style.setFontSize((int) listBoxWidget.getFontSize());
            style.setFormFieldName(listBoxWidget.getFieldName());
            style.setHideForm(listBoxWidget.isHidden());
            CPDFTextAttribute.FontNameHelper.FontType fontType = CPDFTextAttribute.FontNameHelper.getFontType(listBoxWidget.getFontName());
            style.setFontType(fontType);
            style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(listBoxWidget.getFontName()));
            style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(listBoxWidget.getFontName()));
        } else if (baseAnnotImpl instanceof CPDFComboboxWidgetImpl){
            CPDFComboboxWidget comboBoxWidget = (CPDFComboboxWidget) pdfAnnotation;
            style.setBorderWidth(2);
            style.setBorderColor(comboBoxWidget.getBorderColor());
            style.setFillColor(comboBoxWidget.getFillColor());
            style.setFontColor(comboBoxWidget.getFontColor());
            style.setFontSize((int) comboBoxWidget.getFontSize());
            style.setFormFieldName(comboBoxWidget.getFieldName());
            style.setHideForm(comboBoxWidget.isHidden());
            CPDFTextAttribute.FontNameHelper.FontType fontType = CPDFTextAttribute.FontNameHelper.getFontType(comboBoxWidget.getFontName());
            style.setFontType(fontType);
            style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(comboBoxWidget.getFontName()));
            style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(comboBoxWidget.getFontName()));
        } else if (baseAnnotImpl instanceof CPDFPushbuttonWidgetImpl){
            CPDFPushbuttonWidget pushButtonWidget = (CPDFPushbuttonWidget) pdfAnnotation;
            style.setBorderWidth(2);
            style.setBorderColor(pushButtonWidget.getBorderColor());
            style.setFillColor(pushButtonWidget.getFillColor());
            style.setFontColor(pushButtonWidget.getFontColor());
            style.setFontSize((int) pushButtonWidget.getFontSize());
            style.setFormFieldName(pushButtonWidget.getFieldName());
            style.setHideForm(pushButtonWidget.isHidden());
            CPDFTextAttribute.FontNameHelper.FontType fontType = CPDFTextAttribute.FontNameHelper.getFontType(pushButtonWidget.getFontName());
            style.setFontType(fontType);
            style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(pushButtonWidget.getFontName()));
            style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(pushButtonWidget.getFontName()));
            style.setFormDefaultValue(pushButtonWidget.getButtonTitle());
        }
        return style;
    }
}
