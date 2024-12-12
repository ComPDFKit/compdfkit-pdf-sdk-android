/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.provider;

import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFCircleAnnotation;
import com.compdfkit.core.annotation.CPDFFreetextAnnotation;
import com.compdfkit.core.annotation.CPDFInkAnnotation;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFMarkupAnnotation;
import com.compdfkit.core.annotation.CPDFSquareAnnotation;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.common.views.pdfproperties.CTypeUtil;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.proxy.CPDFCircleAnnotImpl;
import com.compdfkit.ui.proxy.CPDFFreetextAnnotImpl;
import com.compdfkit.ui.proxy.CPDFInkAnnotImpl;
import com.compdfkit.ui.proxy.CPDFLineAnnotImpl;
import com.compdfkit.ui.proxy.CPDFMarkupAnnotImpl;
import com.compdfkit.ui.proxy.CPDFSquareAnnotImpl;
import com.compdfkit.ui.reader.PageView;

import java.util.LinkedHashSet;


public class CSelectedAnnotStyleProvider implements CStyleProvider {

    private CPDFBaseAnnotImpl annotImpl;

    private PageView pageView;

    public CSelectedAnnotStyleProvider(CPDFBaseAnnotImpl annotImpl, PageView pageView) {
        this.annotImpl = annotImpl;
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
            if (annotImpl instanceof CPDFMarkupAnnotImpl) {
                CPDFMarkupAnnotImpl markupAnnot = (CPDFMarkupAnnotImpl) annotImpl;
                CPDFMarkupAnnotation markupAnnotation = markupAnnot.onGetAnnotation();
                markupAnnotation.setColor(style.getColor());
                markupAnnotation.setAlpha(style.getOpacity());
                markupAnnotation.updateAp();
                annotImpl.onAnnotAttrChange();
                pageView.invalidate();
            } else if (annotImpl instanceof CPDFInkAnnotImpl) {
                CPDFInkAnnotImpl inkAnnot = (CPDFInkAnnotImpl) annotImpl;
                CPDFInkAnnotation inkAnnotation = (CPDFInkAnnotation) inkAnnot.onGetAnnotation();
                inkAnnotation.setColor(style.getColor());
                inkAnnotation.setAlpha(style.getOpacity());
                inkAnnotation.setBorderWidth(style.getBorderWidth());
                inkAnnotation.updateAp();
                annotImpl.onAnnotAttrChange();
                pageView.invalidate();
            } else if (annotImpl instanceof CPDFSquareAnnotImpl) {
                CPDFSquareAnnotImpl squareAnnot = (CPDFSquareAnnotImpl) annotImpl;
                CPDFSquareAnnotation squareAnnotation = (CPDFSquareAnnotation) squareAnnot.onGetAnnotation();
                squareAnnotation.setBorderColor(style.getLineColor());
                squareAnnotation.setBorderAlpha(style.getLineColorOpacity());
                squareAnnotation.setFillColor(style.getFillColor());
                squareAnnotation.setFillAlpha(style.getFillColorOpacity());
                squareAnnotation.setBorderStyle(style.getBorderStyle());
                if (style.getBordEffectType() != squareAnnotation.getBordEffectType()){
                    squareAnnotation.setBordEffectType(style.getBordEffectType());
                }
                squareAnnotation.updateAp();
                annotImpl.onAnnotAttrChange();
                pageView.invalidate();
            } else if (annotImpl instanceof CPDFCircleAnnotImpl) {
                CPDFCircleAnnotImpl circleAnnot = (CPDFCircleAnnotImpl) annotImpl;
                CPDFCircleAnnotation circleAnnotation = (CPDFCircleAnnotation) circleAnnot.onGetAnnotation();
                circleAnnotation.setBorderColor(style.getLineColor());
                circleAnnotation.setBorderAlpha(style.getLineColorOpacity());
                circleAnnotation.setFillColor(style.getFillColor());
                circleAnnotation.setFillAlpha(style.getFillColorOpacity());
                circleAnnotation.setBorderStyle(style.getBorderStyle());
                if (style.getBordEffectType() != circleAnnotation.getBordEffectType()){
                    circleAnnotation.setBordEffectType(style.getBordEffectType());
                }
                circleAnnotation.updateAp();
                annotImpl.onAnnotAttrChange();
                pageView.invalidate();
            } else if (annotImpl instanceof CPDFLineAnnotImpl) {
                CPDFLineAnnotImpl lineAnnot = (CPDFLineAnnotImpl) annotImpl;
                CPDFLineAnnotation lineAnnotation = lineAnnot.onGetAnnotation();
                lineAnnotation.setBorderColor(style.getLineColor());
                lineAnnotation.setBorderAlpha(style.getLineColorOpacity());
                lineAnnotation.setFillColor(style.getFillColor());
                lineAnnotation.setFillAlpha(style.getFillColorOpacity());
                lineAnnotation.setBorderStyle(style.getBorderStyle());
                lineAnnotation.setLineType(style.getStartLineType(), style.getTailLineType());
                lineAnnotation.updateAp();
                annotImpl.onAnnotAttrChange();
                pageView.invalidate();
            } else if (annotImpl instanceof CPDFFreetextAnnotImpl) {
                CPDFFreetextAnnotImpl freetextAnnot = (CPDFFreetextAnnotImpl) annotImpl;
                CPDFFreetextAnnotation freetextAnnotation = freetextAnnot.onGetAnnotation();
                switch (style.getAlignment()) {
                    case LEFT:
                        freetextAnnotation.setFreetextAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_LEFT);
                        break;
                    case CENTER:
                        freetextAnnotation.setFreetextAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_CENTER);
                        break;
                    case RIGHT:
                        freetextAnnotation.setFreetextAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_RIGHT);
                        break;
                    default:
                        freetextAnnotation.setFreetextAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_UNKNOW);
                        break;
                }
                freetextAnnotation.setAlpha(style.getTextColorOpacity());
                freetextAnnotation.setFreetextDa(new CPDFTextAttribute(getAnnotStyleFontName(style), style.getFontSize(), style.getTextColor()));
                freetextAnnotation.updateAp();
                annotImpl.onAnnotAttrChange();
                pageView.invalidate();
            }
        }
    }

    @Override
    public CAnnotStyle getStyle(CStyleType type) {
        CAnnotStyle style = new CAnnotStyle(type);
        switch (type) {
            case ANNOT_HIGHLIGHT:
            case ANNOT_UNDERLINE:
            case ANNOT_SQUIGGLY:
            case ANNOT_STRIKEOUT:
                CPDFMarkupAnnotImpl markupAnnot = (CPDFMarkupAnnotImpl) annotImpl;
                CPDFMarkupAnnotation markupAnnotation = markupAnnot.onGetAnnotation();
                style.setColor(markupAnnotation.getColor());
                style.setOpacity(markupAnnotation.getAlpha());
                break;
            case ANNOT_INK:
                CPDFInkAnnotImpl inkAnnot = (CPDFInkAnnotImpl) annotImpl;
                CPDFInkAnnotation inkAnnotation = (CPDFInkAnnotation) inkAnnot.onGetAnnotation();
                style.setColor(inkAnnotation.getColor());
                style.setOpacity(inkAnnotation.getAlpha());
                style.setBorderWidth(inkAnnotation.getBorderWidth());
                break;
            case ANNOT_SQUARE:
                CPDFSquareAnnotImpl squareAnnot = (CPDFSquareAnnotImpl) annotImpl;
                CPDFSquareAnnotation squareAnnotation = (CPDFSquareAnnotation) squareAnnot.onGetAnnotation();
                style.setBorderColor(squareAnnotation.getBorderColor());
                style.setLineColorOpacity(squareAnnotation.getBorderAlpha());
                style.setFillColor(squareAnnotation.getFillColor());
                style.setFillColorOpacity(squareAnnotation.getFillAlpha());
                style.setBorderWidth(squareAnnotation.getBorderWidth());
                style.setBordEffectType(squareAnnotation.getBordEffectType());
                CPDFBorderStyle borderStyle = squareAnnotation.getBorderStyle();
                if (borderStyle == null){
                    borderStyle = new CPDFBorderStyle();
                }
                if (borderStyle.getDashArr() == null || borderStyle.getDashArr().length < 2) {
                    borderStyle.setDashArr(new float[]{});
                }
                style.setBorderStyle(borderStyle);
                break;
            case ANNOT_CIRCLE:
                CPDFCircleAnnotImpl circleAnnot = (CPDFCircleAnnotImpl) annotImpl;
                CPDFCircleAnnotation circleAnnotation = (CPDFCircleAnnotation) circleAnnot.onGetAnnotation();
                style.setBorderColor(circleAnnotation.getBorderColor());
                style.setLineColorOpacity(circleAnnotation.getBorderAlpha());
                style.setFillColor(circleAnnotation.getFillColor());
                style.setFillColorOpacity(circleAnnotation.getFillAlpha());
                style.setBorderWidth(circleAnnotation.getBorderWidth());
                style.setBordEffectType(circleAnnotation.getBordEffectType());
                CPDFBorderStyle circleBorderStyle = circleAnnotation.getBorderStyle();
                if (circleBorderStyle == null){
                    circleBorderStyle = new CPDFBorderStyle();
                }
                if (circleBorderStyle.getDashArr() == null || circleBorderStyle.getDashArr().length < 2) {
                    circleBorderStyle.setDashArr(new float[]{});
                }
                style.setBorderStyle(circleBorderStyle);
                break;
            case ANNOT_LINE:
            case ANNOT_ARROW:
                CPDFLineAnnotImpl lineAnnot = (CPDFLineAnnotImpl) annotImpl;
                CPDFLineAnnotation lineAnnotation = lineAnnot.onGetAnnotation();
                style.setBorderColor(lineAnnotation.getBorderColor());
                style.setLineColorOpacity(lineAnnotation.getBorderAlpha());
                style.setFillColor(lineAnnotation.getFillColor());
                style.setFillColorOpacity(lineAnnotation.getFillAlpha());
                style.setBorderWidth(lineAnnotation.getBorderWidth());
                CPDFBorderStyle lineBorderStyle = lineAnnotation.getBorderStyle();
                if (lineBorderStyle == null){
                    lineBorderStyle = new CPDFBorderStyle();
                }
                if (lineBorderStyle.getDashArr() == null || lineBorderStyle.getDashArr().length < 2) {
                    lineBorderStyle.setDashArr(new float[]{});
                }
                style.setBorderStyle(lineBorderStyle);
                style.setStartLineType(lineAnnotation.getLineHeadType());
                style.setTailLineType(lineAnnotation.getLineTailType());
                if (lineAnnotation.getLineHeadType() != CPDFLineAnnotation.LineType.LINETYPE_NONE
                        || lineAnnotation.getLineTailType() != CPDFLineAnnotation.LineType.LINETYPE_NONE){
                    style.setType(CStyleType.ANNOT_ARROW);
                }
                break;
            case ANNOT_FREETEXT:
                CPDFFreetextAnnotImpl freetextAnnot = (CPDFFreetextAnnotImpl) annotImpl;
                CPDFFreetextAnnotation freetextAnnotation = freetextAnnot.onGetAnnotation();
                CPDFTextAttribute textAttribute = freetextAnnotation.getFreetextDa();
                style.setFontColor(textAttribute.getColor());
                style.setTextColorOpacity(freetextAnnotation.getAlpha());
                style.setFontBold(CPDFTextAttribute.FontNameHelper.isBold(textAttribute.getFontName()));
                style.setFontItalic(CPDFTextAttribute.FontNameHelper.isItalic(textAttribute.getFontName()));
                updateAnnotStyleFont(style, textAttribute.getFontName());
                style.setFontSize((int) textAttribute.getFontSize());
                switch (freetextAnnotation.getFreetextAlignment()){
                    case ALIGNMENT_LEFT:
                        style.setAlignment(CAnnotStyle.Alignment.LEFT);
                        break;
                    case ALIGNMENT_RIGHT:
                        style.setAlignment(CAnnotStyle.Alignment.RIGHT);
                        break;
                    case ALIGNMENT_CENTER:
                        style.setAlignment(CAnnotStyle.Alignment.CENTER);
                        break;
                    default:
                        style.setAlignment(CAnnotStyle.Alignment.UNKNOWN);
                        break;
                }
                break;
            default:
                break;
        }
        return style;
    }

    public CAnnotStyle getStyle() {
        return getStyle(CTypeUtil.getStyleType(annotImpl.getAnnotType()));
    }

}
