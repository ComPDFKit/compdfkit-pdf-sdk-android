/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf;


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
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.ui.reader.CPDFReaderView;

public class CPDFApplyConfigUtil {

    private CPDFConfiguration configuration;

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
            }
            readerView.setContinueMode(readerViewConfig.continueMode);
            readerView.setVerticalMode(readerViewConfig.verticalMode);
            readerView.setCropMode(readerViewConfig.cropMode);
            readerView.setPageSameWidth(readerViewConfig.pageSameWidth);
            switch (readerViewConfig.themes) {
                case Dark:
                    readerView.setReadBackgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_dark));
                    break;
                case Sepia:
                    readerView.setReadBackgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_sepia));
                    break;
                case Reseda:
                    readerView.setReadBackgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_reseda));
                    break;
                default:
                    readerView.setReadBackgroundColor(ContextCompat.getColor(fragment.getContext(), R.color.tools_themes_light));
                    break;
            }
            readerView.setPageSpacing(readerViewConfig.pageSpacing);
            readerView.setScale(readerViewConfig.pageScale);
            CPDFDocument document = fragment.pdfView.getCPdfReaderView().getPDFDocument();
            if (document != null){
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
                fragment.showPageEdit(fragment.pdfView, true, () -> {
                    if (fragment.curEditMode > CPDFEditPage.LoadNone && fragment.pdfToolBar.getMode() == CPreviewMode.Edit) {
                        CPDFEditManager editManager = fragment.pdfView.getCPdfReaderView().getEditManager();
                        if (!editManager.isEditMode()) {
                            editManager.beginEdit(fragment.curEditMode);
                        }
                    }
                });
            }
        }
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
        style.setFontType(freetextAttr.getTypeface());
        style.setFontBold(freetextAttr.isBold());
        style.setFontItalic(freetextAttr.isItalic());
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
            builder.setShape(styleType,
                    shapeAttr.getBorderColor(),
                    shapeAttr.getColorAlpha(),
                    shapeAttr.getFillColor(),
                    shapeAttr.getColorAlpha(),
                    shapeAttr.getBorderWidth(),
                    new CPDFBorderStyle(CPDFBorderStyle.Style.valueOf(shapeBorderStyle.borderStyle.id),
                            shapeAttr.getBorderWidth(),
                            new float[]{
                                    shapeBorderStyle.dashWidth,
                                    shapeBorderStyle.dashGap,
                            })
            );
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
        textFieldStyle.setFontBold(textField.isBold());
        textFieldStyle.setFontItalic(textField.isItalic());
        textFieldStyle.setAlignment(textField.getAnnotStyleAlignment());
        textFieldStyle.setFormMultiLine(textField.isMultiline());
        textFieldStyle.setFontType(textField.getTypeface());
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
        listBoxStyle.setFontType(listBoxAttr.getTypeface());
        listBoxStyle.setFontBold(listBoxAttr.isBold());
        listBoxStyle.setFontItalic(listBoxAttr.isItalic());
        builder.setAnnotStyle(listBoxStyle);

        FormsComboBoxAttr comboBoxAttr = initAttribute.comboBox;
        CAnnotStyle comboBoxStyle = new CAnnotStyle(CStyleType.FORM_COMBO_BOX);
        comboBoxStyle.setBorderColor(comboBoxAttr.getBorderColor());
        comboBoxStyle.setFillColor(comboBoxAttr.getFillColor());
        comboBoxStyle.setFontColor(comboBoxAttr.getFontColor());
        comboBoxStyle.setFontSize((int) comboBoxAttr.getFontSize());
        comboBoxStyle.setBorderWidth(comboBoxAttr.getBorderWidth());
        comboBoxStyle.setFontType(comboBoxAttr.getTypeface());
        comboBoxStyle.setFontBold(comboBoxAttr.isBold());
        comboBoxStyle.setFontItalic(comboBoxAttr.isItalic());
        builder.setAnnotStyle(comboBoxStyle);

        FormsPushButtonAttr pushButtonAttr = initAttribute.pushButton;
        CAnnotStyle pushButtonStyle = new CAnnotStyle(CStyleType.FORM_PUSH_BUTTON);
        pushButtonStyle.setFontColor(pushButtonAttr.getFontColor());
        pushButtonStyle.setFontSize((int) pushButtonAttr.getFontSize());
        pushButtonStyle.setFillColor(pushButtonAttr.getFillColor());
        pushButtonStyle.setBorderColor(pushButtonAttr.getBorderColor());
        pushButtonStyle.setBorderWidth(pushButtonAttr.getBorderWidth());
        pushButtonStyle.setFormDefaultValue(pushButtonAttr.getTitle());
        pushButtonStyle.setFontType(pushButtonAttr.getTypeface());
        pushButtonStyle.setFontBold(pushButtonAttr.isBold());
        pushButtonStyle.setFontItalic(pushButtonAttr.isItalic());
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
}