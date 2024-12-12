/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager;

import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CEditSelectionsProvider;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CGlobalStyleProvider;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CSelectedAnnotStyleProvider;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CSelectedFormStyleProvider;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider.CStyleProvider;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.edit.CPDFEditSelections;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.reader.PageView;

import java.util.LinkedHashSet;
import java.util.Map;

public class CStyleManager implements CAnnotStyle.OnAnnotStyleChangeListener {

    private CStyleDialogFragment styleDialogFragment;

    private CStyleProvider styleProxy;

    public CStyleManager(CStyleProvider styleProxy) {
        this.styleProxy = styleProxy;
    }

    public CStyleManager(CPDFViewCtrl pdfView) {
        this.styleProxy = new CGlobalStyleProvider(pdfView);
    }

    public CStyleManager(CPDFReaderView readerView) {
        this.styleProxy = new CGlobalStyleProvider(readerView);
    }

    public CStyleManager(CPDFEditSelections editSelections, CPDFPageView pageView) {
        this.styleProxy = new CEditSelectionsProvider(editSelections, pageView);
    }

    public CStyleManager(CPDFBaseAnnotImpl annotImpl, PageView pageView) {
        if (annotImpl.getAnnotType() == CPDFAnnotation.Type.WIDGET) {
            this.styleProxy = new CSelectedFormStyleProvider(annotImpl, pageView);
        } else {
            this.styleProxy = new CSelectedAnnotStyleProvider(annotImpl, pageView);
        }
    }

    public void updateStyle(CAnnotStyle style) {
        LinkedHashSet<CAnnotStyle> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(style);
        updateStyle(linkedHashSet);
    }

    public void updateStyle(LinkedHashSet<CAnnotStyle> annotStyles) {
        styleProxy.updateStyle(annotStyles);
    }

    public CAnnotStyle getStyle(CStyleType type) {
        return styleProxy.getStyle(type);
    }

    public void setAnnotStyleFragmentListener(CStyleDialogFragment styleDialogFragment) {
        this.styleDialogFragment = styleDialogFragment;
        this.styleDialogFragment.addAnnotStyleChangeListener(this);
    }

