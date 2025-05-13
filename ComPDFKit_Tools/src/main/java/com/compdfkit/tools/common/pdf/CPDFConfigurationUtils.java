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
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.edit.CPDFEditTextArea;
import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.ContentEditorConfig;
import com.compdfkit.tools.common.pdf.config.FormsConfig;
import com.compdfkit.tools.common.pdf.config.GlobalConfig;
import com.compdfkit.tools.common.pdf.config.ModeConfig;
import com.compdfkit.tools.common.pdf.config.ReaderViewConfig;
import com.compdfkit.tools.common.pdf.config.ToolbarConfig;
import com.compdfkit.tools.common.pdf.config.annot.AnnotAttr;
import com.compdfkit.tools.common.pdf.config.annot.AnnotFreetextAttr;
import com.compdfkit.tools.common.pdf.config.annot.AnnotInkAttr;
import com.compdfkit.tools.common.pdf.config.annot.AnnotShapeAttr;
import com.compdfkit.tools.common.pdf.config.annot.AnnotationsAttributes;
import com.compdfkit.tools.common.pdf.config.forms.FormsAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsCheckBoxAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsComboBoxAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsListBoxAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsPushButtonAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsRadioButtonAttr;
import com.compdfkit.tools.common.pdf.config.forms.FormsTextFieldAttr;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CPDFConfigurationUtils {

    public static CPDFConfiguration normalConfig(Context context, String fileName) {
        String json = CFileUtils.readStringFromAssets(context, fileName);
        if (!TextUtils.isEmpty(json)){
            return fromJson(json);
        }
        return null;
    }

    public static CPDFConfiguration fromJson(String json) {
        try {
            CPDFConfiguration configuration = new CPDFConfiguration();
            JSONObject rootJsonObject = new JSONObject(json);
            configuration.modeConfig = parseModeConfig(rootJsonObject.optJSONObject("modeConfig"));
            configuration.toolbarConfig = parseToolbarConfig(rootJsonObject.optJSONObject("toolbarConfig"));
            configuration.annotationsConfig = parseAnnotationsConfig(rootJsonObject.optJSONObject("annotationsConfig"));
            configuration.contentEditorConfig = parseContentEditorConfig(rootJsonObject.optJSONObject("contentEditorConfig"));
            configuration.formsConfig = parseFormsConfig(rootJsonObject.optJSONObject("formsConfig"));
            configuration.readerViewConfig = parseReaderViewConfig(rootJsonObject.optJSONObject("readerViewConfig"));
            configuration.globalConfig = parseGlobalConfig(rootJsonObject.optJSONObject("global"));
            return configuration;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ModeConfig parseModeConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return ModeConfig.normal();
        }
        ModeConfig modeConfig = ModeConfig.normal();
        modeConfig.readerOnly = jsonObject.optBoolean("readerOnly", false);
        CPreviewMode mode = CPreviewMode.fromAlias(jsonObject.optString("initialViewMode"));
        modeConfig.initialViewMode = mode != null ? mode : CPreviewMode.Viewer;
        JSONArray availableViewModes = jsonObject.optJSONArray("availableViewModes");
        if (availableViewModes != null && availableViewModes.length() > 0) {
            modeConfig.availableViewModes = new ArrayList<>();
            for (int i = 0; i < availableViewModes.length(); i++) {
                CPreviewMode items = CPreviewMode.fromAlias(availableViewModes.optString(i, ""));
                if (items != null) {
                    modeConfig.availableViewModes.add(items);
                }
            }
        }else {
            modeConfig.availableViewModes = new ArrayList<>();
            modeConfig.availableViewModes.add(CPreviewMode.Viewer);
        }
        return modeConfig;
    }

    private static ToolbarConfig parseToolbarConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return ToolbarConfig.normal();
        }
        ToolbarConfig toolbarConfig = new ToolbarConfig();
        List<ToolbarConfig.ToolbarAction> androidAvailableActionsList = new ArrayList<>();
        JSONArray androidAvailableActions = jsonObject.optJSONArray("androidAvailableActions");
        if (androidAvailableActions != null) {
            for (int i = 0; i < androidAvailableActions.length(); i++) {
                ToolbarConfig.ToolbarAction action = ToolbarConfig.ToolbarAction.fromString(androidAvailableActions.optString(i));
                if (action != null) {
                    androidAvailableActionsList.add(action);
                }
            }
        }
        toolbarConfig.androidAvailableActions = androidAvailableActionsList;
        // parse more menu actions
        List<ToolbarConfig.MenuAction> menuActionList = new ArrayList<>();
        JSONArray availableMenusArray = jsonObject.optJSONArray("availableMenus");
        if (availableMenusArray != null) {
            for (int i = 0; i < availableMenusArray.length(); i++) {
                ToolbarConfig.MenuAction menuAction = ToolbarConfig.MenuAction.fromString(availableMenusArray.optString(i));
                if (menuAction != null) {
                    menuActionList.add(menuAction);
                }
            }
        }
        toolbarConfig.availableMenus = menuActionList;
        toolbarConfig.mainToolbarVisible = jsonObject.optBoolean("mainToolbarVisible", true);
        return toolbarConfig;
    }

    private static ReaderViewConfig parseReaderViewConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return new ReaderViewConfig();
        }
        ReaderViewConfig readerViewConfig = new ReaderViewConfig();
        readerViewConfig.linkHighlight = jsonObject.optBoolean("linkHighlight", true);
        readerViewConfig.formFieldHighlight = jsonObject.optBoolean("formFieldHighlight", true);
        switch (jsonObject.optString("displayMode", "singlePage").toLowerCase()) {
            case "doublepage":
                readerViewConfig.displayMode = ReaderViewConfig.DisplayMode.DoublePage;
                break;
            case "coverpage":
                readerViewConfig.displayMode = ReaderViewConfig.DisplayMode.CoverPage;
                break;
            default:
                readerViewConfig.displayMode = ReaderViewConfig.DisplayMode.SinglePage;
                break;
        }
        readerViewConfig.verticalMode = jsonObject.optBoolean("verticalMode", true);
        readerViewConfig.continueMode = jsonObject.optBoolean("continueMode", true);
        readerViewConfig.cropMode = jsonObject.optBoolean("cropMode", false);
        switch (jsonObject.optString("themes", "light").toLowerCase()){
            case "light":
                readerViewConfig.themes = ReaderViewConfig.Themes.Light;
                break;
            case "dark":
                readerViewConfig.themes = ReaderViewConfig.Themes.Dark;
                break;
            case "sepia":
                readerViewConfig.themes = ReaderViewConfig.Themes.Sepia;
                break;
            case "reseda":
                readerViewConfig.themes = ReaderViewConfig.Themes.Reseda;
                break;
            default:break;
        }
        readerViewConfig.enableSliderBar = jsonObject.optBoolean("enableSliderBar", true);
        readerViewConfig.enablePageIndicator = jsonObject.optBoolean("enablePageIndicator", true);
        readerViewConfig.pageSpacing = jsonObject.optInt("pageSpacing", 10);
        readerViewConfig.pageScale = Math.max((float) jsonObject.optDouble("pageScale", 1.0), 1.0F);
        readerViewConfig.pageSameWidth = jsonObject.optBoolean("pageSameWidth", true);
        JSONArray marginsJsonArray = jsonObject.optJSONArray("margins");
        if (marginsJsonArray != null && marginsJsonArray.length() == 4) {
            int left = marginsJsonArray.optInt(0, 0);
            int top = marginsJsonArray.optInt(1, 0);
            int right = marginsJsonArray.optInt(2, 0);
            int bottom = marginsJsonArray.optInt(3, 0);
            readerViewConfig.margins = new ArrayList<>(Arrays.asList(left, top, right, bottom));
        }
        return readerViewConfig;
    }

    private static AnnotationsConfig parseAnnotationsConfig(@Nullable JSONObject jsonObject) {
        AnnotationsConfig annotationsConfig = new AnnotationsConfig();
        if (jsonObject == null){
            return annotationsConfig;
        }
        annotationsConfig.annotationAuthor = jsonObject.optString("annotationAuthor", "");
        List<CAnnotationType> annotationTypes = new ArrayList<>();
        JSONArray availableAnnotTypes = jsonObject.optJSONArray("availableTypes");
        if (availableAnnotTypes != null) {
            for (int i = 0; i < availableAnnotTypes.length(); i++) {
                String key = availableAnnotTypes.optString(i);
                switch (key.toLowerCase()) {
                    case "note":
                        annotationTypes.add(CAnnotationType.TEXT);
                        break;
                    case "highlight":
                        annotationTypes.add(CAnnotationType.HIGHLIGHT);
                        break;
                    case "underline":
                        annotationTypes.add(CAnnotationType.UNDERLINE);
                        break;
                    case "squiggly":
                        annotationTypes.add(CAnnotationType.SQUIGGLY);
                        break;
                    case "strikeout":
                        annotationTypes.add(CAnnotationType.STRIKEOUT);
                        break;
                    case "ink":
                        annotationTypes.add(CAnnotationType.INK);
                        break;
                    case "ink_eraser":
                        annotationTypes.add(CAnnotationType.INK_ERASER);
                        break;
                    case "square":
                        annotationTypes.add(CAnnotationType.SQUARE);
                        break;
                    case "circle":
                        annotationTypes.add(CAnnotationType.CIRCLE);
                        break;
                    case "line":
                        annotationTypes.add(CAnnotationType.LINE);
                        break;
                    case "arrow":
                        annotationTypes.add(CAnnotationType.ARROW);
                        break;
                    case "freetext":
                        annotationTypes.add(CAnnotationType.FREETEXT);
                        break;
                    case "signature":
                        annotationTypes.add(CAnnotationType.SIGNATURE);
                        break;
                    case "stamp":
                        annotationTypes.add(CAnnotationType.STAMP);
                        break;
                    case "pictures":
                        annotationTypes.add(CAnnotationType.PIC);
                        break;
                    case "link":
                        annotationTypes.add(CAnnotationType.LINK);
                        break;
                    case "sound":
                        annotationTypes.add(CAnnotationType.SOUND);
                        break;
                    default:
                        break;
                }
            }
        }
        annotationsConfig.availableTypes = annotationTypes;

        List<AnnotationsConfig.AnnotationTools> annotationTools = new ArrayList<>();
        JSONArray availableTools = jsonObject.optJSONArray("availableTools");
        if (availableTools != null) {
            for (int i = 0; i < availableTools.length(); i++) {
                AnnotationsConfig.AnnotationTools tool = AnnotationsConfig.AnnotationTools.fromString(availableTools.optString(i));
                if (tool != null) {
                    annotationTools.add(tool);
                }
            }
        }
        annotationsConfig.availableTools = annotationTools;
        JSONObject initAttrJsonObject = jsonObject.optJSONObject("initAttribute");
        AnnotationsAttributes annotationsAttributes = new AnnotationsAttributes();
        if (initAttrJsonObject != null) {
            Iterator<String> attrsNames = initAttrJsonObject.keys();
            while (attrsNames.hasNext()) {
                String key = attrsNames.next();
                JSONObject attrJson = initAttrJsonObject.optJSONObject(key);
                switch (key.toLowerCase()) {
                    case "note":
                        annotationsAttributes.note = getAnnotAttrItems(CAnnotationType.TEXT, attrJson);
                        break;
                    case "highlight":
                        annotationsAttributes.highlight = getAnnotAttrItems(CAnnotationType.HIGHLIGHT, attrJson);
                        break;
                    case "underline":
                        annotationsAttributes.underline = getAnnotAttrItems(CAnnotationType.UNDERLINE, attrJson);
                        break;
                    case "squiggly":
                        annotationsAttributes.squiggly = getAnnotAttrItems(CAnnotationType.SQUIGGLY, attrJson);
                        break;
                    case "strikeout":
                        annotationsAttributes.strikeout = getAnnotAttrItems(CAnnotationType.STRIKEOUT, attrJson);
                        break;
                    case "ink":
                        annotationsAttributes.ink = (AnnotInkAttr) getAnnotAttrItems(CAnnotationType.INK, attrJson);
                        break;
                    case "square":
                        annotationsAttributes.square = (AnnotShapeAttr) getAnnotAttrItems(CAnnotationType.SQUARE, attrJson);
                        break;
                    case "circle":
                        annotationsAttributes.circle = (AnnotShapeAttr) getAnnotAttrItems(CAnnotationType.CIRCLE, attrJson);
                        break;
                    case "line":
                        annotationsAttributes.line = (AnnotShapeAttr) getAnnotAttrItems(CAnnotationType.LINE, attrJson);
                        break;
                    case "arrow":
                        annotationsAttributes.arrow = (AnnotShapeAttr) getAnnotAttrItems(CAnnotationType.ARROW, attrJson);
                        break;
                    case "freetext":
                        annotationsAttributes.freeText = (AnnotFreetextAttr) getAnnotAttrItems(CAnnotationType.FREETEXT, attrJson);
                        break;
                    default:
                        break;
                }
            }
        }
        annotationsConfig.initAttribute = annotationsAttributes;
        return annotationsConfig;
    }

    private static AnnotAttr getAnnotAttrItems(CAnnotationType annotationType, JSONObject annotJsonObject) {
        AnnotAttr annotAttr = null;
        switch (annotationType) {
            case TEXT:
            case HIGHLIGHT:
            case UNDERLINE:
            case SQUIGGLY:
            case STRIKEOUT:
                annotAttr = new AnnotAttr();
                annotAttr.setColor(annotJsonObject.optString("color", "#000000"));
                annotAttr.setAlpha(annotJsonObject.optInt("alpha", 255));
                break;
            case INK:
                AnnotInkAttr inkAttr = new AnnotInkAttr();
                inkAttr.setColor(annotJsonObject.optString("color", "#000000"));
                inkAttr.setAlpha(annotJsonObject.optInt("alpha", 255));
                inkAttr.setBorderWidth((float) annotJsonObject.optDouble("borderWidth", 10));
                inkAttr.setEraserWidth((float) annotJsonObject.optDouble("borderWidth", 10));
                annotAttr = inkAttr;
                break;
            case SQUARE:
            case CIRCLE:
                AnnotShapeAttr shapeAttr = new AnnotShapeAttr();
                shapeAttr.setFillColor(annotJsonObject.optString("fillColor", "#000000"));
                shapeAttr.setBorderColor(annotJsonObject.optString("borderColor", "#000000"));
                shapeAttr.setColorAlpha(annotJsonObject.optInt("colorAlpha", 255));
                shapeAttr.setBorderWidth((float) annotJsonObject.optDouble("borderWidth", 10));
                JSONObject borderStyleJsonObject = annotJsonObject.optJSONObject("borderStyle");
                if (borderStyleJsonObject != null) {
                    AnnotShapeAttr.ShapeBorderStyle shapeBorderStyle = new AnnotShapeAttr.ShapeBorderStyle();
                    String style = borderStyleJsonObject.optString("style");
                    AnnotShapeAttr.ShapeBorderStyle.StyleType styleType = AnnotShapeAttr.ShapeBorderStyle.StyleType.fromString(style);
                    if (styleType != null) {
                        shapeBorderStyle.borderStyle = styleType;
                    }
                    shapeBorderStyle.dashGap = (float) borderStyleJsonObject.optDouble("dashGap", 0.0);
                    shapeAttr.setBorderStyle(shapeBorderStyle);
                }
                String bordEffectType = annotJsonObject.optString("bordEffectType", "solid");
                if ("solid".equals(bordEffectType)){
                    shapeAttr.setBorderEffectType(CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeSolid);
                }else {
                    shapeAttr.setBorderEffectType(CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeCloudy);
                }
                String startLineType = annotJsonObject.optString("startLineType", AnnotShapeAttr.AnnotLineType.None.name());
                shapeAttr.setStartLineType(AnnotShapeAttr.AnnotLineType.fromString(startLineType));
                String tailLineType = annotJsonObject.optString("tailLineType", AnnotShapeAttr.AnnotLineType.None.name());
                shapeAttr.setTailLineType(AnnotShapeAttr.AnnotLineType.fromString(tailLineType));
                annotAttr = shapeAttr;
                break;

            case LINE:
            case ARROW:
                AnnotShapeAttr lineAttr = new AnnotShapeAttr();
                lineAttr.setFillColor(annotJsonObject.optString("borderColor", "#000000"));
                lineAttr.setBorderColor(annotJsonObject.optString("borderColor", "#000000"));
                lineAttr.setBorderAlpha(annotJsonObject.optInt("borderAlpha", 255));
                lineAttr.setBorderWidth((float) annotJsonObject.optDouble("borderWidth", 10));
                JSONObject lineStyleJsonObject = annotJsonObject.optJSONObject("borderStyle");
                if (lineStyleJsonObject != null) {
                    AnnotShapeAttr.ShapeBorderStyle shapeBorderStyle = new AnnotShapeAttr.ShapeBorderStyle();
                    String style = lineStyleJsonObject.optString("style");
                    AnnotShapeAttr.ShapeBorderStyle.StyleType styleType = AnnotShapeAttr.ShapeBorderStyle.StyleType.fromString(style);
                    if (styleType != null) {
                        shapeBorderStyle.borderStyle = styleType;
                    }
                    shapeBorderStyle.dashGap = (float) lineStyleJsonObject.optDouble("dashGap", 0.0);
                    lineAttr.setBorderStyle(shapeBorderStyle);
                }
                String startLineType1 = annotJsonObject.optString("startLineType", AnnotShapeAttr.AnnotLineType.None.name());
                lineAttr.setStartLineType(AnnotShapeAttr.AnnotLineType.fromString(startLineType1));
                String tailLineType1 = annotJsonObject.optString("tailLineType", AnnotShapeAttr.AnnotLineType.None.name());
                lineAttr.setTailLineType(AnnotShapeAttr.AnnotLineType.fromString(tailLineType1));
                annotAttr = lineAttr;
                break;
            case FREETEXT:
                AnnotFreetextAttr freetextAttr = new AnnotFreetextAttr();
                freetextAttr.setFontColor(annotJsonObject.optString("fontColor", "#000000"));
                freetextAttr.setFontColorAlpha(annotJsonObject.optInt("fontColorAlpha", 255));
                freetextAttr.setFontSize(annotJsonObject.optInt("fontSize", 20));
                String familyName = annotJsonObject.optString("typeface", "Helvetica");
                boolean bold = annotJsonObject.optBoolean("isBold", false);
                boolean italic = annotJsonObject.optBoolean("isItalic", false);

                freetextAttr.setAlignment(AnnotFreetextAttr.Alignment.fromString(annotJsonObject.optString("alignment", AnnotFreetextAttr.Alignment.LEFT.name())));
                freetextAttr.setPsName(getFontPsName(familyName, bold, italic));
                annotAttr = freetextAttr;
                break;
            default:
                break;
        }
        return annotAttr;
    }


    private static ContentEditorConfig parseContentEditorConfig(@Nullable JSONObject jsonObject) {
        ContentEditorConfig contentEditorConfig = ContentEditorConfig.normal();
        if (jsonObject == null) {
            return contentEditorConfig;
        }
        List<ContentEditorConfig.ContentEditorType> editorTypes = new ArrayList<>();
        JSONArray availableEditorTypes = jsonObject.optJSONArray("availableTypes");
        if (availableEditorTypes != null) {
            for (int i = 0; i < availableEditorTypes.length(); i++) {
                ContentEditorConfig.ContentEditorType action = ContentEditorConfig.ContentEditorType.fromString(availableEditorTypes.optString(i));
                if (action != null) {
                    editorTypes.add(action);
                }
            }
        }
        contentEditorConfig.availableTypes = editorTypes;

        List<AnnotationsConfig.AnnotationTools> editorTools = new ArrayList<>();
        JSONArray availableTools = jsonObject.optJSONArray("availableTools");
        if (availableTools != null) {
            for (int i = 0; i < availableTools.length(); i++) {
                AnnotationsConfig.AnnotationTools tool = AnnotationsConfig.AnnotationTools.fromString(availableTools.optString(i));
                if (tool != null) {
                    editorTools.add(tool);
                }
            }
        }
        contentEditorConfig.availableTools = editorTools;

        JSONObject initAttributeJsonObj = jsonObject.optJSONObject("initAttribute");
        if (initAttributeJsonObj != null){
            JSONObject textAttrJsonObj = initAttributeJsonObj.optJSONObject("text");
            if (textAttrJsonObj != null) {
                ContentEditorConfig.TextAttr textAttr = new ContentEditorConfig.TextAttr();
                textAttr.setFontColor(textAttrJsonObj.optString("fontColor", "#000000"));
                textAttr.setFontColorAlpha(textAttrJsonObj.optInt("fontColorAlpha", 255));
                textAttr.setFontSize(textAttrJsonObj.optInt("fontSize", 20));
                textAttr.setBold(textAttrJsonObj.optBoolean("isBold", false));
                textAttr.setItalic(textAttrJsonObj.optBoolean("isItalic", false));
                textAttr.setTypeface(getFontType(textAttrJsonObj.optString("typeface", "helvetica")));
                switch (textAttrJsonObj.optString("alignment", "left")){
                    case "center":
                        textAttr.setAlignment(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignMiddle);
                        break;
                    case "right":
                        textAttr.setAlignment(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignRight);
                        break;
                    default:
                        textAttr.setAlignment(CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);
                        break;
                }
                ContentEditorConfig.ContentEditorAttr attr = new ContentEditorConfig.ContentEditorAttr();
                attr.text = textAttr;
                contentEditorConfig.initAttribute = attr;
            }
        }
        return contentEditorConfig;
    }


    private static FormsConfig parseFormsConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return FormsConfig.normal();
        }
        FormsConfig formsConfig = new FormsConfig();

        List<CPDFWidget.WidgetType> widgetsTypes = new ArrayList<>();
        JSONArray availableFormsTypes = jsonObject.optJSONArray("availableTypes");
        if (availableFormsTypes != null) {
            for (int i = 0; i < availableFormsTypes.length(); i++) {
                CPDFWidget.WidgetType widgetsType = getWidgetType(availableFormsTypes.optString(i));
                if (widgetsType != null) {
                    widgetsTypes.add(widgetsType);
                }
            }
        }
        formsConfig.availableTypes = widgetsTypes;

        List<FormsConfig.FormsTools> annotationTools = new ArrayList<>();
        JSONArray availableTools = jsonObject.optJSONArray("availableTools");
        if (availableTools != null) {
            for (int i = 0; i < availableTools.length(); i++) {
                FormsConfig.FormsTools tool = FormsConfig.FormsTools.fromString(availableTools.optString(i));
                if (tool != null) {
                    annotationTools.add(tool);
                }
            }
        }
        formsConfig.availableTools = annotationTools;

        JSONObject initAttrJsonObj = jsonObject.optJSONObject("initAttribute");
        if (initAttrJsonObj != null) {
            Iterator<String> keys = initAttrJsonObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                CPDFWidget.WidgetType widgetType = getWidgetType(key);
                JSONObject attrJsonObj = initAttrJsonObj.optJSONObject(key);
                if (widgetType == null || attrJsonObj == null) {
                    continue;
                }
                FormsAttr formsAttr = getFormsAttr(widgetType, attrJsonObj);
                switch (widgetType) {
                    case Widget_TextField:
                        formsConfig.initAttribute.textField = (FormsTextFieldAttr) formsAttr;
                        break;
                    case Widget_CheckBox:
                        formsConfig.initAttribute.checkBox = (FormsCheckBoxAttr) formsAttr;
                        break;
                    case Widget_RadioButton:
                        formsConfig.initAttribute.radioButton = (FormsRadioButtonAttr) formsAttr;
                        break;
                    case Widget_ListBox:
                        formsConfig.initAttribute.listBox = (FormsListBoxAttr) formsAttr;
                        break;
                    case Widget_ComboBox:
                        formsConfig.initAttribute.comboBox = (FormsComboBoxAttr) formsAttr;
                        break;
                    case Widget_PushButton:
                        formsConfig.initAttribute.pushButton = (FormsPushButtonAttr) formsAttr;
                        break;
                    case Widget_SignatureFields:
                        formsConfig.initAttribute.signatureFields = formsAttr;
                        break;
                    default:
                        break;
                }
            }
        }
        return formsConfig;
    }

    private static CPDFWidget.WidgetType getWidgetType(String key){
        switch (key.toLowerCase()){
            case "textfield":
                return CPDFWidget.WidgetType.Widget_TextField;
            case "checkbox":
                return CPDFWidget.WidgetType.Widget_CheckBox;
            case "radiobutton":
                return CPDFWidget.WidgetType.Widget_RadioButton;
            case "listbox":
                return CPDFWidget.WidgetType.Widget_ListBox;
            case "combobox":
                return CPDFWidget.WidgetType.Widget_ComboBox;
            case "signaturesfields":
                return CPDFWidget.WidgetType.Widget_SignatureFields;
            case "pushbutton":
                return CPDFWidget.WidgetType.Widget_PushButton;
            default:
                return CPDFWidget.WidgetType.Widget_Unknown;
        }
    }

    private static FormsAttr getFormsAttr(CPDFWidget.WidgetType widgetType, JSONObject jsonObject) {
        FormsAttr formsAttr = null;
        switch (widgetType) {
            case Widget_TextField:
                FormsTextFieldAttr textFieldAttr = new FormsTextFieldAttr();
                textFieldAttr.setFillColor(jsonObject.optString("fillColor", "#000000"));
                textFieldAttr.setBorderColor(jsonObject.optString("borderColor", "#FFFFFF"));
                textFieldAttr.setBorderWidth((float) jsonObject.optDouble("borderWidth", 5));
                textFieldAttr.setFontColor(jsonObject.optString("fontColor", "#000000"));
                textFieldAttr.setFontSize(jsonObject.optInt("fontSize", 20));
                boolean isBold = jsonObject.optBoolean("isBold", false);
                boolean isItalic = jsonObject.optBoolean("isItalic", false);
                String familyName = jsonObject.optString("typeface", "Helvetica");
                textFieldAttr.setPsName(getFontPsName(familyName, isBold, isItalic));
                textFieldAttr.setAlignment(AnnotFreetextAttr.Alignment.fromString(jsonObject.optString("alignment", AnnotFreetextAttr.Alignment.LEFT.name())));
                textFieldAttr.setMultiline(jsonObject.optBoolean("multiline", true));
                formsAttr = textFieldAttr;
                break;
            case Widget_CheckBox:
                FormsCheckBoxAttr checkBoxAttr = new FormsCheckBoxAttr();
                checkBoxAttr.setFillColor(jsonObject.optString("fillColor", "#000000"));
                checkBoxAttr.setBorderColor(jsonObject.optString("borderColor", "#FFFFFF"));
                checkBoxAttr.setBorderWidth((float) jsonObject.optDouble("borderWidth", 5));
                checkBoxAttr.setCheckedColor(jsonObject.optString("checkedColor", "#000000"));
                checkBoxAttr.isChecked = jsonObject.optBoolean("isChecked", false);
                checkBoxAttr.checkedStyle = getCheckStyle(jsonObject.optString("checkedStyle", FormsCheckBoxAttr.CheckedStyle.Check.name()));
                formsAttr = checkBoxAttr;
                break;
            case Widget_RadioButton:
                FormsRadioButtonAttr radioButtonAttr = new FormsRadioButtonAttr();
                radioButtonAttr.setFillColor(jsonObject.optString("fillColor", "#000000"));
                radioButtonAttr.setBorderColor(jsonObject.optString("borderColor", "#FFFFFF"));
                radioButtonAttr.setBorderWidth((float) jsonObject.optDouble("borderWidth", 5));
                radioButtonAttr.setCheckedColor(jsonObject.optString("checkedColor", "#000000"));
                radioButtonAttr.isChecked = jsonObject.optBoolean("isChecked", false);
                radioButtonAttr.checkedStyle = getCheckStyle(jsonObject.optString("checkedStyle", FormsCheckBoxAttr.CheckedStyle.Check.name()));;
                formsAttr = radioButtonAttr;
                break;
            case Widget_ListBox:
                FormsListBoxAttr formsListBoxAttr = new FormsListBoxAttr();
                formsListBoxAttr.setFillColor(jsonObject.optString("fillColor", "#000000"));
                formsListBoxAttr.setBorderColor(jsonObject.optString("borderColor", "#FFFFFF"));
                formsListBoxAttr.setBorderWidth((float) jsonObject.optDouble("borderWidth", 5));
                formsListBoxAttr.setFontColor(jsonObject.optString("fontColor", "#000000"));
                formsListBoxAttr.setFontSize(jsonObject.optInt("fontSize", 20));
                boolean listBoxAttrIsBold = jsonObject.optBoolean("isBold", false);
                boolean listBoxAttrIsItalic = jsonObject.optBoolean("isItalic", false);
                String listBoxAttrFamilyName = jsonObject.optString("typeface", "Helvetica");
                formsListBoxAttr.setPsName(getFontPsName(listBoxAttrFamilyName, listBoxAttrIsBold, listBoxAttrIsItalic));
                formsAttr = formsListBoxAttr;
                break;
            case Widget_ComboBox:
                FormsComboBoxAttr comboBoxAttr = new FormsComboBoxAttr();
                comboBoxAttr.setFillColor(jsonObject.optString("fillColor", "#000000"));
                comboBoxAttr.setBorderColor(jsonObject.optString("borderColor", "#FFFFFF"));
                comboBoxAttr.setBorderWidth((float) jsonObject.optDouble("borderWidth", 5));
                comboBoxAttr.setFontColor(jsonObject.optString("fontColor", "#000000"));
                comboBoxAttr.setFontSize(jsonObject.optInt("fontSize", 20));
                boolean comboBoxAttrIsBold = jsonObject.optBoolean("isBold", false);
                boolean comboBoxAttrIsItalic = jsonObject.optBoolean("isItalic", false);
                String comboBoxAttrFamilyName = jsonObject.optString("typeface", "Helvetica");
                comboBoxAttr.setPsName(getFontPsName(comboBoxAttrFamilyName, comboBoxAttrIsBold, comboBoxAttrIsItalic));
                formsAttr = comboBoxAttr;
                break;
            case Widget_PushButton:
                FormsPushButtonAttr pushButtonAttr = new FormsPushButtonAttr();
                pushButtonAttr.setFillColor(jsonObject.optString("fillColor", "#000000"));
                pushButtonAttr.setBorderColor(jsonObject.optString("borderColor", "#FFFFFF"));
                pushButtonAttr.setBorderWidth((float) jsonObject.optDouble("borderWidth", 5));
                pushButtonAttr.setFontColor(jsonObject.optString("fontColor", "#000000"));
                pushButtonAttr.setFontSize(jsonObject.optInt("fontSize", 20));
                pushButtonAttr.setTitle(jsonObject.optString("title", ""));
                boolean pushButtonAttrIsBold = jsonObject.optBoolean("isBold", false);
                boolean pushButtonAttrIsItalic = jsonObject.optBoolean("isItalic", false);
                String pushButtonAttrFamilyName = jsonObject.optString("typeface", "Helvetica");
                pushButtonAttr.setPsName(getFontPsName(pushButtonAttrFamilyName, pushButtonAttrIsBold, pushButtonAttrIsItalic));
                formsAttr = pushButtonAttr;
                break;
            case Widget_SignatureFields:
                FormsAttr signFieldAttr = new FormsAttr();
                signFieldAttr.setFillColor(jsonObject.optString("fillColor", "#000000"));
                signFieldAttr.setBorderColor(jsonObject.optString("borderColor", "#FFFFFF"));
                signFieldAttr.setBorderWidth((float) jsonObject.optDouble("borderWidth", 5));
                formsAttr = signFieldAttr;
                break;
            default:
                break;
        }
        return formsAttr;
    }

    private static CPDFTextAttribute.FontNameHelper.FontType getFontType(String key){
        switch (key.toLowerCase()) {
            case "courier":
                return CPDFTextAttribute.FontNameHelper.FontType.Courier;
            case "times-roman":
                return CPDFTextAttribute.FontNameHelper.FontType.Times_Roman;
            default:
                return CPDFTextAttribute.FontNameHelper.FontType.Helvetica;
        }
    }

    private static String getFontPsName(String familyName, boolean bold, boolean italic){
        switch (familyName){
            case "Helvetica":
            case "Courier":
                String styleName = "";
                if (bold && !italic){
                    styleName = "Bold";
                } else if (!bold && italic) {
                    styleName = "Oblique";
                } else if (bold && italic) {
                    styleName = "BoldOblique";
                }
                String psName = familyName +"-"+styleName;
                if (TextUtils.isEmpty(styleName)){
                    psName = familyName;
                }
                CLog.e("CPDFConfig", "psName：" + psName);
                return psName;
            case "Times-Roman":
                String styleName1 = "Roman";
                if (bold && !italic){
                    styleName1 = "Bold";
                } else if (!bold && italic) {
                    styleName1 = "Italic";
                } else if (bold && italic) {
                    styleName1 = "BoldItalic";
                }
                String psName1 = "Times-"+styleName1;
                CLog.e("CPDFConfig", "psName：" + psName1);
                return psName1;
            default:break;
        }
        return "Helvetica";
    }


    private static CPDFWidget.CheckStyle getCheckStyle(String key){
        switch(key.toLowerCase()){
            case "check":
                return CPDFWidget.CheckStyle.CK_Check;
            case "circle":
                return CPDFWidget.CheckStyle.CK_Circle;
            case "cross":
                return CPDFWidget.CheckStyle.CK_Cross;
            case "diamond":
                return CPDFWidget.CheckStyle.CK_Diamond;
            case "square":
                return CPDFWidget.CheckStyle.CK_Square;
            case "star":
                return CPDFWidget.CheckStyle.CK_Star;
            default:
                return CPDFWidget.CheckStyle.CK_Check;
        }
    }

    private static GlobalConfig parseGlobalConfig(@Nullable JSONObject jsonObject) {
        GlobalConfig globalConfig = new GlobalConfig();
        if (jsonObject == null){
            return globalConfig;
        }
        globalConfig.themeMode = GlobalConfig.CThemeMode.fromString(jsonObject.optString("themeMode", "light"));
        globalConfig.fileSaveExtraFontSubset = jsonObject.optBoolean("fileSaveExtraFontSubset", true);
        globalConfig.enableExitSaveTips = jsonObject.optBoolean("enableExitSaveTips", true);
        JSONObject watermark = jsonObject.optJSONObject("watermark");
        if (watermark != null){
            globalConfig.watermark.saveAsNewFile = watermark.optBoolean("saveAsNewFile",true);
            globalConfig.watermark.outsideBackgroundColor = watermark.optString("outsideBackgroundColor", "");
        }

        globalConfig.signatureType = GlobalConfig.CSignatureType.fromString(jsonObject.optString("signatureType", "manual"));
        return globalConfig;
    }
}
