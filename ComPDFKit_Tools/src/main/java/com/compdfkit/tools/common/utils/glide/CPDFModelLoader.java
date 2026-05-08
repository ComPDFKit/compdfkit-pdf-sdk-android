package com.compdfkit.tools.common.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.request.target.Target;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFCoverWrapper;
import com.compdfkit.tools.common.utils.glide.wrapper.impl.CPDFDocumentPageWrapper;

class CPDFModelLoader implements ModelLoader<CPDFWrapper, Bitmap> {
    private final Context context;

    public CPDFModelLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public LoadData<Bitmap> buildLoadData(@NonNull CPDFWrapper wrapper, int width, int height, @NonNull Options options) {
        Object sourceWrapper = wrapper.getWrapper();
        CPDFDocumentPageWrapper pageWrapper;
        boolean ownsDocument = false;
        if (sourceWrapper instanceof CPDFCoverWrapper) {
            CPDFDocument document = ((CPDFCoverWrapper) sourceWrapper).getCoverPdfDocument(context);
            if (document == null) {
                return null;
            }
            pageWrapper = new CPDFDocumentPageWrapper(document, 0);
            ownsDocument = true;
        } else {
            pageWrapper = (CPDFDocumentPageWrapper) sourceWrapper;
        }
        int[] resolvedSize = resolveSize(pageWrapper.getDocument(), pageWrapper.getPageIndex(), width, height);
        DataFetcher<Bitmap> dataFetcher = new CPDFFether(pageWrapper, resolvedSize[0], resolvedSize[1], ownsDocument);
        return new LoadData<>(new CPDFCacheKey(pageWrapper.cacheKey(), resolvedSize[0], resolvedSize[1]), dataFetcher);
    }

    @Override
    public boolean handles(@NonNull CPDFWrapper wrapper) {
        return wrapper.getWrapper().isAvailable();
    }

    @NonNull
    private int[] resolveSize(@NonNull CPDFDocument document, int pageIndex, int width, int height) {
        RectF sizeRect = document.pageAtIndex(pageIndex).getSize();
        int resolvedWidth = width == Target.SIZE_ORIGINAL ? Math.max(1, Math.round(sizeRect.width())) : width;
        int resolvedHeight = height == Target.SIZE_ORIGINAL ? Math.max(1, Math.round(sizeRect.height())) : height;
        return new int[]{Math.max(1, resolvedWidth), Math.max(1, resolvedHeight)};
    }

    public static class Factory implements ModelLoaderFactory<CPDFWrapper, Bitmap> {
        private final Context context;

        public Factory(Context context) {
            this.context = context.getApplicationContext();
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
