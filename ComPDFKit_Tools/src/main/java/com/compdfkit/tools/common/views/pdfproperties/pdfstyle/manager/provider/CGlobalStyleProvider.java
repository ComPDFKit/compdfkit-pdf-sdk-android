/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider;


import android.text.TextUtils;

import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFFreetextAnnotation;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFTextAlignment;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CBasicOnStyleChangeListener;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.attribute.CPDFAnnotAttribute;
import com.compdfkit.ui.attribute.CPDFAttributeDataFether;
import com.compdfkit.ui.attribute.CPDFCircleAttr;
import com.compdfkit.ui.attribute.CPDFFreetextAttr;
import com.compdfkit.ui.attribute.CPDFHighlightAttr;
import com.compdfkit.ui.attribute.CPDFInkAttr;
import com.compdfkit.ui.attribute.CPDFLineAttr;
import com.compdfkit.ui.attribute.CPDFSquareAttr;
import com.compdfkit.ui.attribute.CPDFSquigglyAttr;
import com.compdfkit.ui.attribute.CPDFStampAttr;
import com.compdfkit.ui.attribute.CPDFStrikeoutAttr;
import com.compdfkit.ui.attribute.CPDFTextAttr;
import com.compdfkit.ui.attribute.CPDFUnderlineAttr;
import com.compdfkit.ui.attribute.IAttributeCallback;
import com.compdfkit.ui.attribute.form.CPDFCheckboxAttr;
import com.compdfkit.ui.attribute.form.CPDFComboboxAttr;
import com.compdfkit.ui.attribute.form.CPDFListboxAttr;
import com.compdfkit.ui.attribute.form.CPDFPushButtonAttr;
import com.compdfkit.ui.attribute.form.CPDFRadiobuttonAttr;
import com.compdfkit.ui.attribute.form.CPDFSignatureWidgetAttr;
import com.compdfkit.ui.attribute.form.CPDFTextfieldAttr;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;

public class CGlobalStyleProvider extends CBasicOnStyleChangeListener implements CStyleProvider {

    private CPDFReaderView readerView;

    private boolean onStore;

    public CGlobalStyleProvider(CPDFReaderView readerView, boolean onStore) {
        this.readerView = readerView;
        this.onStore = onStore;
    }

    public CGlobalStyleProvider(CPDFReaderView readerView) {
        this.readerView = readerView;
        this.onStore = false;
    }

    public CGlobalStyleProvider(CPDFViewCtrl pdfView) {
        this.readerView = pdfView.getCPdfReaderView();
        this.onStore = false;
    }

    public CGlobalStyleProvider(CPDFViewCtrl pdfView, boolean onStore) {
        this.readerView = pdfView.getCPdfReaderView();
        this.onStore = onStore;
    }

    @Override
    public void updateStyle(CAnnotStyle annotStyle) {
        LinkedHashSet<CAnnotStyle> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(annotStyle);
        updateStyle(linkedHashSet);
    }

