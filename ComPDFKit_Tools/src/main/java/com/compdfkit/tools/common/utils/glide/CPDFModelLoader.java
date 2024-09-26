package com.compdfkit.tools.common.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFCoverWrapper;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFDocumentPageWrapper;

class CPDFModelLoader implements ModelLoader<CPDFWrapper, Bitmap> {
    private Context context;

    public CPDFModelLoader(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<Bitmap> buildLoadData(@NonNull CPDFWrapper wrapper, int width, int height, @NonNull Options options) {
        wrapper.setSize(width, height);
        CPDFDocumentPageWrapper pageWrapper;
        if (wrapper.wrapper instanceof CPDFCoverWrapper) {
            CPDFDocument document = ((CPDFCoverWrapper) wrapper.wrapper).getCoverPdfDocument(context);
            pageWrapper = new CPDFDocumentPageWrapper(document, 0);
        } else {
            pageWrapper = (CPDFDocumentPageWrapper) wrapper.wrapper;
        }
        DataFetcher<Bitmap> dataFetcher = new CPDFFether(pageWrapper, width, height);
        return new LoadData<>(wrapper, dataFetcher);
    }

    @Override
    public boolean handles(@NonNull CPDFWrapper wrapper) {
        return wrapper.wrapper.isAvailable();
    }

    public static class Factory implements ModelLoaderFactory<CPDFWrapper, Bitmap> {
        Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ModelLoader<CPDFWrapper, Bitmap> build(MultiModelLoaderFactory multiFactory) {
            return new CPDFModelLoader(context);
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
