/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties;


import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;

public enum CAnnotationType {
    UNKNOWN,
    TEXT,
    HIGHLIGHT,
    UNDERLINE,
    SQUIGGLY,
    STRIKEOUT,
    INK,
    INK_ERASER,
    CIRCLE,
    SQUARE,
    ARROW,
    LINE,
    FREETEXT,
    SIGNATURE,
    STAMP,
    PIC,
    LINK,
    SOUND;

    public static CAnnotationType getType(CPDFAnnotation.Type type){
        try{
            return CAnnotationType.valueOf(type.name());
        }catch (Exception e){
            return CAnnotationType.UNKNOWN;
        }
    }

    public CStyleType getStyleType(){
        switch (this){
            case TEXT:return CStyleType.ANNOT_TEXT;
            case HIGHLIGHT:return CStyleType.ANNOT_HIGHLIGHT;
            case UNDERLINE:return CStyleType.ANNOT_UNDERLINE;
            case STRIKEOUT:return CStyleType.ANNOT_STRIKEOUT;
            case SQUIGGLY:return CStyleType.ANNOT_SQUIGGLY;
            case INK:return CStyleType.ANNOT_INK;
            case SQUARE:return CStyleType.ANNOT_SQUARE;
            case CIRCLE:return CStyleType.ANNOT_CIRCLE;
            case ARROW:return CStyleType.ANNOT_ARROW;
            case LINE:return CStyleType.ANNOT_LINE;
            case FREETEXT:return CStyleType.ANNOT_FREETEXT;
            case SIGNATURE:return CStyleType.ANNOT_SIGNATURE;
            case STAMP:return CStyleType.ANNOT_STAMP;
            case PIC:return CStyleType.ANNOT_PIC;
            case LINK:return CStyleType.ANNOT_LINK;
            case SOUND:return CStyleType.ANNOT_SOUND;
            default:return CStyleType.UNKNOWN;
        }
    }

}