    @Override
    public void updateStyle(LinkedHashSet<CAnnotStyle> annotStyle) {
        CPDFAnnotAttribute attribute = readerView.getReaderAttribute().getAnnotAttribute();
        for (CAnnotStyle params : annotStyle) {
            if (params == null) {
                continue;
            }
            IAttributeCallback callback = null;
            switch (params.getType()) {
                case ANNOT_TEXT:
                    CPDFTextAttr textAttr = attribute.getTextAttr();
                    textAttr.setColor(params.getColor());
                    textAttr.setAlpha(params.getOpacity());
                    callback = textAttr;
                    break;
                case ANNOT_HIGHLIGHT:
                    CPDFHighlightAttr highlightAttr = attribute.getHighlightAttr();
                    highlightAttr.setColor(params.getColor());
                    highlightAttr.setAlpha(params.getOpacity());
                    callback = highlightAttr;
                    break;
                case ANNOT_UNDERLINE:
                    CPDFUnderlineAttr underlineAttr = attribute.getUnderlineAttr();
                    underlineAttr.setColor(params.getColor());
                    underlineAttr.setAlpha(params.getOpacity());
                    callback = underlineAttr;
                    break;
                case ANNOT_SQUIGGLY:
                    CPDFSquigglyAttr squigglyAttr = attribute.getSquigglyAttr();
                    squigglyAttr.setColor(params.getColor());
                    squigglyAttr.setAlpha(params.getOpacity());
                    callback = squigglyAttr;
                    break;
                case ANNOT_STRIKEOUT:
                    CPDFStrikeoutAttr strikeoutAttr = attribute.getStrikeoutAttr();
                    strikeoutAttr.setColor(params.getColor());
                    strikeoutAttr.setAlpha(params.getOpacity());
                    callback = strikeoutAttr;
                    break;
                case ANNOT_INK:
                    CPDFInkAttr inkAttr = attribute.getInkAttr();
                    inkAttr.setColor(params.getColor());
                    inkAttr.setAlpha(params.getOpacity());
                    inkAttr.setBorderWidth(params.getBorderWidth());
                    inkAttr.setEraseWidth(params.getEraserWidth());
                    callback = inkAttr;
                    if (!onStore) {
                        readerView.invalidateAllChildren();
                    }
                    break;
                case ANNOT_SQUARE:
                    CPDFSquareAttr squareAttr = attribute.getSquareAttr();
                    squareAttr.setFillColor(params.getFillColor());
                    squareAttr.setFillAlpha(params.getFillColorOpacity());
                    squareAttr.setBorderColor(params.getLineColor());
                    squareAttr.setBorderAlpha(params.getLineColorOpacity());
                    squareAttr.setBorderWidth(params.getBorderWidth());
                    squareAttr.setBorderStyle(params.getBorderStyle());
                    callback = squareAttr;
                    break;
                case ANNOT_CIRCLE:
                    CPDFCircleAttr circleAttr = attribute.getCircleAttr();
                    circleAttr.setFillColor(params.getFillColor());
                    circleAttr.setFillAlpha(params.getFillColorOpacity());
                    circleAttr.setBorderColor(params.getLineColor());
                    circleAttr.setBorderAlpha(params.getLineColorOpacity());
                    circleAttr.setBorderWidth(params.getBorderWidth());
                    circleAttr.setBorderStyle(params.getBorderStyle());
                    callback = circleAttr;
                    break;
                case ANNOT_LINE:
                    CPDFLineAttr lineAttr = attribute.getLineAttr();
                    lineAttr.setBorderColor(params.getLineColor());
                    lineAttr.setBorderAlpha(params.getLineColorOpacity());
                    lineAttr.setFillColor(params.getFillColor());
                    lineAttr.setFillAlpha(params.getFillColorOpacity());
                    lineAttr.setBorderWidth(params.getBorderWidth());
                    lineAttr.setBorderStyle(params.getBorderStyle());
                    lineAttr.setLineType(params.getStartLineType(), params.getTailLineType());
                    callback = lineAttr;

                    CPDFAttributeDataFether dataFether1 = new CPDFAttributeDataFether(readerView.getContext());
                    dataFether1.setIntValue("custom_line_attr", "custom_line_head_type", params.getStartLineType().id);
                    dataFether1.setIntValue("custom_line_attr", "custom_line_tail_type", params.getTailLineType().id);
                    dataFether1.setIntValue("custom_line_attr", "custom_line_border_color", params.getLineColor());
                    dataFether1.setIntValue("custom_line_attr", "custom_line_border_color_alpha", params.getLineColorOpacity());
                    dataFether1.setIntValue("custom_line_attr", "custom_line_fill_color", params.getFillColor());
                    dataFether1.setIntValue("custom_line_attr", "custom_line_fill_color_alpha", params.getFillColorOpacity());
                    dataFether1.setFloatValue("custom_line_attr", "custom_line_border_width", params.getBorderWidth());
                    if (params.getBorderStyle() != null) {
                        dataFether1.setIntValue("custom_line_attr", "custom_line_bs_id", params.getBorderStyle().getStyle().id);
                        dataFether1.setFloatValue("custom_line_attr", "custom_line_bs_borderWidth", params.getBorderStyle().getBorderWidth());
                        float dashs[] = params.getBorderStyle().getDashArr();
                        StringBuilder stringBuilder = new StringBuilder();
                        int len = dashs.length;
                        for (int i = 0; i < len; i++) {
                            if (i != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(dashs[i]);
                        }
                        dataFether1.setString("custom_line_attr", "custom_line_bs_dash", stringBuilder.toString());
                    }
                    break;
                case ANNOT_ARROW:

                    CPDFLineAttr lineAttr1 = attribute.getLineAttr();
                    lineAttr1.setBorderColor(params.getLineColor());
                    lineAttr1.setBorderAlpha(params.getLineColorOpacity());
                    lineAttr1.setFillColor(params.getFillColor());
                    lineAttr1.setFillAlpha(params.getFillColorOpacity());
                    lineAttr1.setBorderWidth(params.getBorderWidth());
                    lineAttr1.setBorderStyle(params.getBorderStyle());
                    lineAttr1.setLineType(params.getStartLineType(), params.getTailLineType());
                    callback = lineAttr1;

                    CPDFAttributeDataFether dataFether = new CPDFAttributeDataFether(readerView.getContext());
                    dataFether.setIntValue("custom_arrow_attr", "custom_arrow_head_type", params.getStartLineType().id);
                    dataFether.setIntValue("custom_arrow_attr", "custom_arrow_tail_type", params.getTailLineType().id);
                    dataFether.setIntValue("custom_arrow_attr", "custom_arrow_border_color", params.getLineColor());
                    dataFether.setIntValue("custom_arrow_attr", "custom_arrow_border_color_alpha", params.getLineColorOpacity());
                    dataFether.setIntValue("custom_arrow_attr", "custom_arrow_fill_color", params.getFillColor());
                    dataFether.setIntValue("custom_arrow_attr", "custom_arrow_fill_color_alpha", params.getFillColorOpacity());
                    dataFether.setFloatValue("custom_arrow_attr", "custom_arrow_border_width", params.getBorderWidth());
                    if (params.getBorderStyle() != null) {
                        dataFether.setIntValue("custom_arrow_attr", "custom_arrow_bs_id", params.getBorderStyle().getStyle().id);
                        dataFether.setFloatValue("custom_arrow_attr", "custom_arrow_bs_borderWidth", params.getBorderStyle().getBorderWidth());
                        float dashs[] = params.getBorderStyle().getDashArr();
                        StringBuilder stringBuilder = new StringBuilder();
                        int len = dashs.length;
                        for (int i = 0; i < len; i++) {
                            if (i != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(dashs[i]);
                        }
                        dataFether.setString("custom_arrow_attr", "custom_arrow_bs_dash", stringBuilder.toString());
                    }
                    break;
                case ANNOT_FREETEXT:
                    CPDFFreetextAttr freetextAttr = attribute.getFreetextAttr();
                    switch (params.getAlignment()) {
                        case LEFT:
                            freetextAttr.setAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_LEFT);
                            break;
                        case CENTER:
                            freetextAttr.setAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_CENTER);
                            break;
                        case RIGHT:
                            freetextAttr.setAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_RIGHT);
                            break;
                        default:
                            freetextAttr.setAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_UNKNOW);
                            break;
                    }
                    freetextAttr.setAlpha(params.getTextColorOpacity());
                    String fontName = CPDFTextAttribute.FontNameHelper.obtainFontName(params.getFontType(), params.isFontBold(), params.isFontItalic());
                    freetextAttr.setTextAttribute(new CPDFTextAttribute(fontName, params.getFontSize(), params.getTextColor()));
                    callback = freetextAttr;
                    break;
                case ANNOT_SIGNATURE:
                    CPDFStampAttr stampAttr = attribute.getStampAttr();
                    stampAttr.setImagePath(params.getImagePath(), true);
                    break;
                case ANNOT_STAMP:
                case ANNOT_PIC:
                    CPDFStampAttr stampAttr1 = attribute.getStampAttr();
                    if (params.getStandardStamp() != null) {
                        stampAttr1.setStandardStamp(params.getStandardStamp());
                    } else if (params.getTextStamp() != null) {
                        stampAttr1.setTextStamp(params.getTextStamp());
                    } else if (!TextUtils.isEmpty(params.getImagePath())) {
                        stampAttr1.setImagePath(params.getImagePath(), false);
                    }
                    break;
                case FORM_TEXT_FIELD:
                    CPDFTextfieldAttr textFieldAttr = attribute.getTextfieldAttr();
                    textFieldAttr.setBorderWidth(params.getBorderWidth());
                    textFieldAttr.setBorderColor(params.getLineColor());
                    textFieldAttr.setFillColor(params.getFillColor());
                    textFieldAttr.setiAttributeUpdateCallback(() -> getDefaultFiledName("Text Field_"));
                    String textFieldFontName = CPDFTextAttribute.FontNameHelper.obtainFontName(params.getFontType(), params.isFontBold(), params.isFontItalic());
                    textFieldAttr.setTextAttribute(new CPDFTextAttribute(textFieldFontName, params.getFontSize(), params.getTextColor()));
                    textFieldAttr.setMultiline(params.isFormMultiLine());
                    switch (params.getAlignment()) {
                        case LEFT:
                            textFieldAttr.setTextAlignment(CPDFTextAlignment.ALIGNMENT_LEFT);
                            break;
                        case CENTER:
                            textFieldAttr.setTextAlignment(CPDFTextAlignment.ALIGNMENT_CENTER);
                            break;
                        case RIGHT:
                            textFieldAttr.setTextAlignment(CPDFTextAlignment.ALIGNMENT_RIGHT);
                            break;
                        default:
                            textFieldAttr.setTextAlignment(CPDFTextAlignment.ALIGNMENT_UNKNOWN);
                            break;
                    }
                    callback = textFieldAttr;
                    break;
                case FORM_CHECK_BOX:
                    CPDFCheckboxAttr checkboxAttr = attribute.getCheckboxAttr();
                    checkboxAttr.setCheckStyle(params.getCheckStyle());
                    checkboxAttr.setBorderWidth(params.getBorderWidth());
                    checkboxAttr.setColor(params.getColor());
                    checkboxAttr.setBorderColor(params.getLineColor());
                    checkboxAttr.setFillColor(params.getFillColor());
                    checkboxAttr.setChecked(params.isChecked());
                    checkboxAttr.setiAttributeUpdateCallback(() -> getDefaultFiledName("Check Button_"));
                    callback = checkboxAttr;
                    break;
                case FORM_RADIO_BUTTON:
                    CPDFRadiobuttonAttr radiobuttonAttr = attribute.getRadiobuttonAttr();
                    radiobuttonAttr.setCheckStyle(params.getCheckStyle());
                    radiobuttonAttr.setBorderWidth(params.getBorderWidth());
                    radiobuttonAttr.setColor(params.getColor());
                    radiobuttonAttr.setBorderColor(params.getLineColor());
                    radiobuttonAttr.setFillColor(params.getFillColor());
                    radiobuttonAttr.setChecked(params.isChecked());
                    radiobuttonAttr.setiAttributeUpdateCallback(() -> getDefaultFiledName("Radio Button_"));
                    callback = radiobuttonAttr;
                    break;
                case FORM_LIST_BOX:
                    CPDFListboxAttr listBoxAttr = attribute.getListboxAttr();
                    listBoxAttr.setFontSize(params.getFontSize());
                    listBoxAttr.setFontColor(params.getTextColor());
                    listBoxAttr.setFillColor(params.getFillColor());
                    listBoxAttr.setBorderWidth(params.getBorderWidth());
                    listBoxAttr.setBorderColor(params.getLineColor());
                    String listBoxFontName = CPDFTextAttribute.FontNameHelper.obtainFontName(params.getFontType(), params.isFontBold(), params.isFontItalic());
                    listBoxAttr.setFontName(listBoxFontName);
                    listBoxAttr.setiAttributeUpdateCallback(() -> getDefaultFiledName("List Choice_"));
                    callback = listBoxAttr;
                    break;
                case FORM_COMBO_BOX:
                    CPDFComboboxAttr comboBoxAttr = attribute.getComboboxAttr();
                    comboBoxAttr.setFillColor(params.getFillColor());
                    comboBoxAttr.setFontSize(params.getFontSize());
                    comboBoxAttr.setFontColor(params.getTextColor());
                    comboBoxAttr.setBorderWidth(params.getBorderWidth());
                    comboBoxAttr.setBorderColor(params.getLineColor());
                    String comboBoxFontName = CPDFTextAttribute.FontNameHelper.obtainFontName(params.getFontType(), params.isFontBold(), params.isFontItalic());
                    comboBoxAttr.setFontName(comboBoxFontName);
                    comboBoxAttr.setiAttributeUpdateCallback(() -> getDefaultFiledName("Combox Choice_"));
                    callback = comboBoxAttr;
                    break;
                case FORM_PUSH_BUTTON:
                    CPDFPushButtonAttr pushButtonAttr = attribute.getPushButtonAttr();
                    pushButtonAttr.setFillColor(params.getFillColor());
                    pushButtonAttr.setBorderColor(params.getLineColor());
                    pushButtonAttr.setBorderWidth(params.getBorderWidth());
                    pushButtonAttr.setFontColor(params.getTextColor());
                    pushButtonAttr.setFontSize(params.getFontSize());
                    pushButtonAttr.setButtonTitle(params.getFormDefaultValue());
                    String pushButtonFontName = CPDFTextAttribute.FontNameHelper.obtainFontName(params.getFontType(), params.isFontBold(), params.isFontItalic());
                    pushButtonAttr.setFontName(pushButtonFontName);
                    pushButtonAttr.setiAttributeUpdateCallback(() -> getDefaultFiledName("Push Button_"));
                    callback = pushButtonAttr;
                    break;
                case FORM_SIGNATURE_FIELDS:
                    CPDFSignatureWidgetAttr signatureWidgetAttr = attribute.getSignatureWidgetAttr();
                    signatureWidgetAttr.setBorderWidth(params.getBorderWidth());
                    signatureWidgetAttr.setBorderColor(params.getLineColor());
                    signatureWidgetAttr.setBorderStyle(params.getSignFieldsBorderStyle());
                    signatureWidgetAttr.setFillColor(params.getFillColor());
                    signatureWidgetAttr.setiAttributeUpdateCallback(() -> getDefaultFiledName("Signature__"));
                    callback = signatureWidgetAttr;
                    break;
                default:
                    break;
            }
            if (onStore) {
                if (callback != null) {
                    callback.onstore();
                }
            }
        }
    }

    @Override
    public CAnnotStyle getStyle(CStyleType type) {
        CAnnotStyle style = new CAnnotStyle(type);
        CPDFAnnotAttribute attribute = readerView.getReaderAttribute().getAnnotAttribute();
        switch (type) {
            case ANNOT_TEXT:
                CPDFTextAttr textAttr = attribute.getTextAttr();
                style.setColor(textAttr.getColor());
                style.setOpacity(textAttr.getAlpha());
                break;
            case ANNOT_HIGHLIGHT:
                CPDFHighlightAttr highlightAttr = attribute.getHighlightAttr();
                style.setColor(highlightAttr.getColor());
                style.setOpacity(highlightAttr.getAlpha());
                break;
            case ANNOT_UNDERLINE:
                CPDFUnderlineAttr underlineAttr = attribute.getUnderlineAttr();
                style.setColor(underlineAttr.getColor());
                style.setOpacity(underlineAttr.getAlpha());
                break;
            case ANNOT_SQUIGGLY:
                CPDFSquigglyAttr squigglyAttr = attribute.getSquigglyAttr();
                style.setColor(squigglyAttr.getColor());
                style.setOpacity(squigglyAttr.getAlpha());
                break;
            case ANNOT_STRIKEOUT:
                CPDFStrikeoutAttr strikeoutAttr = attribute.getStrikeoutAttr();
                style.setColor(strikeoutAttr.getColor());
                style.setOpacity(strikeoutAttr.getAlpha());
                break;
            case ANNOT_INK:
                CPDFInkAttr inkAttr = attribute.getInkAttr();
                style.setColor(inkAttr.getColor());
                style.setOpacity(inkAttr.getAlpha());
                style.setBorderWidth(inkAttr.getBorderWidth());
                break;
            case ANNOT_SQUARE:
                CPDFSquareAttr squareAttr = attribute.getSquareAttr();
                style.setFillColor(squareAttr.getFillColor());
                style.setFillColorOpacity(squareAttr.getFillAlpha());
                style.setBorderColor(squareAttr.getBorderColor());
                style.setLineColorOpacity(squareAttr.getBorderAlpha());
                style.setBorderWidth(squareAttr.getBorderWidth());
                style.setBorderStyle(squareAttr.getBorderStyle());
                break;
            case ANNOT_CIRCLE:
                CPDFCircleAttr circleAttr = attribute.getCircleAttr();
                style.setFillColor(circleAttr.getFillColor());
                style.setFillColorOpacity(circleAttr.getFillAlpha());
                style.setBorderColor(circleAttr.getBorderColor());
                style.setLineColorOpacity(circleAttr.getBorderAlpha());
                style.setBorderWidth(circleAttr.getBorderWidth());
                style.setBorderStyle(circleAttr.getBorderStyle());
                break;
            case ANNOT_LINE:

                CPDFAttributeDataFether dataFether1 = new CPDFAttributeDataFether(readerView.getContext());
                CPDFLineAnnotation.LineType headType1 = CPDFLineAnnotation.LineType.valueOf(dataFether1.getIntValue("custom_line_attr", "custom_line_head_type", CPDFLineAnnotation.LineType.LINETYPE_NONE.id));
                CPDFLineAnnotation.LineType tailType1 = CPDFLineAnnotation.LineType.valueOf(dataFether1.getIntValue("custom_line_attr", "custom_line_tail_type", CPDFLineAnnotation.LineType.LINETYPE_NONE.id));
                style.setStartLineType(headType1);
                style.setTailLineType(tailType1);

                style.setBorderColor(dataFether1.getIntValue("custom_line_attr", "custom_line_border_color", 0xDD2C00));
                style.setLineColorOpacity(dataFether1.getIntValue("custom_line_attr", "custom_line_border_color_alpha", 255));
                style.setFillColor(dataFether1.getIntValue("custom_line_attr", "custom_line_fill_color", 0xFFFFFF));
                style.setFillColorOpacity(dataFether1.getIntValue("custom_line_attr", "custom_line_fill_color_alpha", 0));
                style.setBorderWidth(dataFether1.getFloatValue("custom_line_attr", "custom_line_border_width", 2));


                int bsId1 = dataFether1.getIntValue("custom_line_attr", "custom_line_bs_id", 0);
                float bsBorderWidth1 = dataFether1.getFloatValue("custom_line_attr", "custom_line_bs_borderWidth", 2);
                CPDFBorderStyle borderStyle1 = new CPDFBorderStyle();
                borderStyle1.setStyle(CPDFBorderStyle.Style.valueOf(bsId1));
                borderStyle1.setBorderWidth(bsBorderWidth1);
                String bs_dash_str1 = dataFether1.getString("custom_line_attr", "custom_line_bs_dash", "12,12");
                try {
                    if (!TextUtils.isEmpty(bs_dash_str1)) {
                        String dash_strs[] = bs_dash_str1.split(",");
                        int len = dash_strs.length;
                        float dash[] = new float[len];
                        for (int i = 0; i < len; i++) {
                            dash[i] = Float.valueOf(dash_strs[i]);
                        }
                        borderStyle1.setDashArr(dash);
                        style.setBorderStyle(borderStyle1);
                    }
                } catch (NumberFormatException e) {

                }


                break;
            case ANNOT_ARROW:
                CPDFAttributeDataFether dataFether = new CPDFAttributeDataFether(readerView.getContext());
                CPDFLineAnnotation.LineType headType = CPDFLineAnnotation.LineType.valueOf(dataFether.getIntValue("custom_arrow_attr", "custom_arrow_head_type", CPDFLineAnnotation.LineType.LINETYPE_NONE.id));
                CPDFLineAnnotation.LineType tailType = CPDFLineAnnotation.LineType.valueOf(dataFether.getIntValue("custom_arrow_attr", "custom_arrow_tail_type", CPDFLineAnnotation.LineType.LINETYPE_ARROW.id));
                style.setStartLineType(headType);
                style.setTailLineType(tailType);

                style.setBorderColor(dataFether.getIntValue("custom_arrow_attr", "custom_arrow_border_color", 0xDD2C00));
                style.setLineColorOpacity(dataFether.getIntValue("custom_arrow_attr", "custom_arrow_border_color_alpha", 255));
                style.setFillColor(dataFether.getIntValue("custom_arrow_attr", "custom_arrow_fill_color", 0xFFFFFF));
                style.setFillColorOpacity(dataFether.getIntValue("custom_arrow_attr", "custom_arrow_fill_color_alpha", 0));
                style.setBorderWidth(dataFether.getFloatValue("custom_arrow_attr", "custom_arrow_border_width", 2));


                int bsId = dataFether.getIntValue("custom_arrow_attr", "custom_arrow_bs_id", 0);
                float bsBorderWidth = dataFether.getFloatValue("custom_arrow_attr", "custom_arrow_bs_borderWidth", 2);
                CPDFBorderStyle borderStyle = new CPDFBorderStyle();
                borderStyle.setStyle(CPDFBorderStyle.Style.valueOf(bsId));
                borderStyle.setBorderWidth(bsBorderWidth);
                String bs_dash_str = dataFether.getString("custom_arrow_attr", "custom_arrow_bs_dash", "12,12");
                try {
                    if (!TextUtils.isEmpty(bs_dash_str)) {
                        String dash_strs[] = bs_dash_str.split(",");
                        int len = dash_strs.length;
                        float dash[] = new float[len];
                        for (int i = 0; i < len; i++) {
                            dash[i] = Float.valueOf(dash_strs[i]);
                        }
                        borderStyle.setDashArr(dash);
                        style.setBorderStyle(borderStyle);
                    }
                } catch (NumberFormatException e) {

                }
                break;
            case ANNOT_FREETEXT:
                CPDFFreetextAttr freetextAttr = attribute.getFreetextAttr();
                switch (freetextAttr.getAlignment()) {
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
                style.setTextColorOpacity(freetextAttr.getAlpha());
                CPDFTextAttribute textAttribute = freetextAttr.getTextAttribute();
                style.setFontColor(textAttribute.getColor());
                style.setFontType(CPDFTextAttribute.FontNameHelper.getFontType(textAttribute.getFontName()));
                style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(textAttribute.getFontName()));
                style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(textAttribute.getFontName()));
                style.setFontSize((int) textAttribute.getFontSize());
                break;
            case FORM_TEXT_FIELD:
                CPDFTextfieldAttr textFieldAttr = attribute.getTextfieldAttr();
                //text fields
                style.setHideForm(false);
                style.setFormMultiLine(textFieldAttr.isMultiline());
                style.setFontColor(textFieldAttr.getFontColor());
                style.setBorderColor(textFieldAttr.getBorderColor());
                style.setFillColor(textFieldAttr.getFillColor());
                CPDFTextAttribute.FontNameHelper.FontType fontType = CPDFTextAttribute.FontNameHelper.getFontType(textFieldAttr.getFontName());
                style.setFontType(fontType);
                style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(textFieldAttr.getFontName()));
                style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(textFieldAttr.getFontName()));
                style.setFontSize((int) textFieldAttr.getFontSize());
                style.setFormDefaultValue("");
                switch (textFieldAttr.getTextAlignment()) {
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
                break;
            case FORM_LIST_BOX:
                CPDFListboxAttr listboxAttr = attribute.getListboxAttr();
                style.setFontSize((int) listboxAttr.getFontSize());
                style.setFontColor(listboxAttr.getFontColor());
                style.setFillColor(listboxAttr.getFillColor());
                style.setBorderColor(listboxAttr.getBorderColor());
                style.setBorderWidth(listboxAttr.getBorderWidth());
                CPDFTextAttribute.FontNameHelper.FontType listBoxFontType = CPDFTextAttribute.FontNameHelper.getFontType(listboxAttr.getFontName());
                style.setFontType(listBoxFontType);
                style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(listboxAttr.getFontName()));
                style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(listboxAttr.getFontName()));
                break;
            case FORM_COMBO_BOX:
                CPDFComboboxAttr comboBoxAttr = attribute.getComboboxAttr();
                style.setFontSize((int) comboBoxAttr.getFontSize());
                style.setFontColor(comboBoxAttr.getFontColor());
                style.setFillColor(comboBoxAttr.getFillColor());
                style.setBorderColor(comboBoxAttr.getBorderColor());
                style.setBorderWidth(comboBoxAttr.getBorderWidth());
                CPDFTextAttribute.FontNameHelper.FontType comBoBoxFontType = CPDFTextAttribute.FontNameHelper.getFontType(comboBoxAttr.getFontName());
                style.setFontType(comBoBoxFontType);
                style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(comboBoxAttr.getFontName()));
                style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(comboBoxAttr.getFontName()));
                break;
            case FORM_PUSH_BUTTON:
                CPDFPushButtonAttr pushButtonAttr = attribute.getPushButtonAttr();
                //text fields
                style.setHideForm(false);
                style.setFontColor(pushButtonAttr.getFontColor());
                style.setBorderColor(pushButtonAttr.getBorderColor());
                style.setFillColor(pushButtonAttr.getFillColor());
                CPDFTextAttribute.FontNameHelper.FontType pushButtonFontType = CPDFTextAttribute.FontNameHelper.getFontType(pushButtonAttr.getFontName());
                style.setFontType(pushButtonFontType);
                style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(pushButtonAttr.getFontName()));
                style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(pushButtonAttr.getFontName()));
                style.setFontSize((int) pushButtonAttr.getFontSize());
                style.setFormDefaultValue(pushButtonAttr.getButtonTitle());
                break;
            default:
                break;
        }
        return style;
    }

    protected String getDefaultFiledName(String widgetType) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateStr = df.format(new Date());
        return String.format("%s%s", widgetType, dateStr);
    }
}
