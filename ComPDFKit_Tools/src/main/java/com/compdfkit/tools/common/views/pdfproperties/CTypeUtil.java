/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
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
}
