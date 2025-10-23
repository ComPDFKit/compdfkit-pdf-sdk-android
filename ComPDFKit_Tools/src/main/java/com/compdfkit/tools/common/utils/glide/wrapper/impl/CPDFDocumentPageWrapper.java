package com.compdfkit.tools.common.utils.glide.wrapper.impl;


import android.graphics.Color;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.glide.wrapper.CIPDFWrapper;

public class CPDFDocumentPageWrapper implements CIPDFWrapper {
    public static final String TPDF = "TPDF:";

    private CPDFDocument document;

    private int pageIndex;

    private int backgroundColor = Color.WHITE;

    private boolean drawAnnotation;

    private boolean drawForms;

    public CPDFDocumentPageWrapper(CPDFDocument cpdfDocument, int pageIndex){
        this.document = cpdfDocument;
        this.pageIndex = pageIndex;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public CPDFDocument getDocument() {
        return document;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isDrawAnnotation() {
        return drawAnnotation;
    }

    public void setDrawAnnotation(boolean drawAnnotation) {
        this.drawAnnotation = drawAnnotation;
    }

    public boolean isDrawForms() {
        return drawForms;
    }

    public void setDrawForms(boolean drawForms) {
        this.drawForms = drawForms;
    }

    @Override
    public String cacheKey() {
        return TPDF + document.getAbsolutePath() +
                "_" +
                pageIndex +
                "_" +
                backgroundColor +
                "_" +
                drawAnnotation +
                "_" +
                drawForms;
    }

    @Override
    public boolean isAvailable() {
        return document != null;
    }
}
