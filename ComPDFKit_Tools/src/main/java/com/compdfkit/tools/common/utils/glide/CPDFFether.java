/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.request.target.Target;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFDocumentPageWrapper;

class CPDFFether implements DataFetcher<Bitmap> {
    private static final int MAXIMUM_REDIRECTS = 1;

    private CPDFDocumentPageWrapper cpdfWrapper;

    private volatile boolean isCancelled;

    private CPDFDocument tpdfDocument;

    private Context context;

    private int loadImageWidth;

    private int loadImageHeight;

    public CPDFFether(CPDFDocumentPageWrapper cpdfWrapper, int width, int height) {
        this.cpdfWrapper = cpdfWrapper;
        tpdfDocument = cpdfWrapper.getDocument();
        context = tpdfDocument.getContext();
        this.loadImageWidth = width;
        this.loadImageHeight = height;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Bitmap> callback) {
        try {
            isCancelled = false;
            Bitmap result = loadDataWithRedirects(cpdfWrapper.getPageIndex(),
                    loadImageWidth,loadImageHeight,
                    0);
            callback.onDataReady(result);
        } catch (Exception e) {
            callback.onLoadFailed(e);
        }
    }

    private Bitmap loadDataWithRedirects(int pageIndex, int width, int height, int redirects) throws Exception {
        if (redirects >= MAXIMUM_REDIRECTS) {
            throw new Exception("Too many (> " + MAXIMUM_REDIRECTS + ") redirects!");
        }
        if (tpdfDocument == null) {
            throw new Exception("CPDFDocument is null!");
        }
        RectF sizeRect = tpdfDocument.pageAtIndex(pageIndex).getSize();
        if (width == Target.SIZE_ORIGINAL ){
            width = (int) sizeRect.width();
        }
        if (height == Target.SIZE_ORIGINAL){
            height = (int) sizeRect.height();
        }
        Bitmap bitmap = Glide.get(context).getBitmapPool().get(width, height, Bitmap.Config.ARGB_4444);
        boolean res = tpdfDocument.renderPageAtIndex(bitmap,
                pageIndex,
                width,
                height,
                0,
                0,
                width,
                height,
                cpdfWrapper.getBackgroundColor(),
                255,
                0,
                true,
                true);

        if (!res || (null == bitmap) || bitmap.isRecycled()) {
            return loadDataWithRedirects(pageIndex, width, height, ++redirects);
        }
        return isCancelled ? null : bitmap;
    }

    @Override
    public void cleanup() {
        cpdfWrapper = null;
        tpdfDocument = null;
    }

    @NonNull
    @Override
    public Class<Bitmap> getDataClass() {
        return Bitmap.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }
}
