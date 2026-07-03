/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
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
        if (editText == null || editText.getText() == null || editText.getText().length() == 0) {
            return null;
        }
        String text = editText.getText().toString();
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(editText.getTextSize());
        textPaint.setColor(editText.getCurrentTextColor());
        textPaint.setTypeface(editText.getTypeface());
        int width = editText.getWidth() - editText.getPaddingLeft() - editText.getPaddingRight();
        if (width <= 0) {
            width = (int) Math.ceil(Layout.getDesiredWidth(text, textPaint));
        }
        if (width <= 0) {
            return null;
        }

        StaticLayout measureLayout = new StaticLayout(
                text, textPaint, width,
                Layout.Alignment.ALIGN_CENTER, editText.getLineSpacingMultiplier(), editText.getLineSpacingExtra(), false);
        float maxLineWidth = 0F;
        for (int i = 0; i < measureLayout.getLineCount(); i++) {
            maxLineWidth = Math.max(maxLineWidth, measureLayout.getLineWidth(i));
        }
        if (maxLineWidth <= 0F) {
            return null;
        }
        int contentWidth = Math.min(width, Math.max(1, (int) Math.ceil(maxLineWidth) + 2));
        StaticLayout staticLayout = new StaticLayout(
                text, textPaint, contentWidth,
                Layout.Alignment.ALIGN_CENTER, editText.getLineSpacingMultiplier(), editText.getLineSpacingExtra(), false);
        int height = staticLayout.getHeight();
        if (height <= 0) {
            return null;
        }

        int padding = (int) (editText.getResources().getDisplayMetrics().density * 2 + 0.5F);
        Bitmap bitmap = Bitmap.createBitmap(contentWidth + padding * 2, height + padding * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.translate(padding, padding);
        staticLayout.draw(canvas);
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
