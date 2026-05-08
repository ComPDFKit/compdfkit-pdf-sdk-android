package com.compdfkit.tools.common.utils.glide;

import android.content.Context;

import com.bumptech.glide.Glide;

/**
 * @classname:
 * @author: LiuXiaoLong
 * @date: 2025/6/23
 * description:
 */
public class CPDFGlideInitializer {

    public static synchronized void register(Context context) {
        if (context == null) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        CPDFToolsLibraryGlideModule.ensureRuntimeRegistration(applicationContext, Glide.get(applicationContext));
    }
}
