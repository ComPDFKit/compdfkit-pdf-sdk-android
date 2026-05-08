package com.compdfkit.tools.common.utils.glide;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public final class CPDFGlideLogUtils {

    private static final String TAG = "CPDFGlide";

    private CPDFGlideLogUtils() {
    }

    public static void logRequestStart(@NonNull CPDFWrapper wrapper, int width, int height) {
        Log.d(TAG, "requestStart: page=" + wrapper.getLogPageIndex()
                + ", size=" + width + "x" + height
                + ", model=" + wrapper.getLogSource());
    }

    @NonNull
    public static RequestListener<Bitmap> createRequestListener(@NonNull CPDFWrapper wrapper) {
        return new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Log.e(TAG, "requestFailed: page=" + wrapper.getLogPageIndex()
                        + ", model=" + wrapper.getLogSource()
                        + ", message=" + (e == null ? "unknown" : e.getMessage()), e);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                Log.d(TAG, "requestSuccess: page=" + wrapper.getLogPageIndex()
                        + ", source=" + describeDataSource(dataSource)
                        + ", size=" + resource.getWidth() + "x" + resource.getHeight()
                        + ", model=" + wrapper.getLogSource());
                return false;
            }
        };
    }

    @NonNull
    private static String describeDataSource(@Nullable DataSource dataSource) {
        if (dataSource == null) {
            return "UNKNOWN";
        }
        switch (dataSource) {
            case MEMORY_CACHE:
                return "MEMORY_CACHE";
            case DATA_DISK_CACHE:
                return "DATA_DISK_CACHE";
            case RESOURCE_DISK_CACHE:
                return "RESOURCE_DISK_CACHE";
            case LOCAL:
                return "LOCAL_RENDER";
            case REMOTE:
                return "REMOTE";
            default:
                return dataSource.name();
        }
    }
}
