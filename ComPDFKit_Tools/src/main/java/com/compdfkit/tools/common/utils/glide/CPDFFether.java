/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFDocumentPageWrapper;

class CPDFFether implements DataFetcher<Bitmap> {
    private static final String TAG = "CPDFGlide";
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private CPDFDocumentPageWrapper cpdfWrapper;

    private volatile boolean isCancelled;

    private CPDFDocument tpdfDocument;

    private final Context context;

    private final boolean ownsDocument;

    private final int loadImageWidth;

    private final int loadImageHeight;

    public CPDFFether(CPDFDocumentPageWrapper cpdfWrapper, int width, int height, boolean ownsDocument) {
        this.cpdfWrapper = cpdfWrapper;
        tpdfDocument = cpdfWrapper.getDocument();
        context = tpdfDocument.getContext();
        this.loadImageWidth = width;
        this.loadImageHeight = height;
        this.ownsDocument = ownsDocument;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Bitmap> callback) {
        try {
            isCancelled = false;
            if (tpdfDocument == null || cpdfWrapper == null || isCancelled) {
                return;
            }
            Log.d(TAG, "renderStart: page=" + cpdfWrapper.getPageIndex()
                    + ", size=" + loadImageWidth + "x" + loadImageHeight
                    + ", message=start render or re-render after cache miss");
            Bitmap result = renderPage(cpdfWrapper.getPageIndex(), loadImageWidth, loadImageHeight);
            if (isCancelled) {
                if (result != null && !result.isRecycled()) {
                    Glide.get(context).getBitmapPool().put(result);
                }
                return;
            }
            Log.d(TAG, "renderSuccess: page=" + cpdfWrapper.getPageIndex()
                    + ", size=" + result.getWidth() + "x" + result.getHeight());
            callback.onDataReady(result);
        } catch (Exception e) {
            if (isCancelled) {
                return;
            }
            Log.e(TAG, "renderFailed: page="
                    + (cpdfWrapper == null ? -1 : cpdfWrapper.getPageIndex())
                    + ", size=" + loadImageWidth + "x" + loadImageHeight
                    + ", message=" + e.getMessage(), e);
            callback.onLoadFailed(e);
        }
    }

    @NonNull
    private Bitmap renderPage(int pageIndex, int width, int height) throws Exception {
        if (tpdfDocument == null) {
            throw new Exception("CPDFDocument is null!");
        }
        Bitmap bitmap = Glide.get(context).getBitmapPool().get(width, height, BITMAP_CONFIG);
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
                cpdfWrapper.isDrawAnnotation(),
                cpdfWrapper.isDrawForms());

        if (!res || bitmap.isRecycled()) {
            if (!bitmap.isRecycled()) {
                Glide.get(context).getBitmapPool().put(bitmap);
            }
            throw new Exception("Failed to render PDF page " + pageIndex + ".");
        }
        return bitmap;
    }

    @Override
    public void cleanup() {
        Log.d(TAG, "cleanup: page=" + (cpdfWrapper == null ? -1 : cpdfWrapper.getPageIndex())
                + ", ownsDocument=" + ownsDocument);
        if (ownsDocument && tpdfDocument != null) {
            tpdfDocument.close();
        }
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
        return DataSource.LOCAL;
    }

    @Override
    public void cancel() {
        isCancelled = true;
        Log.d(TAG, "cancel: page=" + (cpdfWrapper == null ? -1 : cpdfWrapper.getPageIndex()));
    }
}
