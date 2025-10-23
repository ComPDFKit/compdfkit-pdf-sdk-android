/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileUtils {

    public static void shareFile(Context context, String title, String type, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            Uri uri = getUriBySystem(context, file);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setDataAndType(uri, type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, title));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Uri getUriBySystem(Context context, File file) {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                return Uri.fromFile(file);
            } else {
                return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getNameWithoutExtension(String name){
        int index = name.lastIndexOf(".");
        return index == -1 ? name : name.substring(0, index);
    }

    public static String getAssetsTempFile(Context context, String assetsName) {
        return copyFileFromAssets(context, assetsName, context.getCacheDir().getAbsolutePath(), assetsName, true);
    }



    public static String copyFileFromAssets(Context context,
                                            String assetName,
                                            String savePath,
                                            String saveName,
                                            final boolean overwriteExisting) {
        //if save path folder not exists, create directory
        File dir = new File(savePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }

        // 拷贝文件
        String filename = savePath + "/" + saveName;
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists() || overwriteExisting) {
            try {
                InputStream inStream = context.getAssets().open(assetName);
                FileOutputStream fileOutputStream = new FileOutputStream(filename);
                int byteread;
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, byteread);
                }
                fileOutputStream.flush();
                inStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getAbsolutePath();
        } else {
            return file.getAbsolutePath();
        }
    }

    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }
}
