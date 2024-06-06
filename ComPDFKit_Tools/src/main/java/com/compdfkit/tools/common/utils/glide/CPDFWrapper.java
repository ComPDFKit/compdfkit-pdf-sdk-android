/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.glide;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.model.Headers;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.glide.wrapper.CIPDFWrapper;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFCoverWrapper;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFDocumentPageWrapper;

import java.security.MessageDigest;


public class CPDFWrapper implements Key {

    public CIPDFWrapper wrapper;

    private final Headers headers = Headers.DEFAULT;

    private int hashCode;

    @Nullable
    private volatile byte[] cacheKeyBytes;

    private int width;

    private int height;

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

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String getCacheKey() {
        return wrapper.cacheKey() + "_" + width + "_" + height;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(getCacheKeyBytes());
    }

    private byte[] getCacheKeyBytes() {
        if (cacheKeyBytes == null) {
            cacheKeyBytes = getCacheKey().getBytes(CHARSET);
        }
        return cacheKeyBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CPDFWrapper) {
            CPDFWrapper other = (CPDFWrapper) o;
            return getCacheKey().equals(other.getCacheKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getCacheKey().hashCode();
            hashCode = 31 * hashCode + headers.hashCode();
        }
        return hashCode;
    }
}
