/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.glide.wrapper.impl;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.glide.wrapper.CIPDFWrapper;

public class CPDFCoverWrapper implements CIPDFWrapper {
    private String pdfFilePath;

    private Uri pdfFileUri;

    public CPDFCoverWrapper(String pdfFilePath){
        this.pdfFilePath = pdfFilePath;
    }

    public CPDFCoverWrapper(Uri pdfFileUri){
        this.pdfFileUri = pdfFileUri;
    }

    public @Nullable CPDFDocument getCoverPdfDocument(Context context){
        CPDFDocument cpdfDocument = new CPDFDocument(context);
        CPDFDocument.PDFDocumentError error;
        if (!TextUtils.isEmpty(pdfFilePath)){
            error = cpdfDocument.open(pdfFilePath);
        } else if (pdfFileUri != null){
            error = cpdfDocument.open(pdfFileUri);
        }else {
            error = CPDFDocument.PDFDocumentError.PDFDocumentErrorUnknown;
        }
        if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess){
            return cpdfDocument;
        }else {
            return null;
        }
    }

    @Override
    public boolean isAvailable(){
        return !TextUtils.isEmpty(pdfFilePath) || pdfFileUri != null;
    }

    @Override
    public String cacheKey() {
        if (!TextUtils.isEmpty(pdfFilePath)){
            return pdfFilePath;
        } else if (pdfFileUri != null) {
            return pdfFileUri.toString();
        }else {
            return "";
        }
    }
}
