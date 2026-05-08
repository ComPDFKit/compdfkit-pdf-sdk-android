/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.glide;

import android.net.Uri;

import androidx.annotation.NonNull;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.glide.wrapper.CIPDFWrapper;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFCoverWrapper;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFDocumentPageWrapper;

public class CPDFWrapper {

    private final CIPDFWrapper wrapper;

    public CPDFWrapper(CIPDFWrapper cipdfWrapper) {
        this.wrapper = cipdfWrapper;
    }

    public static CPDFWrapper fromFile(String pdfFilePath) {
        return new CPDFWrapper(new CPDFCoverWrapper(pdfFilePath));
    }

    public static CPDFWrapper fromUri(Uri pdfFileUri) {
        return new CPDFWrapper(new CPDFCoverWrapper(pdfFileUri));
    }

    public static CPDFWrapper fromDocument(CPDFDocument cPdfDocument, int pageIndex) {
        return new CPDFWrapper(new CPDFDocumentPageWrapper(cPdfDocument, pageIndex));
    }

    public int getLogPageIndex() {
        if (wrapper instanceof CPDFDocumentPageWrapper) {
            return ((CPDFDocumentPageWrapper) wrapper).getPageIndex();
        }
        return 0;
    }

    @NonNull
    public String getLogSource() {
        return wrapper.cacheKey();
    }

    @NonNull
    CIPDFWrapper getWrapper() {
        return wrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CPDFWrapper) {
            CPDFWrapper other = (CPDFWrapper) o;
            return getLogSource().equals(other.getLogSource());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getLogSource().hashCode();
    }
}
