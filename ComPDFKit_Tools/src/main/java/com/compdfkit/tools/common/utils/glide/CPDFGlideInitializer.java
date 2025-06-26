package com.compdfkit.tools.common.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;

/**
 * @classname:
 * @author: LiuXiaoLong
 * @date: 2025/6/23
 * description:
 */
public class CPDFGlideInitializer {

    private static boolean registered = false;

    public static synchronized void register(Context context) {
        if (registered){
            return;
        }
        Glide glide = Glide.get(context);
        Registry registry = glide.getRegistry();
        registry.append(CPDFWrapper.class, Bitmap.class, new CPDFModelLoader.Factory(context));
        registered = true;
    }
}
