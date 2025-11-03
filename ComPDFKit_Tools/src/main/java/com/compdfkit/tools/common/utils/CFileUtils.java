package com.compdfkit.tools.common.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class CFileUtils {

    public static final String CERTIFICATE_DIGITAL_TYPE = "application/x-pkcs12";

    public static final String ROOT_FOLDER = "compdfkit/";

    public static final String CACHE_FOLDER = "compdfkit/temp";

    public static final String SIGNATURE_FOLDER = "compdfkit/annot/signature";
    public static final String DIGITAL_SIGNATURE_FOLDER = "compdfkit/form/digitalSignature";

    public static final String IMAGE_STAMP_FOLDER = "compdfkit/annot/stamp/image";

    public static final String TEXT_STAMP_FOLDER = "compdfkit/annot/stamp/text";

    public static final String SOUND_FOLDER = "compdfkit/annot/sound";

    public static final String EXPORT_FOLDER = "compdfkit/edit/export";

    public static final String EXTRACT_FOLDER = "compdfkit/extract";

    public static String getAssetsTempFile(Context context, String assetsName, String saveName) {
        String path = context.getCacheDir().getAbsolutePath();
        copyFileFromAssets(context, assetsName, path, saveName, false);
        return path + "/" + saveName;
    }

    public static String getAssetsTempFile(Context context, String assetsName, String saveName, boolean overwrite) {
        String path = context.getCacheDir().getAbsolutePath();
        copyFileFromAssets(context, assetsName, path, saveName, overwrite);
        return path + "/" + saveName;
    }

    public static void copyFileFromAssets(Context context,
                                          String assetName,
                                          String savePath,
                                          String saveName,
                                          final boolean overwriteExisting) {
        //if save path folder not exists, create directory
        File dir = new File(savePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                CLog.d("FileUtils", "mkdir error: " + savePath);
                return;
            }
        }

        // 拷贝文件
        String filename = savePath + "/" + saveName;
        File file = new File(filename);
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
            } catch (IOException ignored) {
            }
            CLog.d("FileUtils", "[copyFileFromAssets] copy asset file: " + assetName + " to : " + filename);
        } else {
            CLog.d("FileUtils", "[copyFileFromAssets] file is exist: " + filename);
        }
    }


    public static void createFile(File file, boolean isFile) {// 创建文件
        try {
            if (!file.exists()) {
                // 如果文件不存在
                File parentFile = file.getParentFile();
                if (parentFile != null && parentFile.exists()) {
                    // 如果文件父目录不存在
                    createFile(file.getParentFile(), false);
                } else {
                    // 存在文件父目录
                    if (isFile) {
                        // 创建文件
                        file.createNewFile();// 创建新文件
                    } else {
                        file.mkdir();// 创建目录
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static String copyFileToInternalDirectory(
            Context context,
            Uri uri,
            String dir,
            String fileName
    ) {
        try {
            File file = new File(dir, fileName);
            File parentFile = file.getParentFile();
            if (parentFile != null){
                parentFile.mkdirs();
            }

            ContentResolver cr = context.getContentResolver();
            ParcelFileDescriptor fd = cr.openFileDescriptor(uri, "r");
            if (fd == null){
                return null;
            }
            FileInputStream ist = new FileInputStream(fd.getFileDescriptor());
            FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath());
            if (writeFile(ist, outputStream)) {
                return file.getAbsolutePath();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean writeFileFromString(final File file,
                                              final String content,
                                              final boolean append) {
        if (file == null || content == null) {
            return false;
        }
        if (file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            return false;
        }
        BufferedWriter bw = null;
        try {
            file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static String readFile2String(final File file, final String charsetName) {
        if (file == null || !file.exists()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (isSpace(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), charsetName)
                );
            }
            String line;
            if ((line = reader.readLine()) != null) {
                sb.append(line);
                while ((line = reader.readLine()) != null) {
                    sb.append(System.getProperty("line.separator")).append(line);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static Boolean writeFile(InputStream fileInputStream, OutputStream fileOutputStream) {

        try {

            byte[] data = new byte[2048];
            int length;
            while ((length = fileInputStream.read(data)) != -1) {
                fileOutputStream.write(data, 0, length);
            }
            fileOutputStream.flush();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException ignored) {
            }
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

    public static void shareFile(Context context, String title, String type, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setDataAndType(getUriBySystem(context, file), type);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_STREAM, getUriBySystem(context, file));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, title));
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static void shareFile(Context context, String title, String type, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setDataAndType(uri, type);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, title));
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static Intent getContentIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return intent;
    }

    public static Intent getIntent(String type) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    public static void notifyMediaStore(Context context, String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(filePath));
            intent.setData(uri);
            context.sendBroadcast(intent);
        } catch (Exception ignored) {

        }
    }

    public static Intent selectSystemDir(boolean isOnlyLocal) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.putExtra("android.content.extra.SHOW_ADVANCED", isOnlyLocal);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        return intent;
    }

    public static void takeUriPermission(Context context, Uri uri) {
        try {
            context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } catch (Exception ignored) {

        }
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    filePath = context.getExternalFilesDir(null) + "/" + split[1];
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                filePath = getDataColumn(context, contentUri, null, null);
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                filePath = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            filePath = getDataColumn(context, uri, null, null);
        }

        return filePath;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            }
        }

        return filePath;
    }


    public static String getFileNameNoExtension(final File file) {
        if (file == null) {
            return null;
        }
        return getFileNameNoExtension(file.getPath());
    }

    public static String getFileNameNoExtension(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    public static File renameNameSuffix(File file) {
        if (file.exists()) {
            String fileNameNotExtension = getFileNameNoExtension(file);
            String extension = getFileExtension(file);

            int index = 1;
            File destFile;
            do {
                destFile = new File(file.getParentFile(), fileNameNotExtension + "(" + index++ + ")." + extension);
            } while (destFile.exists());
            return destFile;
        }
        return file;
    }

    public static String getFileExtension(final File file) {
        if (file == null) {
            return null;
        }
        return getFileExtension(file.getPath());
    }

    public static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) {
            return "";
        }
        return filePath.substring(lastPoi + 1);
    }


    public static String fileSizeFormat(long fileSize) {
        final long MB = 1024 * 1024;
        final int KB = 1024;
        float size;
        String unit = " M";
        if (fileSize > MB) {
            size = ((float) fileSize / (1024 * 1024));
        } else if (fileSize > KB) {
            size = ((float) fileSize / 1024);
            unit = " KB";
        } else {
            size = fileSize;
            unit = " B";
        }
        return String.format("%.2f", size) + unit;
    }

    public static String readStringFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();

        InputStream inputStream = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuilder sb = new StringBuilder();
        try {
            inputStream = assetManager.open(fileName);
            isr = new InputStreamReader(inputStream);
            br = new BufferedReader(isr);

            sb.append(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                sb.append("\n").append(line);
            }

            br.close();
            isr.close();
            inputStream.close();
        } catch (IOException ignored) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
        return sb.toString();
    }


    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }

    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    public static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        if (files != null){
            for (File file : files) {
                if (file.isFile()) {
                    flag = deleteSingleFile(file.getAbsolutePath());
                    if (!flag)
                        break;
                } else if (file.isDirectory()) {
                    flag = deleteDirectory(file
                            .getAbsolutePath());
                    if (!flag)
                        break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        return dirFile.delete();
    }

    public static void startPrint(Context context, String filePath, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.setPackage("com.android.bips");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (uri != null && uri.toString().startsWith("content://")) {
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(intent);
                return;
            }
            String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!TextUtils.isEmpty(filePath)) {
                if (filePath.startsWith(context.getCacheDir().getAbsolutePath()) ||
                        filePath.startsWith(context.getFilesDir().getAbsolutePath())) {
                    Uri uri1 = getUriBySystem(context, new File(filePath));
                    intent.setData(uri1);
                    intent.putExtra(Intent.EXTRA_STREAM, uri1);
                } else if (filePath.startsWith(externalStoragePath)) {
                    Uri uri2 = CFileUtils.getUriBySystem(context, new File(filePath));
                    intent.setData(uri2);
                    intent.putExtra(Intent.EXTRA_STREAM, uri2);
                }
            }
            context.startActivity(intent);
        } catch (Exception ignored) {

        }
    }

    public static void copyAssetsDirToPhone(Activity activity, String assetsPath, String outPutParentDir){
        try {
            String[] fileList = activity.getAssets().list(assetsPath);
            if(fileList != null && fileList.length>0) {//如果是目录
                File file = new File(outPutParentDir+ File.separator+assetsPath);
                boolean result = file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName:fileList){
                    assetsPath=assetsPath+File.separator+fileName;
                    copyAssetsDirToPhone(activity,assetsPath, outPutParentDir);
                    assetsPath=assetsPath.substring(0,assetsPath.lastIndexOf(File.separator));
                }
            } else {//如果是文件
                InputStream inputStream=activity.getAssets().open(assetsPath);
                File file=new File(outPutParentDir+ File.separator+assetsPath);
                if(!file.exists() || file.length()==0) {
                    FileOutputStream fos=new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len=inputStream.read(buffer))!=-1){
                        fos.write(buffer,0,len);
                    }
                    fos.flush();
                    inputStream.close();
                    fos.close();
                }
            }
        } catch (IOException ignored) {
        }
    }

}
