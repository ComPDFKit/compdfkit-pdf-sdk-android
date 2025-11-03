package com.compdfkit.tools.common.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;

public class CUriUtil {

    public static String getUriType(Context context, Uri uri) {

        Cursor cursor = null;
        final String column = MediaStore.Files.FileColumns.MIME_TYPE;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception ignored) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static String getUriFileName(Context context, Uri uri) {

        Cursor cursor = null;
        final String column = MediaStore.Files.FileColumns.DISPLAY_NAME;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception ignored) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static String getUriData(Context context, Uri uri) {

        Cursor cursor = null;
        final String column = MediaStore.Files.FileColumns.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception ignored) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static String copyUriToInternalCache(Context context, Uri uri) {
        try {
            File file = new File(context.getFilesDir(), CFileUtils.CACHE_FOLDER);
            String fileName = CUriUtil.getUriFileName(context, uri);
            if (TextUtils.isEmpty(fileName)) {
                fileName = "pic_" + System.currentTimeMillis() + ".png";
            }
            return CFileUtils.copyFileToInternalDirectory(
                    context, uri, file.getAbsolutePath(), fileName);
        } catch (Exception e) {
            return "";
        }
    }


    public static int getBitmapDegree(String path) {
        int degree = 0;
        if (TextUtils.isEmpty(path)) {
            return degree;
        }
        try { // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:break;
            }
            return degree;
        } catch (Exception e) {
            return degree;
        }
    }

    public static int getBitmapDegree(Context context, Uri uri) {
        try {
            int degree = 0;
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null){
                return 0;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ExifInterface exifInterface = new ExifInterface(inputStream);
                // 获取图片的旋转信息
                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                );
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:break;
                }
                return degree;
            }else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getUriFd(Context context, Uri uri){
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            ParcelFileDescriptor fileDescriptor = resolver.openFileDescriptor(uri, "r");
            if (fileDescriptor == null){
                return 0;
            }
            int detachFd = fileDescriptor.detachFd();
            if (inputStream != null) {
                inputStream.close();
            }
            fileDescriptor.close();
            return detachFd;
        } catch (Exception e) {
            return 0;
        }
    }


    public static void sendEmail(Context context, String emailAddress, String title){
        Uri uri = Uri.parse("mailto:"+emailAddress);
        Intent data=new Intent(Intent.ACTION_SEND);
        data.setData(uri);
        data.putExtra(Intent.EXTRA_SUBJECT, title);
        context.startActivity(Intent.createChooser(data, "select"));
    }

    public static Uri createFileUri(Context context, String publicDirectory, String fileName, String mimeType) {
        long currentTime = System.currentTimeMillis() / 1000;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, currentTime);
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, currentTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, publicDirectory);
            contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, 0);
            contentValues.put(MediaStore.Files.FileColumns.DATE_TAKEN, currentTime);
        }else {
            String path = Environment.getExternalStoragePublicDirectory(publicDirectory).getAbsolutePath() + File.separator + fileName;
            contentValues.put(MediaStore.Files.FileColumns.DATA, path);
            new File(path).getParentFile().mkdirs();
        }
        return context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
    }
}
