/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;

public class CTypeUtil {

    public static CPDFAnnotation.Type toAnnotationType(CStyleType styleType){
        switch (styleType){
            case ANNOT_TEXT:
                return CPDFAnnotation.Type.TEXT;
            case ANNOT_HIGHLIGHT:
                return CPDFAnnotation.Type.HIGHLIGHT;
            case ANNOT_UNDERLINE:
                return CPDFAnnotation.Type.UNDERLINE;
            case ANNOT_STRIKEOUT:
                return CPDFAnnotation.Type.STRIKEOUT;
            case ANNOT_SQUIGGLY:
                return CPDFAnnotation.Type.SQUIGGLY;
            case ANNOT_INK:
                return CPDFAnnotation.Type.INK;
            case ANNOT_SQUARE:
                return CPDFAnnotation.Type.SQUARE;
            case ANNOT_CIRCLE:
                return CPDFAnnotation.Type.CIRCLE;
            case ANNOT_LINE:
            case ANNOT_ARROW:
                return CPDFAnnotation.Type.LINE;
            case ANNOT_FREETEXT:
                return CPDFAnnotation.Type.FREETEXT;
            case ANNOT_SIGNATURE:
            case ANNOT_PIC:
            case ANNOT_STAMP:
                return CPDFAnnotation.Type.STAMP;
            case ANNOT_LINK:
                return CPDFAnnotation.Type.LINK;
            case ANNOT_SOUND:
                return CPDFAnnotation.Type.SOUND;
            default:
                return null;
        }
    }


    public static CStyleType getStyleType(CPDFAnnotation.Type type) {
        switch (type) {
            case TEXT:
                return CStyleType.ANNOT_TEXT;
            case HIGHLIGHT:
                return CStyleType.ANNOT_HIGHLIGHT;
            case UNDERLINE:
                return CStyleType.ANNOT_UNDERLINE;
            case STRIKEOUT:
                return CStyleType.ANNOT_STRIKEOUT;
            case SQUIGGLY:
                return CStyleType.ANNOT_SQUIGGLY;
            case INK:
                return CStyleType.ANNOT_INK;
            case SQUARE:
                return CStyleType.ANNOT_SQUARE;
            case CIRCLE:
                return CStyleType.ANNOT_CIRCLE;
            case LINE:
                return CStyleType.ANNOT_LINE;
            case FREETEXT:
                return CStyleType.ANNOT_FREETEXT;
            case STAMP:
                return CStyleType.ANNOT_STAMP;
            case LINK:
                return CStyleType.ANNOT_LINK;
            case SOUND:
                return CStyleType.ANNOT_SOUND;
            default:
                return CStyleType.UNKNOWN;
        }
    }

    public static CStyleType getStyleType(CAnnotationType type){
        return type.getStyleType();
    }

    public static CStyleType getFormStyleType(CPDFWidget.WidgetType formWidgetType) {
        switch (formWidgetType) {
            case Widget_PushButton:
                return CStyleType.FORM_PUSH_BUTTON;
            case Widget_CheckBox:
                return CStyleType.FORM_CHECK_BOX;
            case Widget_RadioButton:
                return CStyleType.FORM_RADIO_BUTTON;
            case Widget_TextField:
                return CStyleType.FORM_TEXT_FIELD;
            case Widget_ComboBox:
                return CStyleType.FORM_COMBO_BOX;
            case Widget_ListBox:
                return CStyleType.FORM_LIST_BOX;
            case Widget_SignatureFields:
                return CStyleType.FORM_SIGNATURE_FIELDS;
            default:
                return CStyleType.UNKNOWN;
        }
    }

    public static boolean isAnnotationType(CStyleType styleType){
        return styleType == CStyleType.ANNOT_TEXT
                || styleType == CStyleType.ANNOT_HIGHLIGHT
                || styleType == CStyleType.ANNOT_UNDERLINE
                || styleType == CStyleType.ANNOT_STRIKEOUT
                || styleType == CStyleType.ANNOT_SQUIGGLY
                || styleType == CStyleType.ANNOT_INK
                || styleType == CStyleType.ANNOT_CIRCLE
                || styleType == CStyleType.ANNOT_SQUARE
                || styleType == CStyleType.ANNOT_LINE
                || styleType == CStyleType.ANNOT_ARROW
                || styleType == CStyleType.ANNOT_FREETEXT
                || styleType == CStyleType.ANNOT_SIGNATURE
                || styleType == CStyleType.ANNOT_PIC
                || styleType == CStyleType.ANNOT_STAMP
                || styleType == CStyleType.ANNOT_LINK
                || styleType == CStyleType.ANNOT_SOUND;
    }

    public static boolean isFormType(CStyleType styleType){
        return styleType == CStyleType.FORM_PUSH_BUTTON
                || styleType == CStyleType.FORM_CHECK_BOX
                || styleType == CStyleType.FORM_RADIO_BUTTON
                || styleType == CStyleType.FORM_TEXT_FIELD
                || styleType == CStyleType.FORM_COMBO_BOX
                || styleType == CStyleType.FORM_LIST_BOX
                || styleType == CStyleType.FORM_SIGNATURE_FIELDS;
    }

    public static boolean isContentEditorType(CStyleType styleType){
        return styleType == CStyleType.EDIT_TEXT
                || styleType == CStyleType.EDIT_IMAGE;
    }
}
