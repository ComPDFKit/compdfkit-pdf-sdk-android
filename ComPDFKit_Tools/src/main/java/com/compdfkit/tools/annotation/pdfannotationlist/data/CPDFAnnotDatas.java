package com.compdfkit.tools.annotation.pdfannotationlist.data;


import android.graphics.Color;

import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFCircleAnnotation;
import com.compdfkit.core.annotation.CPDFFreetextAnnotation;
import com.compdfkit.core.annotation.CPDFHighlightAnnotation;
import com.compdfkit.core.annotation.CPDFInkAnnotation;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFSoundAnnotation;
import com.compdfkit.core.annotation.CPDFSquareAnnotation;
import com.compdfkit.core.annotation.CPDFSquigglyAnnotation;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.core.annotation.CPDFStrikeoutAnnotation;
import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.core.annotation.CPDFUnderlineAnnotation;
import com.compdfkit.core.common.CPDFDate;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.annotation.pdfannotationlist.bean.CPDFAnnotListItem;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;

import java.util.ArrayList;
import java.util.List;

public class CPDFAnnotDatas {

    public static List<CPDFAnnotListItem> getAnnotationList(@Nullable CPDFViewCtrl pdfView) {
        List<CPDFAnnotListItem> list = new ArrayList<>();
        try{
            if (pdfView == null){
                return new ArrayList<>();
            }
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            int count = document.getPageCount();

            for (int i = 0; i <= count - 1; i++) {
                List<CPDFAnnotation> pageAnnotations = document.pageAtIndex(i).getAnnotations();
                List<CPDFAnnotListItem> annotListItems = convertPageAnnotations(pageAnnotations, i);
                if (annotListItems.size() > 0){
                    CPDFAnnotListItem headerItem = new CPDFAnnotListItem();
                    headerItem.setHeader(true);
                    headerItem.setAnnotationCount(annotListItems.size());
                    headerItem.setPage(i);
                    list.add(headerItem);
                    list.addAll(annotListItems);
                }
            }
        }catch (Exception e){

        }
        return list;
    }

    private static List<CPDFAnnotListItem> convertPageAnnotations(List<CPDFAnnotation> list, int page){
        List<CPDFAnnotListItem> annotListItems = new ArrayList<>();
        if (list == null || list.size() == 0){
            return annotListItems;
        }
        for (CPDFAnnotation cpdfAnnotation : list) {
            if (cpdfAnnotation == null || !cpdfAnnotation.isValid()){
                continue;
            }
            CPDFAnnotation.Type type = cpdfAnnotation.getType();
            boolean isArrowLine = true;
            int attrColor = Color.TRANSPARENT;
            String attrTime = "";
            if (null != cpdfAnnotation.getCreationDate()){
                attrTime = CPDFDate.toStandardDate(cpdfAnnotation.getCreationDate());
            }
            int attrAlpha = 0;
            String attrContent = "";
            if (!cpdfAnnotation.isValid()) {
                return annotListItems;
            }
            switch (cpdfAnnotation.getType()){
                case TEXT:
                    CPDFTextAnnotation textAnnotation = (CPDFTextAnnotation) cpdfAnnotation;
                    attrColor = textAnnotation.getColor();
                    attrAlpha = textAnnotation.getAlpha();
                    attrContent = textAnnotation.getContent();
                    break;
                case HIGHLIGHT:
                    CPDFHighlightAnnotation highlightAnnotation = (CPDFHighlightAnnotation) cpdfAnnotation;
                    attrColor = highlightAnnotation.getColor();
                    attrAlpha = highlightAnnotation.getAlpha();
                    attrContent = highlightAnnotation.getMarkedText();
                    break;
                case UNDERLINE:
                    CPDFUnderlineAnnotation underlineAnnotation = (CPDFUnderlineAnnotation) cpdfAnnotation;
                    attrColor = underlineAnnotation.getColor();
                    attrAlpha = underlineAnnotation.getAlpha();
                    attrContent = underlineAnnotation.getMarkedText();
                    break;
                case STRIKEOUT:
                    CPDFStrikeoutAnnotation strikeoutAnnotation = (CPDFStrikeoutAnnotation) cpdfAnnotation;
                    attrColor = strikeoutAnnotation.getColor();
                    attrAlpha = strikeoutAnnotation.getAlpha();
                    attrContent = strikeoutAnnotation.getMarkedText();
                    break;
                case SQUIGGLY:
                    CPDFSquigglyAnnotation squigglyAnnotation = (CPDFSquigglyAnnotation) cpdfAnnotation;
                    attrColor = squigglyAnnotation.getColor();
                    attrAlpha = squigglyAnnotation.getAlpha();
                    attrContent = squigglyAnnotation.getMarkedText();
                    break;
                case INK:
                    CPDFInkAnnotation cpdfInkAnnotation = (CPDFInkAnnotation) cpdfAnnotation;
                    attrColor = cpdfInkAnnotation.getColor();
                    attrAlpha = cpdfInkAnnotation.getAlpha();
                    attrContent = cpdfInkAnnotation.getContent();
                    break;
                case LINE:
                    CPDFLineAnnotation lineAnnotation = (CPDFLineAnnotation) cpdfAnnotation;
                    isArrowLine = lineAnnotation.getLineHeadType() == CPDFLineAnnotation.LineType.LINETYPE_ARROW;
                    attrColor = lineAnnotation.getBorderColor();
                    attrAlpha = lineAnnotation.getBorderAlpha();
                    attrContent = lineAnnotation.getContent();
                    break;
                case FREETEXT:
                    CPDFFreetextAnnotation annotation = (CPDFFreetextAnnotation) cpdfAnnotation;
                    attrContent = annotation.getContent();
                    break;
                case STAMP:
                    CPDFStampAnnotation stampAnnotation = (CPDFStampAnnotation) cpdfAnnotation;
                    attrContent = stampAnnotation.getContent();
                    if (stampAnnotation.isStampSignature()){
                        continue;
                    }
                    break;
                case SQUARE:
                    CPDFSquareAnnotation squareAnnotation = (CPDFSquareAnnotation) cpdfAnnotation;
                    attrColor = squareAnnotation.getBorderColor();
                    attrAlpha = squareAnnotation.getBorderAlpha();
                    attrContent = squareAnnotation.getContent();
                    break;
                case CIRCLE:
                    CPDFCircleAnnotation circleAnnotation = (CPDFCircleAnnotation) cpdfAnnotation;
                    attrColor = circleAnnotation.getBorderColor();
                    attrAlpha = circleAnnotation.getBorderAlpha();
                    attrContent = circleAnnotation.getContent();
                    break;
                case SOUND:
                    CPDFSoundAnnotation soundAnnotation = (CPDFSoundAnnotation) cpdfAnnotation;
                    attrContent = soundAnnotation.getContent();
                    break;
                default:
                    continue;
            }
            CPDFAnnotListItem item = new CPDFAnnotListItem();
            item.setAnnotType(type);
            item.setArrowLine(isArrowLine);
            item.setModifyDate(CDateUtil.transformPDFDate(attrTime));
            item.setColor(attrColor);
            item.setColorAlpha(attrAlpha);
            item.setContent(attrContent);
            item.setPage(page);
            item.setAnnotationCount(0);
            item.setAttr(cpdfAnnotation);
            item.setHeader(false);
            annotListItems.add(item);
        }
        return annotListItems;
    }
}
