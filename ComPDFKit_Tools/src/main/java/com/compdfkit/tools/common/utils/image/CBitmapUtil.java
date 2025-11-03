/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.image;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.core.content.ContextCompat;

import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CUriUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CBitmapUtil {

    public static Bitmap cropTransparent(Bitmap sourceBitmap) {
        try {
            int sourceHeight = sourceBitmap.getHeight();
            int sourceWidth = sourceBitmap.getWidth();

            int[] pixels = new int[sourceHeight * sourceWidth];
            sourceBitmap.getPixels(pixels, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);

            int top = 0;
            int bot = sourceHeight;
            int left = 0;
            int right = sourceWidth;

            a:
            for (int i = 0; i < sourceHeight; i++) {

                for (int j = 0; j < sourceWidth; j++) {
                    int color = pixels[i * sourceWidth + j];
                    if (color != 0) {
                        top = i;
                        break a;
                    }
                }
            }

            b:
            for (int i = sourceHeight - 1; i >= 0; i--) {

                for (int j = 0; j < sourceWidth; j++) {
                    int color = pixels[i * sourceWidth + j];
                    if (color != 0) {
                        bot = i;
                        break b;
                    }
                }
            }

            c:
            for (int i = 0; i < sourceWidth; i++) {

                for (int j = 0; j < sourceHeight; j++) {
                    int color = pixels[j * sourceWidth + i];
                    if (color != 0) {
                        left = i;
                        break c;
                    }
                }
            }

            d:
            for (int i = sourceWidth - 1; i >= 0; i--) {

                for (int j = 0; j < sourceHeight; j++) {
                    int color = pixels[j * sourceWidth + i];
                    if (color != 0) {
                        right = i;
                        break d;
                    }
                }
            }
            int realWidth = right - left;
            int realHeight = bot - top;

            int[] realColors = new int[realWidth * realHeight];
            sourceBitmap.getPixels(realColors, 0, realWidth, left, top, realWidth, realHeight);
            sourceBitmap.recycle();

            return Bitmap.createBitmap(realColors, realWidth, realHeight, Bitmap.Config.ARGB_8888);
        } catch (Throwable e) {
            return sourceBitmap;
        }
    }



    public static boolean saveBitmapToFile(Bitmap bitmap, File file, int quality, Bitmap.CompressFormat format) {
        if (bitmap == null || file == null){
            return false;
        }
        if (bitmap.isRecycled()) {
            return false;
        }

        if (!file.exists()) {
            CFileUtils.createFile(file, true);
        }
        if (!file.exists()) {
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(format, quality, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Bitmap decodeBitmap(String path)  {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            int degrees = CUriUtil.getBitmapDegree(path);
            if (degrees > 0){
                Matrix matrix = new Matrix();
                matrix.setRotate(degrees);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri)  {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            if (parcelFileDescriptor == null){
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());
            int degrees = CUriUtil.getBitmapDegree(context, uri);
            if (degrees > 0){
                Matrix matrix = new Matrix();
                matrix.setRotate(degrees);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Drawable getDrawableByName(Context context, String resName) {
        int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        if (resId != 0) {
            return ContextCompat.getDrawable(context, resId);
        }
        return null;
    }

    public static Bitmap getBitmapByName(Context context, String resName) {
        int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        if (resId != 0) {
            return BitmapFactory.decodeResource(context.getResources(), resId);
        }
        return null;
    }

    public static int getBitmapResId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }
}
