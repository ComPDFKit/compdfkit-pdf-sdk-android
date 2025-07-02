/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf;


import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.ContentEditorConfig;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.pdf.config.ModeConfig;
import com.compdfkit.tools.common.pdf.config.ReaderViewConfig;
import com.compdfkit.tools.common.pdf.config.annot.AnnotFreetextAttr;
import com.compdfkit.tools.common.pdf.config.annot.AnnotShapeAttr;
import com.compdfkit.tools.common.pdf.config.annot.AnnotationsAttributes;
import com.compdfkit.tools.common.pdf.config.forms.FormsAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsAttributes;
import com.compdfkit.tools.common.pdf.config.forms.FormsCheckBoxAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsComboBoxAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsListBoxAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsPushButtonAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsRadioButtonAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsTextFieldAttr;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPDFApplyConfigUtil {

    private CPDFConfiguration configuration;

    private int themeId;

    private static CPDFApplyConfigUtil instance;

    public static CPDFApplyConfigUtil getInstance() {
        if (instance == null) {
            instance = new CPDFApplyConfigUtil();
        }
        return instance;
    }

    public CPDFConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CPDFConfiguration configuration) {
        this.configuration = configuration;
    }

    public int getGlobalThemeId() {
        if (themeId == 0){
            themeId = R.style.ComPDFKit_Theme_Light;
        }
        return themeId;
    }

    public void applyConfiguration(CPDFDocumentFragment fragment, CPDFConfiguration configuration) {
        this.configuration = configuration;
        applyReaderViewConfig(fragment, configuration);
        applyModeConfig(fragment, configuration);
        applyAnnotationConfig(fragment, configuration);
        applyContentEditorConfig(fragment, configuration);
        applyFormsConfig(fragment, configuration);
    }

    private void applyReaderViewConfig(CPDFDocumentFragment fragment, CPDFConfiguration configuration) {
        CPDFReaderView readerView = fragment.pdfView.getCPdfReaderView();
        if (configuration.readerViewConfig != null) {
            ReaderViewConfig readerViewConfig = configuration.readerViewConfig;
            readerView.setLinkHighlight(readerViewConfig.linkHighlight);
            readerView.setFormFieldHighlight(readerViewConfig.formFieldHighlight);
            switch (readerViewConfig.displayMode) {
                case SinglePage:
                    readerView.setDoublePageMode(false);
                    readerView.setCoverPageMode(false);
                    break;
                case DoublePage:
                    readerView.setDoublePageMode(true);
                    readerView.setCoverPageMode(false);
                    break;
                case CoverPage:
                    readerView.setDoublePageMode(true);
                    readerView.setCoverPageMode(true);
                    break;
                default:
                    break;
            }
            readerView.setContinueMode(readerViewConfig.continueMode);
            readerView.setVerticalMode(readerViewConfig.verticalMode);
            readerView.setCropMode(readerViewConfig.cropMode);
            readerView.setPageSameWidth(readerViewConfig.pageSameWidth);
            switch (readerViewConfig.themes) {
                case Dark:
                    int darkColor = ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_dark);
                    readerView.setReadBackgroundColor(darkColor);
                    fragment.pdfView.setBackgroundColor(CViewUtils.getColor(darkColor, 190));
                    break;
                case Sepia:
                    int sepiaColor = ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_sepia);
                    readerView.setReadBackgroundColor(sepiaColor);
                    fragment.pdfView.setBackgroundColor(CViewUtils.getColor(sepiaColor, 190));
                    break;
                case Reseda:
                    int resedaColor = ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_reseda);
                    readerView.setReadBackgroundColor(resedaColor);
                    fragment.pdfView.setBackgroundColor(CViewUtils.getColor(resedaColor, 190));
                    break;
                default:
                    readerView.setReadBackgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_light));
                    fragment.pdfView.setBackgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tools_pdf_view_ctrl_background_color));
                    break;
            }
            readerView.setPageSpacing(readerViewConfig.pageSpacing);
            readerView.setScale(readerViewConfig.pageScale);
            ArrayList<Integer> margins = readerViewConfig.margins;
            if (margins != null && margins.size() == 4){
                int left = margins.get(0);
                int top = margins.get(1);
                int right = margins.get(2);
                int bottom = margins.get(3);
                readerView.setReaderViewHorizontalMargin(left, right);
                readerView.setReaderViewTopMargin(top);
                readerView.setReaderViewBottomMargin(bottom);
            }
            CPDFDocument document = fragment.pdfView.getCPdfReaderView().getPDFDocument();
            if (document != null) {
                fragment.pdfView.enableSliderBar(readerViewConfig.enableSliderBar);
                fragment.pdfView.enablePageIndicator(readerViewConfig.enablePageIndicator);
            }
        }
    }

    private void applyModeConfig(CPDFDocumentFragment fragment, CPDFConfiguration configuration) {
        if (configuration.modeConfig != null) {
            ModeConfig modeConfig = configuration.modeConfig;
            fragment.pdfToolBar.addModes(modeConfig.availableViewModes);
            if (modeConfig.initialViewMode != CPreviewMode.PageEdit) {
                if (!modeConfig.availableViewModes.contains(modeConfig.initialViewMode)) {
                    fragment.setPreviewMode(CPreviewMode.Viewer);
                    fragment.editToolBar.setEditMode(true);
                    return;
                }
                fragment.setPreviewMode(modeConfig.initialViewMode);
                if (modeConfig.initialViewMode == CPreviewMode.Edit) {
                    fragment.editToolBar.setEditMode(true);
                }
            } else {
                fragment.setPreviewMode(CPreviewMode.Viewer);
                fragment.showPageEdit(true, true);
            }

        }
    }

    public void appleUiConfig(CPDFDocumentFragment fragment, CPDFConfiguration configuration){
        ModeConfig modeConfig = configuration.modeConfig;
        if (modeConfig.readerOnly) {
            fragment.flTool.setVisibility(View.GONE);
            fragment.flBottomToolBar.setVisibility(View.GONE);
        }
        boolean showMainToolbar = configuration.toolbarConfig.mainToolbarVisible;
        fragment.flTool.setVisibility(showMainToolbar && !modeConfig.readerOnly ? View.VISIBLE : View.GONE);
        boolean showAnnotationToolbar = configuration.toolbarConfig.annotationToolbarVisible;
        fragment.annotationToolbar.setVisibility(showAnnotationToolbar && !modeConfig.readerOnly ? View.VISIBLE : View.GONE);
    }

    private void applyAnnotationConfig(CPDFDocumentFragment fragment, CPDFConfiguration configuration) {
        AnnotationsConfig annotationsConfig = configuration.annotationsConfig;
        AnnotationsAttributes attributes = annotationsConfig.initAttribute;

        //-----  note and markup attr --------------------------------
        CStyleManager.Builder builder = new CStyleManager.Builder()
                .setNote(attributes.note.getColor(), attributes.note.getAlpha())
                .setMarkup(CStyleType.ANNOT_HIGHLIGHT, attributes.highlight.getColor(), attributes.highlight.getAlpha())
                .setMarkup(CStyleType.ANNOT_UNDERLINE, attributes.underline.getColor(), attributes.underline.getAlpha())
                .setMarkup(CStyleType.ANNOT_SQUIGGLY, attributes.squiggly.getColor(), attributes.squiggly.getAlpha())
                .setMarkup(CStyleType.ANNOT_STRIKEOUT, attributes.strikeout.getColor(), attributes.strikeout.getAlpha())
                .setInkAttribute(attributes.ink.getColor(), attributes.ink.getAlpha(),
                        (int) attributes.ink.getBorderWidth(), (int) attributes.ink.getEraserWidth());

        //-----  shape attr --------------------------------
        applyAnnotShapeAttr(CStyleType.ANNOT_SQUARE, attributes.square, builder);
        applyAnnotShapeAttr(CStyleType.ANNOT_CIRCLE, attributes.circle, builder);
        applyAnnotShapeAttr(CStyleType.ANNOT_LINE, attributes.line, builder);
        applyAnnotShapeAttr(CStyleType.ANNOT_ARROW, attributes.arrow, builder);

        //-----  freetext attr --------------------------------
        AnnotFreetextAttr freetextAttr = attributes.freeText;
        CAnnotStyle style = new CAnnotStyle(CStyleType.ANNOT_FREETEXT);
        style.setFontColor(freetextAttr.getFontColor());
        style.setTextColorOpacity(freetextAttr.getFontColorAlpha());
        style.setFontSize(freetextAttr.getFontSize());
        style.setExternFontName(freetextAttr.getPsName());
        style.setAlignment(freetextAttr.getAnnotStyleAlignment());
        builder.setAnnotStyle(style);

        //----- update --------------------------------
        builder.init(fragment.pdfView, true);

        fragment.annotationToolbar.setAnnotationList(annotationsConfig.availableTypes.toArray(new CAnnotationType[0]));
        fragment.annotationToolbar.setTools(annotationsConfig.availableTools);
    }

    private void applyAnnotShapeAttr(CStyleType styleType, AnnotShapeAttr shapeAttr, CStyleManager.Builder builder) {
        AnnotShapeAttr.ShapeBorderStyle shapeBorderStyle = shapeAttr.getBorderStyle();
        if (styleType == CStyleType.ANNOT_ARROW) {
            builder.setShapeArrow(
                    shapeAttr.getBorderColor(),
                    shapeAttr.getBorderAlpha(),
                    shapeAttr.getFillColor(),
                    shapeAttr.getBorderAlpha(),
                    shapeAttr.getBorderWidth(),
                    CPDFLineAnnotation.LineType.valueOf(shapeAttr.getStartLineType().id),
                    CPDFLineAnnotation.LineType.valueOf(shapeAttr.getTailLineType().id),
                    new CPDFBorderStyle(CPDFBorderStyle.Style.valueOf(shapeBorderStyle.borderStyle.id),
                            shapeAttr.getBorderWidth(),
                            new float[]{
                                    shapeBorderStyle.dashWidth,
                                    shapeBorderStyle.dashGap,
                            }
                    ));
        } else if (styleType == CStyleType.ANNOT_LINE) {
            builder.setShape(styleType,
                    shapeAttr.getBorderColor(),
                    shapeAttr.getBorderAlpha(),
                    shapeAttr.getFillColor(),
                    shapeAttr.getBorderAlpha(),
                    shapeAttr.getBorderWidth(),
                    new CPDFBorderStyle(CPDFBorderStyle.Style.valueOf(shapeBorderStyle.borderStyle.id),
                            shapeAttr.getBorderWidth(),
                            new float[]{
                                    shapeBorderStyle.dashWidth,
                                    shapeBorderStyle.dashGap,
                            })
            );
        } else {
            CAnnotStyle shapeAnnotStyle = new CAnnotStyle(styleType);
            shapeAnnotStyle.setBorderColor(shapeAttr.getBorderColor());
            shapeAnnotStyle.setLineColorOpacity(shapeAttr.getColorAlpha());
            shapeAnnotStyle.setFillColor(shapeAttr.getFillColor());
            shapeAnnotStyle.setFillColorOpacity(shapeAttr.getColorAlpha());
            shapeAnnotStyle.setBorderWidth(shapeAttr.getBorderWidth());

            CPDFBorderStyle borderStyle = new CPDFBorderStyle(CPDFBorderStyle.Style.valueOf(shapeBorderStyle.borderStyle.id),
                    shapeAttr.getBorderWidth(),
                    new float[]{
                            shapeBorderStyle.dashWidth,
                            shapeBorderStyle.dashGap,
                    });
            shapeAnnotStyle.setBorderStyle(borderStyle);
            shapeAnnotStyle.setBordEffectType(shapeAttr.getBorderEffectType());
            builder.setAnnotStyle(shapeAnnotStyle);
        }
    }

    private void applyContentEditorConfig(CPDFDocumentFragment fragment, CPDFConfiguration configuration) {
        ContentEditorConfig editorConfig = configuration.contentEditorConfig;
        fragment.editToolBar.setTools(editorConfig.availableTools);
        fragment.editToolBar.setEditType(editorConfig.availableTypes.toArray(new ContentEditorConfig.ContentEditorType[0]));
    }

    private void applyFormsConfig(CPDFDocumentFragment fragment, CPDFConfiguration configuration) {
        fragment.formToolBar.setFormList(configuration.formsConfig.availableTypes.toArray(new CPDFWidget.WidgetType[0]));
        fragment.formToolBar.setTools(configuration.formsConfig.availableTools);

        FormsAttributes initAttribute = configuration.formsConfig.initAttribute;
        FormsRadioButtonAttr radioButtonAttr = initAttribute.radioButton;
        FormsTextFieldAttr textField = initAttribute.textField;

        CStyleManager.Builder builder = new CStyleManager.Builder();

        CAnnotStyle textFieldStyle = new CAnnotStyle(CStyleType.FORM_TEXT_FIELD);
        textFieldStyle.setFillColor(textField.getFillColor());
        textFieldStyle.setBorderColor(textField.getBorderColor());
        textFieldStyle.setBorderWidth(textField.getBorderWidth());
        textFieldStyle.setFontColor(textField.getFontColor());
        textFieldStyle.setFontSize((int) textField.getFontSize());
        textFieldStyle.setAlignment(textField.getAnnotStyleAlignment());
        textFieldStyle.setFormMultiLine(textField.isMultiline());
        textFieldStyle.setExternFontName(textField.getPsName());
        builder.setAnnotStyle(textFieldStyle);

        FormsCheckBoxAttr checkBox = initAttribute.checkBox;
        builder.setCheckBox(checkBox.getBorderColor(), checkBox.getFillColor(),
                        checkBox.getCheckedColor(), (int) checkBox.getBorderWidth(),
                        checkBox.checkedStyle, checkBox.isChecked)
                .setRadioButton(
                        radioButtonAttr.getBorderColor(), radioButtonAttr.getFillColor(),
                        radioButtonAttr.getCheckedColor(), (int) radioButtonAttr.getBorderWidth(),
                        radioButtonAttr.checkedStyle, radioButtonAttr.isChecked
                );

        FormsListBoxAttr listBoxAttr = initAttribute.listBox;
        CAnnotStyle listBoxStyle = new CAnnotStyle(CStyleType.FORM_LIST_BOX);
        listBoxStyle.setBorderColor(listBoxAttr.getBorderColor());
        listBoxStyle.setFillColor(listBoxAttr.getFillColor());
        listBoxStyle.setFontColor(listBoxAttr.getFontColor());
        listBoxStyle.setFontSize((int) listBoxAttr.getFontSize());
        listBoxStyle.setBorderWidth(listBoxAttr.getBorderWidth());
        listBoxStyle.setExternFontName(listBoxAttr.getPsName());

        builder.setAnnotStyle(listBoxStyle);

        FormsComboBoxAttr comboBoxAttr = initAttribute.comboBox;
        CAnnotStyle comboBoxStyle = new CAnnotStyle(CStyleType.FORM_COMBO_BOX);
        comboBoxStyle.setBorderColor(comboBoxAttr.getBorderColor());
        comboBoxStyle.setFillColor(comboBoxAttr.getFillColor());
        comboBoxStyle.setFontColor(comboBoxAttr.getFontColor());
        comboBoxStyle.setFontSize((int) comboBoxAttr.getFontSize());
        comboBoxStyle.setBorderWidth(comboBoxAttr.getBorderWidth());
        comboBoxStyle.setExternFontName(comboBoxAttr.getPsName());

        builder.setAnnotStyle(comboBoxStyle);

        FormsPushButtonAttr pushButtonAttr = initAttribute.pushButton;
        CAnnotStyle pushButtonStyle = new CAnnotStyle(CStyleType.FORM_PUSH_BUTTON);
        pushButtonStyle.setFontColor(pushButtonAttr.getFontColor());
        pushButtonStyle.setFontSize((int) pushButtonAttr.getFontSize());
        pushButtonStyle.setFillColor(pushButtonAttr.getFillColor());
        pushButtonStyle.setBorderColor(pushButtonAttr.getBorderColor());
        pushButtonStyle.setBorderWidth(pushButtonAttr.getBorderWidth());
        pushButtonStyle.setFormDefaultValue(pushButtonAttr.getTitle());
        pushButtonStyle.setExternFontName(pushButtonAttr.getPsName());

        builder.setAnnotStyle(pushButtonStyle);

        FormsAttr signFieldAttr = initAttribute.signatureFields;
        CAnnotStyle signatureFieldsStyle = new CAnnotStyle(CStyleType.FORM_SIGNATURE_FIELDS);
        signatureFieldsStyle.setFillColor(signFieldAttr.getFillColor());
        signatureFieldsStyle.setBorderColor(signFieldAttr.getBorderColor());
        signatureFieldsStyle.setBorderWidth(signFieldAttr.getBorderWidth());
        signatureFieldsStyle.setSignFieldsBorderStyle(CPDFWidget.BorderStyle.BS_Solid);
        builder.setAnnotStyle(signatureFieldsStyle);

        builder.init(fragment.pdfView, true);
    }

    public int getGlobalThemeId(Context context, CPDFConfiguration configuration){
        switch (configuration.globalConfig.themeMode) {
            case Light:
                themeId = R.style.ComPDFKit_Theme_Light;
                break;
            case Dark:
                themeId = R.style.ComPDFKit_Theme_Dark;
                break;
            default:
                if (CViewUtils.isDarkMode(context)) {
                    themeId = R.style.ComPDFKit_Theme_Dark;
                } else {
                    themeId = R.style.ComPDFKit_Theme_Light;
                }
                break;
        }
        return themeId;
    }

    public Map<String, List<ContextMenuConfig.ContextMenuActionItem>> getGlobalContextMenuConfig() {
        if (configuration == null){
            return new HashMap<>();
        }
        ContextMenuConfig contextMenuConfig = configuration.contextMenuConfig;
        return contextMenuConfig.global;
    }

    public Map<String, List<ContextMenuConfig.ContextMenuActionItem>> getViewModeContextMenuConfig() {
        if (configuration == null){
            return new HashMap<>();
        }
        ContextMenuConfig contextMenuConfig = configuration.contextMenuConfig;
        return contextMenuConfig.viewMode;
    }

    public Map<String, List<ContextMenuConfig.ContextMenuActionItem>> getAnnotationModeContextMenuConfig() {
        if (configuration == null){
            return new HashMap<>();
        }
        ContextMenuConfig contextMenuConfig = configuration.contextMenuConfig;
        return contextMenuConfig.annotationMode;
    }

    public Map<String, List<ContextMenuConfig.ContextMenuActionItem>> getContentEditorModeContextMenuConfig() {
        if (configuration == null){
            return new HashMap<>();
        }
        ContextMenuConfig contextMenuConfig = configuration.contextMenuConfig;
        return contextMenuConfig.contentEditorMode;
    }

    public Map<String, List<ContextMenuConfig.ContextMenuActionItem>> getFormsModeContextMenuConfig() {
        if (configuration == null){
            return new HashMap<>();
        }
        ContextMenuConfig contextMenuConfig = configuration.contextMenuConfig;
        return contextMenuConfig.formMode;
    }

    public Map<String, List<ContextMenuConfig.ContextMenuActionItem>> getSignatureModeContextMenuConfig() {
        if (configuration == null){
            return new HashMap<>();
        }
        ContextMenuConfig contextMenuConfig = configuration.contextMenuConfig;
        return contextMenuConfig.signatureMode;
    }


}