    public void setDialogHeightCallback(CStyleDialogFragment styleDialogFragment, CPDFReaderView readerView) {
        styleDialogFragment.setDialogHeightCallback((layoutHeight, isFirst) -> {
            if (isFirst) {
                if (readerView.isContinueMode() && readerView.isVerticalMode()) {
                    try {
                        readerView.setReadViewOffsetY(layoutHeight);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    @Override
    public void onChangeColor(int color) {
        updateAnnot();
    }

    @Override
    public void onChangeOpacity(int opacity) {
        updateAnnot();
    }

    @Override
    public void onChangeBorderWidth(float borderWidth) {
        updateAnnot();
    }

    @Override
    public void onChangeLineColor(int color) {
        updateAnnot();
    }

    @Override
    public void onChangeLineColorOpacity(int opacity) {
        updateAnnot();
    }

    @Override
    public void onChangeFillColor(int color) {
        updateAnnot();
    }

    @Override
    public void onChangeFillColorOpacity(int opacity) {
        updateAnnot();
    }

    @Override
    public void onChangeBorderStyle(CPDFBorderStyle style) {
        updateAnnot();
    }

    @Override
    public void onChangeStartLineType(CPDFLineAnnotation.LineType lineType) {
        updateAnnot();
    }

    @Override
    public void onChangeTailLineType(CPDFLineAnnotation.LineType lineType) {
        updateAnnot();
    }

    @Override
    public void onChangeTextColor(int textColor) {
        updateAnnot();
    }

    @Override
    public void onChangeTextColorOpacity(int textColorOpacity) {
        updateAnnot();
    }

    @Override
    public void onChangeFontBold(boolean bold) {
        updateAnnot();
    }

    @Override
    public void onChangeFontItalic(boolean italic) {
        updateAnnot();
    }

    @Override
    public void onChangeFontSize(int fontSize) {
        updateAnnot();
    }

    @Override
    public void onChangeTextAlignment(CAnnotStyle.Alignment alignment) {
        updateAnnot();
    }

    @Override
    public void onChangeImagePath(String imagePath, Uri imageUri) {
        updateAnnot();
    }

    @Override
    public void onChangeAnnotExternFontType(String fontName) {
        updateAnnot();
    }

    @Override
    public void onChangeStandardStamp(CPDFStampAnnotation.StandardStamp standardStamp) {
        updateAnnot();
    }

    @Override
    public void onChangeTextStamp(CPDFStampAnnotation.TextStamp textStamp) {
        updateAnnot();
    }

    @Override
    public void onChangeMirror(CAnnotStyle.Mirror mirror) {
        updateAnnot();
    }

    @Override
    public void onChangeRotation(float rotation) {
        updateAnnot();
    }

    @Override
    public void onChangeEditArea(CAnnotStyle.EditUpdatePropertyType type) {
        updateAnnot();
    }

    @Override
    public void onChangeFieldName(String fieldName) {
        updateAnnot();
    }

    @Override
    public void onChangeHideForm(boolean hide) {
        updateAnnot();
    }

    @Override
    public void onChangeMultiLine(boolean multiLine) {
        updateAnnot();
    }

    @Override
    public void onChangeTextFieldDefaultValue(String defaultValue) {
        updateAnnot();
    }

    @Override
    public void onChangeCheckStyle(CPDFWidget.CheckStyle checkStyle) {
        updateAnnot();
    }

    @Override
    public void onChangeIsChecked(boolean isChecked) {
        updateAnnot();
    }

    @Override
    public void onChangeExtraMap(Map<String, Object> extraMap) {
        updateAnnot();
    }

    @Override
    public void onChangeSignFieldsBorderStyle(CPDFWidget.BorderStyle borderStyle) {
        updateAnnot();
    }


    @Override
    public void onChangeShapeBordEffectType(CPDFAnnotation.CPDFBorderEffectType type) {
        updateAnnot();
    }

    @Override
    public void onChangeEditTextStrikeThrough(boolean addStrikeThrough) {
        updateAnnot();
    }

    @Override
    public void onChangeEditTextUnderline(boolean addUnderline) {
        updateAnnot();
    }

    private void updateAnnot() {
        if (styleDialogFragment != null) {
            CAnnotStyle style = styleDialogFragment.getAnnotStyle();
            updateStyle(style);
        }
    }

    public static class Builder {
        private LinkedHashSet<CAnnotStyle> attrSet = new LinkedHashSet();

        public Builder() {
        }

        public Builder setNote(@ColorInt int color, @IntRange(from = 0, to = 255) int colorOpacity) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.ANNOT_TEXT);
            style.setColor(color);
            style.setOpacity(colorOpacity);
            attrSet.add(style);
            return this;
        }

        public Builder setMarkup(CStyleType type, @ColorInt int color, @IntRange(from = 0, to = 255) int colorAlpha) {
            CAnnotStyle style = new CAnnotStyle(type);
            style.setColor(color);
            style.setOpacity(colorAlpha);
            attrSet.add(style);
            return this;
        }

        public Builder setInkAttribute(@ColorInt int color,
                                       @IntRange(from = 0, to = 255) int colorAlpha,
                                       @IntRange(from = 0, to = 10) int borderWidth,
                                       @IntRange(from = 0, to = 10) int eraserWidth) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.ANNOT_INK);
            style.setColor(color);
            style.setOpacity(colorAlpha);
            style.setBorderWidth(borderWidth);
            style.setEraserWidth(eraserWidth);
            attrSet.add(style);
            return this;
        }

        public Builder setShape(CStyleType type,
                                @ColorInt int lineColor,
                                @IntRange(from = 0, to = 255) int lineColorOpacity,
                                @ColorInt int fillColor,
                                @IntRange(from = 0, to = 255) int fillColorAlpha,
                                float borderWidth,
                                CPDFBorderStyle borderStyle) {
            CAnnotStyle style = new CAnnotStyle(type);
            style.setBorderColor(lineColor);
            style.setLineColorOpacity(lineColorOpacity);
            style.setFillColor(fillColor);
            style.setFillColorOpacity(fillColorAlpha);
            style.setBorderWidth(borderWidth);
            if (borderStyle == null) {
                borderStyle = new CPDFBorderStyle();
                borderStyle.setBorderWidth(borderWidth);
                borderStyle.setDashArr(new float[]{8.0F, 0F});
            }
            style.setBorderStyle(borderStyle);
            attrSet.add(style);
            return this;
        }

        public Builder setShapeArrow(@ColorInt int lineColor,
                                     @IntRange(from = 0, to = 255) int lineColorOpacity,
                                     @ColorInt int fillColor,
                                     @IntRange(from = 0, to = 255) int fillColorAlpha,
                                     float borderWidth,
                                     CPDFLineAnnotation.LineType startLineType,
                                     CPDFLineAnnotation.LineType tailLineType,
                                     CPDFBorderStyle borderStyle) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.ANNOT_ARROW);
            style.setBorderColor(lineColor);
            style.setLineColorOpacity(lineColorOpacity);
            style.setFillColor(fillColor);
            style.setFillColorOpacity(fillColorAlpha);
            style.setBorderWidth(borderWidth);
            if (startLineType != null) {
                style.setStartLineType(startLineType);
            }
            if (tailLineType != null) {
                style.setTailLineType(tailLineType);
            }
            style.setBorderStyle(borderStyle);
            attrSet.add(style);
            return this;
        }

        public Builder setFreeText(@ColorInt int textColor,
                                   @IntRange(from = 0, to = 255) int textColorOpacity,
                                   @IntRange(from = 0, to = 100) int fontSize) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.ANNOT_FREETEXT);
            style.setFontColor(textColor);
            style.setTextColorOpacity(textColorOpacity);
            style.setFontSize(fontSize);
            attrSet.add(style);
            return this;
        }

        public Builder setTextField(@ColorInt int borderColor,
                                    @ColorInt int fillColor,
                                    @ColorInt int textColor,
                                    @IntRange(from = 0, to = 100) int fontSize,
                                    int borderWidth,
                                    boolean multiLine) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.FORM_TEXT_FIELD);
            style.setBorderColor(borderColor);
            style.setFillColor(fillColor);
            style.setFontColor(textColor);
            style.setBorderWidth(borderWidth);
            style.setFormMultiLine(multiLine);
            style.setFontSize(fontSize);
            attrSet.add(style);
            return this;
        }

        public Builder setCheckBox(@ColorInt int borderColor,
                                   @ColorInt int fillColor,
                                   @ColorInt int checkBoxColor,
                                   int borderWidth,
                                   CPDFWidget.CheckStyle checkStyle,
                                   boolean isChecked) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.FORM_CHECK_BOX);
            style.setBorderColor(borderColor);
            style.setFillColor(fillColor);
            style.setColor(checkBoxColor);
            style.setBorderWidth(borderWidth);
            style.setCheckStyle(checkStyle);
            style.setChecked(isChecked);
            attrSet.add(style);
            return this;
        }

        public Builder setRadioButton(@ColorInt int borderColor,
                                      @ColorInt int fillColor,
                                      @ColorInt int checkBoxColor,
                                      int borderWidth,
                                      CPDFWidget.CheckStyle checkStyle,
                                      boolean isChecked) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.FORM_RADIO_BUTTON);
            style.setBorderColor(borderColor);
            style.setFillColor(fillColor);
            style.setColor(checkBoxColor);
            style.setBorderWidth(borderWidth);
            style.setCheckStyle(checkStyle);
            style.setChecked(isChecked);
            attrSet.add(style);
            return this;
        }

        public Builder setListBox(
                @ColorInt int borderColor,
                @ColorInt int fillColor,
                @ColorInt int textColor,
                @IntRange(from = 1, to = 100) int fontSize,
                @FloatRange(from = 0F, to = 100F) float borderWidth) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.FORM_LIST_BOX);
            style.setFillColor(fillColor);
            style.setBorderColor(borderColor);
            style.setFontColor(textColor);
            style.setFontSize(fontSize);
            style.setBorderWidth(borderWidth);
            attrSet.add(style);
            return this;
        }

        public Builder setComboBox(
                @ColorInt int borderColor,
                @ColorInt int fillColor,
                @ColorInt int textColor,
                @IntRange(from = 1, to = 100) int fontSize,
                @FloatRange(from = 0F, to = 100F) float borderWidth) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.FORM_COMBO_BOX);
            style.setFillColor(fillColor);
            style.setBorderColor(borderColor);
            style.setFontColor(textColor);
            style.setFontSize(fontSize);
            style.setBorderWidth(borderWidth);
            attrSet.add(style);
            return this;
        }

        public Builder setPushButton(
                @ColorInt int borderColor,
                @ColorInt int fillColor,
                @ColorInt int textColor,
                @IntRange(from = 1, to = 100) int fontSize,
                @FloatRange(from = 0F, to = 100F) float borderWidth,
                String buttonTitle) {
            CAnnotStyle style = new CAnnotStyle(CStyleType.FORM_PUSH_BUTTON);
            style.setFillColor(fillColor);
            style.setBorderColor(borderColor);
            style.setFontColor(textColor);
            style.setFontSize(fontSize);
            style.setBorderWidth(borderWidth);
            style.setFormDefaultValue(buttonTitle);
            attrSet.add(style);
            return this;
        }

        public Builder setFormSignature(
                @ColorInt int borderColor,
                @ColorInt int fillColor,
                 int borderWidth
        ){
            CAnnotStyle style = new CAnnotStyle(CStyleType.FORM_SIGNATURE_FIELDS);
            style.setFillColor(fillColor);
            style.setBorderColor(borderColor);
            style.setBorderWidth(borderWidth);
            attrSet.add(style);
            return this;
        }

        public Builder setAnnotStyle(CAnnotStyle annotStyle){
            attrSet.add(annotStyle);
            return this;
        }


        private CStyleManager create(CPDFViewCtrl pdfView, boolean onStore) {
            return new CStyleManager(new CGlobalStyleProvider(pdfView, onStore));
        }

        public void apply(CPDFViewCtrl pdfView) {
            create(pdfView, false).updateStyle(attrSet);
        }

        public void onStore(CPDFViewCtrl pdfView, boolean onStore) {
            CStyleManager styleManager = create(pdfView, onStore);
            styleManager.updateStyle(attrSet);
        }

        public void init(CPDFViewCtrl pdfView, boolean onStore) {
            onStore(pdfView, onStore);
        }
    }
}
