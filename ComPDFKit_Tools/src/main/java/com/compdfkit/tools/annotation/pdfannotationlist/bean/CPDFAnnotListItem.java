/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationlist.bean;


import com.compdfkit.core.annotation.CPDFAnnotation;

public class CPDFAnnotListItem {

    private CPDFAnnotation.Type annotType;

    private boolean isArrowLine = false;

    private String modifyDate;

    private int color;

    private int colorAlpha;

    private String content;

    private boolean isHeader;

    private int annotationCount;

    private int page;

    private CPDFAnnotation attr;

    public CPDFAnnotation.Type getAnnotType() {
        return annotType;
    }

    public void setAnnotType(CPDFAnnotation.Type annotType) {
        this.annotType = annotType;
    }

    public boolean isArrowLine() {
        return isArrowLine;
    }

    public void setArrowLine(boolean arrowLine) {
        isArrowLine = arrowLine;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorAlpha() {
        return colorAlpha;
    }

    public void setColorAlpha(int colorAlpha) {
        this.colorAlpha = colorAlpha;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public int getAnnotationCount() {
        return annotationCount;
    }

    public void setAnnotationCount(int annotationCount) {
        this.annotationCount = annotationCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public CPDFAnnotation getAttr() {
        return attr;
    }

    public void setAttr(CPDFAnnotation attr) {
        this.attr = attr;
    }
}
