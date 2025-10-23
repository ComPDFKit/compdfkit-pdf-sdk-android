/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfsignature.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.image.CBitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CSignatureDatas {

    public static List<String> getSignatures(Context context){
        File file = new File(context.getFilesDir(), CFileUtils.SIGNATURE_FOLDER);
        if (file.listFiles() != null){
            List<File> files = Arrays.asList(file.listFiles());
            Collections.sort(files, (o1, o2) -> {
                if (o1.lastModified() < o2.lastModified()) {
                    return 1;
                } else if (o1.lastModified() == o2.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
            });
            List<String> list = new ArrayList<>();
            for (File file1 : files) {
                list.add(file1.getAbsolutePath());
            }
            return list;
        }else {
            return new ArrayList<>();
        }
    }

    public static boolean removeSignature(String signaturePath){
        File file = new File(signaturePath);
        return file.delete();
    }

    public static String saveSignatureBitmap(Context context, Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        File file = new File(context.getFilesDir(), CFileUtils.SIGNATURE_FOLDER);
        file.mkdirs();
        File bitmapFile =  new File(file, "signature_"+ CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT)+".png");
        boolean success = CBitmapUtil.saveBitmapToFile(bitmap,bitmapFile, 100, Bitmap.CompressFormat.PNG);
        if (success){
            return bitmapFile.getAbsolutePath();
        }else {
            return null;
        }
    }

    public static String saveDigitalSignatureBitmap(Context context, Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        File file = new File(context.getFilesDir(), CFileUtils.DIGITAL_SIGNATURE_FOLDER);
        file.mkdirs();
        File bitmapFile =  new File(file, "digital_signature_"+ CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT)+".png");
        boolean success = CBitmapUtil.saveBitmapToFile(bitmap,bitmapFile, 100, Bitmap.CompressFormat.PNG);
        if (success){
            return bitmapFile.getAbsolutePath();
        }else {
            return null;
        }
    }

    public static String saveSignatureImage(Context context, Uri imageUri){
        File file = new File(context.getFilesDir(), CFileUtils.SIGNATURE_FOLDER);
        return CFileUtils.copyFileToInternalDirectory(context, imageUri, file.getAbsolutePath(),
                "signature_"+ CDateUtil.getDataTime(CDateUtil.NORMAL_DATE_FORMAT)+".png");
    }
}
