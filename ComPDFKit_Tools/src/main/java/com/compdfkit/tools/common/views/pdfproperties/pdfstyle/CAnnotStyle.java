/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CAnnotStyle implements Serializable {
    private CStyleType type;

    private EditUpdatePropertyType updatePropertyType = EditUpdatePropertyType.All;

    private int color = Color.BLACK;

    private int opacity = 255;

    private float borderWidth = 10;

    private float eraserWidth = 10;

    private int lineColor = Color.BLACK;

    private int lineColorOpacity = 255;

    private int fillColor = Color.BLACK;

    private int fillColorOpacity = 255;


    private CPDFBorderStyle borderStyle = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10, new float[]{8.0F, 0F});

    private CPDFLineAnnotation.LineType startLineType = CPDFLineAnnotation.LineType.LINETYPE_NONE;

    private CPDFLineAnnotation.LineType tailLineType = CPDFLineAnnotation.LineType.LINETYPE_NONE;

    private boolean fontBold;

    private boolean fontItalic;

    private int textColor = Color.BLACK;

    private int textColorOpacity = 255;

    private int fontSize;
    private String externFontName = "";
    private Alignment alignment = Alignment.UNKNOWN;

    private String imagePath;

    private Uri imageUri;

    private CPDFStampAnnotation.StandardStamp standardStamp;

    private CPDFStampAnnotation.TextStamp textStamp;

    private float rotationAngle;

    private Mirror mirror;

    private String formFieldName;

    private String formDefaultValue;

    private boolean hideForm;

    private boolean formMultiLine;

    private boolean isChecked;

    private Map<String, Object> customExtraMap = new HashMap<>();

    private CPDFWidget.CheckStyle checkStyle = CPDFWidget.CheckStyle.CK_Check;

    private CPDFWidget.BorderStyle signFieldsBorderStyle = CPDFWidget.BorderStyle.BS_Solid;

    private CPDFAnnotation.CPDFBorderEffectType bordEffectType = CPDFAnnotation.CPDFBorderEffectType.CPDFBorderEffectTypeSolid;

    private boolean editTextUnderLine = false;

    private boolean editTextStrikeThrough = false;

    private transient Bitmap editImageBitmap;

    private List<OnAnnotStyleChangeListener> styleChangeListenerList = new ArrayList<>();

    public CAnnotStyle(CStyleType type) {
        this.type = type;
    }

    public void setType(CStyleType type) {
        this.type = type;
    }

    public CStyleType getType() {
        return type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        boolean update = color != this.color;
        this.color = color;
        CLog.e("CAnnotStyle", "setColor(" + color + ", update:" + update + ")");
        updateColorListener(this.color, update);
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(@IntRange(from = 0, to = 255) int opacity) {
        boolean update = opacity != this.opacity;
        this.opacity = opacity;
        CLog.e("CAnnotStyle", "setOpacity(" + opacity + ", update:" + update + ")");
        updateOpacity(this.opacity, update);
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(@FloatRange(from = 0.0) float borderWidth) {
        boolean update = borderWidth != this.borderWidth;
        this.borderWidth = borderWidth;
        if (borderStyle != null) {
            borderStyle.setBorderWidth(this.borderWidth);
        }
        CLog.e("CAnnotStyle", "setBorderWidth(" + borderWidth + ", update:" + update + ")");
        updateBorderWidth(this.borderWidth, update);
    }

    public void setEraserWidth(float eraserWidth) {
        this.eraserWidth = eraserWidth;
    }

    public float getEraserWidth() {
        return eraserWidth;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setBorderColor(@ColorInt int lineColor) {
        boolean update = lineColor != this.lineColor;
        this.lineColor = lineColor;
        CLog.e("CAnnotStyle", "setLineColor(" + lineColor + ", update:" + update + ")");
        updateLineColor(lineColor, update);
    }

    public int getLineColorOpacity() {
        return lineColorOpacity;
    }

    public void setLineColorOpacity(@IntRange(from = 0, to = 255) int lineColorOpacity) {
        boolean update = lineColorOpacity != this.lineColorOpacity;
        this.lineColorOpacity = lineColorOpacity;
        CLog.e("CAnnotStyle", "setLineColorOpacity(" + lineColorOpacity + ", update:" + update + ")");
        updateLineColorOpacity(lineColorOpacity, update);
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(@ColorInt int fillColor) {
        boolean update = fillColor != this.fillColor;
        this.fillColor = fillColor;
        CLog.e("CAnnotStyle", "setFillColor(" + fillColor + ", update:" + update + ")");
        updateFillColor(fillColor, update);
    }


    public int getFillColorOpacity() {
        return fillColorOpacity;
    }

    public void setFillColorOpacity(@IntRange(from = 0, to = 255) int fillColorOpacity) {
        boolean update = fillColorOpacity != this.fillColorOpacity;
        this.fillColorOpacity = fillColorOpacity;
        CLog.e("CAnnotStyle", "setFillColorOpacity(" + fillColor + ", update:" + update + ")");
        updateFillColorOpacity(fillColorOpacity, update);
    }

    public void setBorderStyle(CPDFBorderStyle borderStyle) {
        this.borderStyle = borderStyle;
        CLog.e("CAnnotStyle", "setBorderStyle(" + borderStyle + ", update:" + true + ")");
        updateBorderStyle(borderStyle, true);
    }

    public CPDFBorderStyle getBorderStyle() {
        return borderStyle;
    }

    public CPDFLineAnnotation.LineType getStartLineType() {
        return startLineType;
    }

    public void setStartLineType(CPDFLineAnnotation.LineType startLineType) {
        boolean update = startLineType != this.startLineType;
        this.startLineType = startLineType;
        CLog.e("CAnnotStyle", "setStartLineType(" + startLineType + ", update:" + update + ")");
        updateStartLineType(this.startLineType, update);
    }

    public CPDFLineAnnotation.LineType getTailLineType() {
        return tailLineType;
    }

    public void setTailLineType(CPDFLineAnnotation.LineType tailLineType) {
        boolean update = tailLineType != this.tailLineType;
        this.tailLineType = tailLineType;
        CLog.e("CAnnotStyle", "setTailLineType(" + tailLineType + ", update:" + update + ")");
        updateTailLineType(this.tailLineType, update);
    }

    public boolean isFontBold() {
        return fontBold;
    }

    public void setFontBold(boolean fontBold) {
        boolean update = fontBold != this.fontBold;
        this.fontBold = fontBold;
        CLog.e("CAnnotStyle", "setFontBold(" + fontBold + ", update:" + update + ")");
        updateFontBold(this.fontBold, update);
    }

    public void setBold(boolean isBold){
        this.fontBold = isBold;
    }

    public void setItalic(boolean isItalic){
        this.fontItalic = isItalic;
    }

    public boolean isFontItalic() {
        return fontItalic;
    }

    public void setFontItalic(boolean fontItalic) {
        boolean update = fontItalic != this.fontItalic;
        this.fontItalic = fontItalic;
        CLog.e("CAnnotStyle", "setFontItalic(" + fontItalic + ", update:" + update + ")");
        updateFontItalic(this.fontItalic, update);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setFontColor(@ColorInt int textColor) {
        boolean update = textColor != this.textColor;
        this.textColor = textColor;
        CLog.e("CAnnotStyle", "setTextColor(" + textColor + ", update:" + update + ")");
        updateTextColor(textColor, update);
    }

    public int getTextColorOpacity() {
        return textColorOpacity;
    }

    public void setTextColorOpacity(@IntRange(from = 0, to = 255) int textColorOpacity) {
        boolean update = textColorOpacity != this.textColorOpacity;
        this.textColorOpacity = textColorOpacity;
        CLog.e("CAnnotStyle", "setTextColorOpacity(" + textColorOpacity + ", update:" + update + ")");
        updateTextColorOpacity(textColorOpacity, update);
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(@IntRange(from = 0) int fontSize) {
        boolean update = fontSize != this.fontSize;
        this.fontSize = fontSize;
        CLog.e("CAnnotStyle", "setFontSize(" + fontSize + ", update:" + update + ")");
        updateFontSize(fontSize, update);
    }


    public String getExternFontName() {
        return externFontName;
    }

    public void setExternFontName(String name) {
        boolean update = !externFontName.equals(name);
        externFontName = name;
        if (TextUtils.isEmpty(externFontName)) {
            externFontName = "";
        } else {
            updateExternFontType(externFontName, update);
        }
    }

    public void setAlignment(Alignment alignment) {
        boolean update = alignment != this.alignment;
        this.alignment = alignment;
        CLog.e("CAnnotStyle", "setAlignment(" + alignment + ", update:" + update + ")");
        updateTextAlignment(alignment, update);
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        CLog.e("CAnnotStyle", "setImagePath(" + imagePath + ", update:" + true + ")");
        updateImage(imagePath, null);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        CLog.e("CAnnotStyle", "setImageUri("+ imageUri.toString() + ", update : true)");
        updateImage(null, imageUri);
    }

    public void setStandardStamp(CPDFStampAnnotation.StandardStamp standardStamp) {
        this.standardStamp = standardStamp;
        CLog.e("CAnnotStyle", "setStandardStamp(" + standardStamp + ", update:" + true + ")");
        updateStandardStamp(standardStamp);
    }

    public CPDFStampAnnotation.StandardStamp getStandardStamp() {
        return standardStamp;
    }

    public void setTextStamp(CPDFStampAnnotation.TextStamp textStamp) {
        this.textStamp = textStamp;
        CLog.e("CAnnotStyle", "setTextStamp(" + textStamp + ", update:" + true + ")");
        updateTextStamp(textStamp);
    }

    public CPDFStampAnnotation.TextStamp getTextStamp() {
        return textStamp;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
        CLog.e("CAnnotStyle", "setRotationAngle(" + rotationAngle + ", update:" + true + ")");
        setUpdatePropertyType(EditUpdatePropertyType.Rotation);
        updateRotationAngle(rotationAngle);
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setMirror(Mirror mirror) {
        this.mirror = mirror;
        CLog.e("CAnnotStyle", "setMirror(" + mirror + ", update:" + true + ")");
        setUpdatePropertyType(EditUpdatePropertyType.Mirror);
        updateMirror(mirror);
    }

    public Mirror getMirror() {
        return mirror;
    }

    public void setFormDefaultValue(String formDefaultValue) {
        boolean update = !formDefaultValue.equals(this.formDefaultValue);
        this.formDefaultValue = formDefaultValue;
        CLog.e("CAnnotStyle", "setFormDefaultValue(" + formDefaultValue + ", update:" + update + ")");
        updateDefaultValue(formDefaultValue, update);
    }

    public String getFormDefaultValue() {
        return formDefaultValue;
    }

    public void setFormFieldName(String formFieldName) {
        boolean update = !formFieldName.equals(this.formFieldName);
        this.formFieldName = formFieldName;
        CLog.e("CAnnotStyle", "setFormFieldName(" + formFieldName + ", update:" + update + ")");
        updateFieldName(formFieldName, update);
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public void setHideForm(boolean hideForm) {
        boolean update = hideForm != this.hideForm;
        this.hideForm = hideForm;
        CLog.e("CAnnotStyle", "setHideForm(" + hideForm + ", update:" + update + ")");
        updateHideForm(this.hideForm, update);
    }

    public boolean isHideForm() {
        return hideForm;
    }

    public void setFormMultiLine(boolean formMultiLine) {
        boolean update = formMultiLine != this.formMultiLine;
        this.formMultiLine = formMultiLine;
        CLog.e("CAnnotStyle", "setFormMultiLine(" + formMultiLine + ", update:" + update + ")");
        updateMultiLine(this.formMultiLine, update);
    }

    public boolean isFormMultiLine() {
        return formMultiLine;
    }

    public void setCheckStyle(CPDFWidget.CheckStyle checkStyle) {
        this.checkStyle = checkStyle;
        CLog.e("CAnnotStyle", "setCheckStyle(" + checkStyle + ", update:" + true + ")");
        updateCheckStyle(checkStyle, true);
    }

    public CPDFWidget.CheckStyle getCheckStyle() {
        return checkStyle;
    }

    public void setEditTextUnderLine(boolean editTextUnderLine) {
        this.editTextUnderLine = editTextUnderLine;
        setUpdatePropertyType(EditUpdatePropertyType.UnderLine);
        updateEditTextUnderLine(editTextUnderLine);
        setUpdatePropertyType(EditUpdatePropertyType.All);

    }

    public boolean isEditTextUnderLine() {
        return editTextUnderLine;
    }

    public void setEditTextStrikeThrough(boolean editTextStrikeThrough) {
        this.editTextStrikeThrough = editTextStrikeThrough;
        setUpdatePropertyType(EditUpdatePropertyType.StrikeThrough);
        updateEditTextStrikeThrough(editTextStrikeThrough);
        setUpdatePropertyType(EditUpdatePropertyType.All);
    }

    public boolean isEditTextStrikeThrough() {
        return editTextStrikeThrough;
    }

    public void setChecked(boolean checked) {
        boolean update = checked != this.isChecked;
        isChecked = checked;
        CLog.e("CAnnotStyle", "setChecked(" + isChecked + ", update:" + update + ")");
        updateIsChecked(isChecked, update);
    }

    public void setCustomExtraMap(Map<String, Object> customExtraMap) {
        this.customExtraMap = customExtraMap;
        CLog.e("CAnnotStyle", "setCustomExtraMap(" + customExtraMap.toString() + ")");
        updateExtraMap(this.customExtraMap);
    }

    public Map<String, Object> getCustomExtraMap() {
        return customExtraMap;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setSignFieldsBorderStyle(CPDFWidget.BorderStyle signFieldsBorderStyle) {
        this.signFieldsBorderStyle = signFieldsBorderStyle;
        CLog.e("CAnnotStyle", "setSignFieldsBorderStyle(" + signFieldsBorderStyle + ", update:" + true + ")");
        updateSignFieldsBorderStyle(signFieldsBorderStyle);
    }

    public CPDFWidget.BorderStyle getSignFieldsBorderStyle() {
        return signFieldsBorderStyle;
    }

    public CPDFAnnotation.CPDFBorderEffectType getBordEffectType() {
        return bordEffectType;
    }

    public void setBordEffectType(CPDFAnnotation.CPDFBorderEffectType bordEffectType) {
        boolean update = this.bordEffectType != bordEffectType;
        this.bordEffectType = bordEffectType;
        updateShapeBordEffectType(bordEffectType, update);
    }

    public Bitmap getEditImageBitmap() {
        return editImageBitmap;
    }

    public void setEditImageBitmap(Bitmap editImageBitmap) {
        this.editImageBitmap = editImageBitmap;
    }

    @NonNull
    @Override
    public String toString() {
        return "CAnnotStyle{" +
                "type:" + type + "," +
                "color" + color + "," +
                "colorOpacity" + opacity + "," +
                "borderWidth" + borderWidth + "," +
                "lineColor" + lineColor + "," +
                "lineColorOpacity" + lineColorOpacity + "," +
                "fillColor" + fillColor + "," +
                "fillColorOpacity" + fillColorOpacity + "," +
                "borderStyle" + (borderStyle != null ? borderStyle.toString() : "empty") + "," +
                "startLineType" + (startLineType != null ? startLineType.name() : "empty") + "," +
                "endLineType" + (tailLineType != null ? tailLineType.name() : "empty") + "," +
                "fontBold" + fontBold + "," +
                "fontItalic" + fontItalic + "," +
                "textColor" + textColor + "," +
                "textColorOpacity" + textColorOpacity + "," +
                "fontSize" + fontSize + "," +
                "imagePath" + imagePath + "," +
                "imageUri" + (imageUri != null ? imageUri.toString() : "empty")+ "," +
                "standardStamp" + (standardStamp != null ? standardStamp.name() : "empty") + "," +
                "textStamp" + (textStamp != null ? textStamp.toString() : "empty") + "," +
                "rotationAngle" + rotationAngle + "," +
                "mirror" + (mirror != null ? mirror.name() : "empty") + "," +
                "formFieldName" + formFieldName + "," +
                "formDefaultValue" + formDefaultValue + "," +
                "hideForm" + hideForm + "," +
                "formMultiLine" + formMultiLine + "," +
                "customExtraMap" + customExtraMap.toString() + "," +
                "signFieldsBorderStyle" + (signFieldsBorderStyle != null ? signFieldsBorderStyle.name() : "empty") + "," +
                "bordEffectType" + bordEffectType + "," +
                "editTextUnderLine" + editTextUnderLine + "," +
                "editTextStrikeThrough" + editTextStrikeThrough+ "}";
    }

    @Override
    public int hashCode() {
        int result = this.borderWidth != 0.0F ? Float.floatToIntBits(this.borderWidth) : 0;
        result = 31 * result + this.color;
        result = 31 * result + (this.opacity != 0.0F ? Float.floatToIntBits(this.opacity) : 0);
        result = 31 * result + this.lineColor;
        result = 31 * result + (this.lineColorOpacity != 0.0F ? Float.floatToIntBits(this.lineColorOpacity) : 0);
        result = 31 * result + this.fillColor;
        result = 31 * result + (this.fillColorOpacity != 0.0F ? Float.floatToIntBits(this.fillColorOpacity) : 0);
        result = 31 * result + (this.borderStyle != null ? this.borderStyle.hashCode() : 0);
        result = 31 * result + (this.startLineType != null ? this.startLineType.hashCode() : 0);
        result = 31 * result + (this.tailLineType != null ? this.tailLineType.hashCode() : 0);
        result = 31 * result + this.textColor;
        result = 31 * result + (this.textColorOpacity != 0.0F ? Float.floatToIntBits(this.textColorOpacity) : 0);
        result = 31 * result + (this.fontSize != 0.0F ? Float.floatToIntBits(this.fontSize) : 0);
        result = 31 * result + (this.imagePath != null ? this.imagePath.hashCode() : 0);
        result = 31 * result + (this.imageUri != null ? this.imageUri.hashCode() : 0);
        result = 31 * result + (this.type != null ? type.hashCode() : 0);
        result = 31 * result + (this.standardStamp != null ? this.standardStamp.hashCode() : 0);
        result = 31 * result + (this.textStamp != null ? this.textStamp.hashCode() : 0);
        result = 31 * result + (this.rotationAngle != 0.0F ? Float.floatToIntBits(rotationAngle) : 0);
        result = 31 * result + (this.mirror != null ? this.mirror.hashCode() : 0);
        result = 31 * result + (this.formFieldName != null ? this.formFieldName.hashCode() : 0);
        result = 31 * result + (this.formDefaultValue != null ? this.formDefaultValue.hashCode() : 0);
        result = 31 * result + (this.customExtraMap != null ? this.customExtraMap.hashCode() : 0);
        result = 31 * result + (this.signFieldsBorderStyle != null ? this.signFieldsBorderStyle.hashCode() : 0);
        result = 31 * result + (this.bordEffectType != null ? this.bordEffectType.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CAnnotStyle) {
            return obj.toString().equals(this.toString());
        } else {
            return false;
        }
    }

    public int getAnnotTypeTitleResId() {
        int titleResId = 0;
        switch (type) {
            case ANNOT_TEXT:
                titleResId = R.string.tools_annot_note;
                break;
            case ANNOT_HIGHLIGHT:
                titleResId = R.string.tools_annot_highlight;
                break;
            case ANNOT_STRIKEOUT:
                titleResId = R.string.tools_annot_strikeout;
                break;
            case ANNOT_UNDERLINE:
                titleResId = R.string.tools_annot_underline;
                break;
            case ANNOT_SQUIGGLY:
                titleResId = R.string.tools_annot_squiggly;
                break;
            case ANNOT_INK:
                titleResId = R.string.tools_annot_ink;
                break;
            case ANNOT_SQUARE:
                titleResId = R.string.tools_annot_rectangle;
                break;
            case ANNOT_CIRCLE:
                titleResId = R.string.tools_annot_circle;
                break;
            case ANNOT_LINE:
                titleResId = R.string.tools_annot_line;
                break;
            case ANNOT_ARROW:
                titleResId = R.string.tools_annot_arrow;
                break;
            case ANNOT_FREETEXT:
                titleResId = R.string.tools_annot_freetext;
                break;
            case ANNOT_SIGNATURE:
            case FORM_SIGNATURE_FIELDS:
                titleResId = R.string.tools_annot_signature;
                break;
            case ANNOT_STAMP:
                titleResId = R.string.tools_annot_stamp;
                break;
            case ANNOT_LINK:
                titleResId = R.string.tools_link_to;
                break;
            case EDIT_TEXT:
                titleResId = R.string.tools_edit_text_property_title;
                break;
            case EDIT_IMAGE:
                titleResId = R.string.tools_edit_image_property_title;
                break;
            case FORM_TEXT_FIELD:
                titleResId = R.string.tools_text_field;
                break;
            case FORM_CHECK_BOX:
                titleResId = R.string.tools_check_box;
                break;
            case FORM_RADIO_BUTTON:
                titleResId = R.string.tools_check_radio_button;
                break;
            case FORM_LIST_BOX:
                titleResId = R.string.tools_check_list_box;
                break;
            case FORM_COMBO_BOX:
                titleResId = R.string.tools_combo_button;
                break;
            case FORM_PUSH_BUTTON:
                titleResId = R.string.tools_push_button;
                break;
            case WATERMARK_TEXT:
            case WATERMARK_IMAGE:
                titleResId = R.string.tools_watermark_settings;
                break;
            default:
                titleResId = 0;
                break;
        }
        return titleResId;
    }

    public boolean stampIsAvailable(){
        if (type == CStyleType.ANNOT_STAMP){
            return textStamp != null
                    || standardStamp != null
                    || !TextUtils.isEmpty(getImagePath());
        }else {
            return false;
        }
    }

    private void updateColorListener(@ColorInt int color, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeColor(color);
            }
        }
    }

    private void updateOpacity(int colorOpacity, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeOpacity(colorOpacity);
            }
        }
    }

    private void updateBorderWidth(float borderWidth, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeBorderWidth(borderWidth);
            }
        }
    }

    private void updateLineColor(int color, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeLineColor(color);
            }
        }
    }

    private void updateLineColorOpacity(int opacity, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeLineColorOpacity(opacity);
            }
        }
    }

    private void updateFillColor(int color, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeFillColor(color);
            }
        }
    }

    private void updateFillColorOpacity(int opacity, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeFillColorOpacity(opacity);
            }
        }
    }

    private void updateBorderStyle(CPDFBorderStyle style, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeBorderStyle(style);
            }
        }
    }

    private void updateStartLineType(CPDFLineAnnotation.LineType lineType, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeStartLineType(lineType);
            }
        }
    }

    private void updateTailLineType(CPDFLineAnnotation.LineType lineType, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeTailLineType(lineType);
            }
        }
    }

    private void updateFontBold(boolean fontBold, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeFontBold(fontBold);
            }
        }
    }

    private void updateFontItalic(boolean fontItalic, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeFontItalic(fontItalic);
            }
        }
    }

    private void updateTextColor(int textColor, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeTextColor(textColor);
            }
        }
    }

    private void updateTextColorOpacity(int textColorOpacity, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeTextColorOpacity(textColorOpacity);
            }
        }
    }

    private void updateFontSize(int fontSize, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeFontSize(fontSize);
            }
        }
    }

    private void updateExternFontType(String fontName, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeAnnotExternFontType(fontName);
            }
        }
    }

    private void updateTextAlignment(CAnnotStyle.Alignment alignment, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeTextAlignment(alignment);
            }
        }
    }

    private void updateImage(String imagePath, Uri uri) {
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeImagePath(imagePath, uri);
            }
        }
    }

    private void updateStandardStamp(CPDFStampAnnotation.StandardStamp standardStamp) {
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeStandardStamp(standardStamp);
            }
        }
    }

    private void updateTextStamp(CPDFStampAnnotation.TextStamp textStamp) {
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeTextStamp(textStamp);
            }
        }
    }

    private void updateRotationAngle(float rotationAngle) {
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeRotation(rotationAngle);
            }
        }
    }

    private void updateMirror(Mirror mirror) {
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeMirror(mirror);
            }
        }
    }

    private void updateEditArea(EditUpdatePropertyType type, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeEditArea(type);
            }
        }
    }

    private void updateFieldName(String fieldName, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeFieldName(fieldName);
            }
        }
    }

    private void updateDefaultValue(String defaultValue, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeTextFieldDefaultValue(defaultValue);
            }
        }
    }

    private void updateHideForm(boolean hideForm, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeHideForm(hideForm);
            }
        }
    }

    private void updateMultiLine(boolean multiLine, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeMultiLine(multiLine);
            }
        }
    }

    private void updateCheckStyle(CPDFWidget.CheckStyle checkStyle, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeCheckStyle(checkStyle);
            }
        }
    }

    private void updateIsChecked(boolean isChecked, boolean update) {
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeIsChecked(isChecked);
            }
        }
    }

    private void updateExtraMap(Map<String, Object> extraMap){
        if (styleChangeListenerList != null ){
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeExtraMap(extraMap);
            }
        }
    }

    private void updateSignFieldsBorderStyle(CPDFWidget.BorderStyle borderStyle){
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeSignFieldsBorderStyle(borderStyle);
            }
        }
    }

    private void updateShapeBordEffectType(CPDFAnnotation.CPDFBorderEffectType bordEffectType, boolean update){
        if (styleChangeListenerList != null && update) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeShapeBordEffectType(bordEffectType);
            }
        }
    }

    private void updateEditTextUnderLine(boolean addUnderLine) {
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeEditTextUnderline(addUnderLine);
            }
        }
    }

    private void updateEditTextStrikeThrough(boolean addStrikeThrough) {
        if (styleChangeListenerList != null) {
            for (OnAnnotStyleChangeListener onAnnotStyleChangeListener : styleChangeListenerList) {
                onAnnotStyleChangeListener.onChangeEditTextStrikeThrough(addStrikeThrough);
            }
        }
    }

    public void addStyleChangeListener(OnAnnotStyleChangeListener styleChangeListener) {
        this.styleChangeListenerList.add(styleChangeListener);
    }

    public void addStyleChangeListener(List<OnAnnotStyleChangeListener> styleChangeListener) {
        this.styleChangeListenerList.addAll(styleChangeListener);
    }

    public void removeStyleChangeListener(OnAnnotStyleChangeListener styleChangeListener) {
        this.styleChangeListenerList.remove(styleChangeListener);
    }

    public void cleanStyleChangeListener(){
        if (this.styleChangeListenerList != null) {
            styleChangeListenerList.clear();
        }
    }


    public interface OnAnnotStyleChangeListener {

        void onChangeColor(@ColorInt int color);

        void onChangeOpacity(int opacity);

        void onChangeBorderWidth(float borderWidth);

        void onChangeLineColor(@ColorInt int color);

        void onChangeLineColorOpacity(int opacity);

        void onChangeFillColor(@ColorInt int color);

        void onChangeFillColorOpacity(int opacity);

        void onChangeBorderStyle(CPDFBorderStyle style);

        void onChangeStartLineType(CPDFLineAnnotation.LineType lineType);

        void onChangeTailLineType(CPDFLineAnnotation.LineType lineType);

        void onChangeFontBold(boolean bold);

        void onChangeFontItalic(boolean italic);

        void onChangeTextColor(int textColor);

        void onChangeTextColorOpacity(int textColorOpacity);

        void onChangeFontSize(int fontSize);

        void onChangeTextAlignment(CAnnotStyle.Alignment alignment);

        void onChangeImagePath(String imagePath, Uri imageUri);

        void onChangeAnnotExternFontType(String fontName);

        void onChangeStandardStamp(CPDFStampAnnotation.StandardStamp standardStamp);

        void onChangeTextStamp(CPDFStampAnnotation.TextStamp textStamp);

        void onChangeRotation(float rotation);

        void onChangeMirror(Mirror mirror);

        void onChangeEditArea(EditUpdatePropertyType type);

        void onChangeFieldName(String fieldName);

        void onChangeTextFieldDefaultValue(String defaultValue);

        void onChangeHideForm(boolean hide);

        void onChangeMultiLine(boolean multiLine);

        void onChangeCheckStyle(CPDFWidget.CheckStyle checkStyle);

        void onChangeIsChecked(boolean isChecked);

        void onChangeExtraMap(Map<String, Object> extraMap);

        void onChangeSignFieldsBorderStyle(CPDFWidget.BorderStyle borderStyle);

        void onChangeShapeBordEffectType(CPDFAnnotation.CPDFBorderEffectType type);

        void onChangeEditTextUnderline(boolean addUnderline);

        void onChangeEditTextStrikeThrough(boolean addStrikeThrough);
    }

    public void setUpdatePropertyType(EditUpdatePropertyType type) {
        updatePropertyType = type;
    }

    public void setUpdatePropertyType(EditUpdatePropertyType type, boolean updateListener) {
        updatePropertyType = type;
        updateEditArea(type, updateListener);
    }

    public EditUpdatePropertyType getUpdatePropertyType() {
        return updatePropertyType;
    }

    public enum EditUpdatePropertyType {
        All,
        TextColor,
        TextColorOpacity,
        Bold,
        Italic,
        FontSize,
        Alignment,
        FontType,
        Rotation,
        Mirror,
        ReplaceImage,
        Export,
        Crop,
        UnderLine,
        StrikeThrough
    }

    public enum Mirror{
        Horizontal,

        Vertical
    }

    public enum Alignment {
        UNKNOWN,
        LEFT,
        CENTER,
        RIGHT
    }
}
