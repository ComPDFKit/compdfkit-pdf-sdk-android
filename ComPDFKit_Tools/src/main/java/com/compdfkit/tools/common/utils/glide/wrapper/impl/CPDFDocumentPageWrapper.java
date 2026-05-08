package com.compdfkit.tools.common.utils.glide.wrapper.impl;


import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.common.utils.glide.CPDFThumbnailCacheRevisionManager;
import com.compdfkit.tools.common.utils.glide.wrapper.CIPDFWrapper;

import java.io.File;

public class CPDFDocumentPageWrapper implements CIPDFWrapper {
    public static final String TPDF = "TPDF:";

    private CPDFDocument document;

    private int pageIndex;

    private int backgroundColor = Color.WHITE;

    private boolean drawAnnotation;

    private boolean drawForms;

    private int rotation;

    public CPDFDocumentPageWrapper(CPDFDocument cpdfDocument, int pageIndex){
        this(cpdfDocument, pageIndex, Color.WHITE, false, false);
    }

    public CPDFDocumentPageWrapper(CPDFDocument cpdfDocument, int pageIndex, int backgroundColor, boolean drawAnnotation, boolean drawForms){
        this.document = cpdfDocument;
        this.pageIndex = pageIndex;
        this.backgroundColor = backgroundColor;
        this.drawAnnotation = drawAnnotation;
        this.drawForms = drawForms;
        this.rotation = resolveRotation(cpdfDocument, pageIndex);
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
        String documentKey = getDocumentKey();
        return TPDF + documentKey +
                "_" +
                pageIndex +
                "_" +
                CPDFThumbnailCacheRevisionManager.getRevision(document) +
                "_" +
                rotation +
                "_" +
                backgroundColor +
                "_" +
                drawAnnotation +
                "_" +
                drawForms;
    }

    private int resolveRotation(CPDFDocument document, int pageIndex) {
        if (document == null) {
            return 0;
        }
        CPDFPage page = document.pageAtIndex(pageIndex);
        return page == null ? 0 : page.getRotation();
    }

    private String getDocumentKey() {
        if (document == null) {
            return "";
        }
        String absolutePath = document.getAbsolutePath();
        if (!TextUtils.isEmpty(absolutePath)) {
            File file = new File(absolutePath);
            return absolutePath + "_" + file.lastModified() + "_" + file.length();
        }
        Uri uri = document.getUri();
        if (uri != null) {
            return uri.toString();
        }
        return "";
    }

    @Override
    public boolean isAvailable() {
        return document != null;
    }
}
