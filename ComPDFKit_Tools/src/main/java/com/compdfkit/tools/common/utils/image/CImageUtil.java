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
import android.view.View;

import com.compdfkit.tools.common.utils.CFileUtils;

import java.io.File;

public class CImageUtil {


    
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return CBitmapUtil.cropTransparent(bitmap);
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


}
