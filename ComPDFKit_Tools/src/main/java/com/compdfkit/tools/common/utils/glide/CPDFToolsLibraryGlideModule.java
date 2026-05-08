package com.compdfkit.tools.common.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

import java.util.concurrent.atomic.AtomicBoolean;

@GlideModule
public class CPDFToolsLibraryGlideModule extends LibraryGlideModule {

    private static final String TAG = "CPDFToolsGlideModule";

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    private static final AtomicBoolean FALLBACK_REGISTERED = new AtomicBoolean(false);

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        REGISTERED.set(true);
        Log.d(TAG, "registerComponents: registering ComPDFKit Tools Glide model loaders");
        registry.append(CPDFWrapper.class, Bitmap.class, new CPDFModelLoader.Factory(context));
        super.registerComponents(context, glide, registry);
    }

    public static boolean isRegistered() {
        return REGISTERED.get();
    }

    public static void ensureRuntimeRegistration(@NonNull Context context, @NonNull Glide glide) {
        if (REGISTERED.get() || !FALLBACK_REGISTERED.compareAndSet(false, true)) {
            return;
        }
        Log.w(TAG, "ensureRuntimeRegistration: LibraryGlideModule was not discovered, registering loaders with runtime fallback");
        glide.getRegistry().append(CPDFWrapper.class, Bitmap.class, new CPDFModelLoader.Factory(context));
    }
}
