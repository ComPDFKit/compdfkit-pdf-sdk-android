/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;

import com.compdfkit.tools.common.utils.CFileUtils;

import java.io.File;

public class CImageUtil {


    
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return CBitmapUtil.cropTransparent(bitmap);
    }

    public static Bitmap convertLongTextToBitmap(EditText editText) {
        String text = editText.getText().toString();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(editText.getTextSize());
        paint.setColor(editText.getCurrentTextColor());
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(editText.getTypeface());
        int width = editText.getWidth() - editText.getPaddingLeft() - editText.getPaddingRight();

        StaticLayout staticLayout = new StaticLayout(
                text, new TextPaint(paint), width,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int height = staticLayout.getHeight(); // 获取所有行的总高度

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT); // 设定背景色（可修改）

        float centerX = width / 2f;

        canvas.save();
        canvas.translate(centerX, 0);
        staticLayout.draw(canvas);
        canvas.restore();
        return bitmap;
    }

    public static String saveBitmap(Context context, String fileName, Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        File file = new File(context.getFilesDir(), CFileUtils.CACHE_FOLDER);
        file.mkdirs();
        File bitmapFile =  new File(file, fileName);
        boolean success = CBitmapUtil.saveBitmapToFile(bitmap,bitmapFile, 100, Bitmap.CompressFormat.PNG);
        if (success){
            return bitmapFile.getAbsolutePath();
        }else {
            return null;
        }
    }

    public static void scanFile(Context context, String filePath, String mineType) {
        try {
            MediaScannerConnection.scanFile(
                    context,
                    new String[]{filePath},
                    new String[]{mineType},
                    (path, uri) -> {
                    }
            );
        } catch (Exception e) {

        }

    }


}
