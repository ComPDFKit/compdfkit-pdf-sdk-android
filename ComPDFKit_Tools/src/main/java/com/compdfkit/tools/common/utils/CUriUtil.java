package com.compdfkit.tools.common.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.compdfkit.tools.BuildConfig;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CUriUtil {

    private static final String STORAGE_TAG = "CPDFStorage";

    private static final ExecutorService IMAGE_LOG_EXECUTOR = Executors.newSingleThreadExecutor();

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

    public static void logImageUriInfo(Context context, String stage, Uri uri) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (context == null || uri == null) {
            CLog.e("ImageAnnot", stage + ", uri is null");
            return;
        }
        Context appContext = context.getApplicationContext();
        IMAGE_LOG_EXECUTOR.execute(() -> logImageUriInfoInternal(appContext, stage, uri));
    }

    private static void logImageUriInfoInternal(Context context, String stage, Uri uri) {
        InputStream inputStream = null;
        try {
            String mime = context.getContentResolver().getType(uri);
            String name = getUriFileName(context, uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            CLog.e("ImageAnnot", stage
                    + ", uri=" + safeUri(uri)
                    + ", name=" + name
                    + ", resolverMime=" + mime
                    + ", bitmapMime=" + options.outMimeType
                    + ", width=" + options.outWidth
                    + ", height=" + options.outHeight);
        } catch (Exception e) {
            CLog.e("ImageAnnot", stage + ", uri=" + uri + ", error=" + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void logImageFileInfo(String stage, String path) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (TextUtils.isEmpty(path)) {
            CLog.e("ImageAnnot", stage + ", path is empty");
            return;
        }
        IMAGE_LOG_EXECUTOR.execute(() -> logImageFileInfoInternal(stage, path));
    }

    private static void logImageFileInfoInternal(String stage, String path) {
        try {
            File file = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            CLog.e("ImageAnnot", stage
                    + ", fileName=" + file.getName()
                    + ", exists=" + file.exists()
                    + ", size=" + (file.exists() ? file.length() : -1)
                    + ", bitmapMime=" + options.outMimeType
                    + ", width=" + options.outWidth
                    + ", height=" + options.outHeight);
        } catch (Exception e) {
            CLog.e("ImageAnnot", stage + ", path=" + path + ", error=" + e.getMessage());
        }
    }

    public static String getImageExtension(Context context, Uri uri) {
        String mimeType = "";
        try {
            mimeType = context.getContentResolver().getType(uri);
        } catch (Exception ignored) {
        }
        String extension = extensionFromMime(mimeType);
        if (!TextUtils.isEmpty(extension)) {
            return extension;
        }

        extension = extensionFromMime(getUriType(context, uri));
        if (!TextUtils.isEmpty(extension)) {
            return extension;
        }

        extension = extensionFromFileName(getUriFileName(context, uri));
        if (!TextUtils.isEmpty(extension)) {
            return extension;
        }

        InputStream inputStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            extension = extensionFromMime(options.outMimeType);
            if (!TextUtils.isEmpty(extension)) {
                return extension;
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {
            }
        }
        return ".jpg";
    }

    private static String extensionFromMime(String mimeType) {
        if ("image/png".equalsIgnoreCase(mimeType)) {
            return ".png";
        }
        if ("image/jpeg".equalsIgnoreCase(mimeType) || "image/jpg".equalsIgnoreCase(mimeType)) {
            return ".jpg";
        }
        if ("image/heic".equalsIgnoreCase(mimeType)) {
            return ".heic";
        }
        if ("image/heif".equalsIgnoreCase(mimeType)) {
            return ".heif";
        }
        if ("image/webp".equalsIgnoreCase(mimeType)) {
            return ".webp";
        }
        return "";
    }

    private static String extensionFromFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".png")) {
            return ".png";
        }
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return ".jpg";
        }
        if (lowerName.endsWith(".heic")) {
            return ".heic";
        }
        if (lowerName.endsWith(".heif")) {
            return ".heif";
        }
        if (lowerName.endsWith(".webp")) {
            return ".webp";
        }
        return "";
    }

    private static String safeUri(Uri uri) {
        if (uri == null) {
            return "null";
        }
        return uri.getScheme() + "://" + uri.getAuthority();
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
        try { 
            ExifInterface exifInterface = new ExifInterface(path);
            
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
        CLog.d(STORAGE_TAG, "createFileUri start publicDirectory=" + publicDirectory
                + ", fileName=" + fileName
                + ", mimeType=" + mimeType
                + ", sdk=" + Build.VERSION.SDK_INT);
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
            String path = CPDFStorageManager.getLegacyPublicDirectoryPath(publicDirectory) + File.separator + fileName;
            contentValues.put(MediaStore.Files.FileColumns.DATA, path);
            File parentFile = new File(path).getParentFile();
            boolean created = parentFile != null && (parentFile.exists() || parentFile.mkdirs());
            CLog.d(STORAGE_TAG, "createFileUri legacy dataPath=" + path + ", parentReady=" + created);
        }
        Uri collectionUri = getMediaStoreCollectionUri(publicDirectory, mimeType);
        CLog.d(STORAGE_TAG, "createFileUri collectionUri=" + safeUri(collectionUri));
        Uri uri = context.getContentResolver().insert(collectionUri, contentValues);
        CLog.d(STORAGE_TAG, "createFileUri result uri=" + safeUri(uri));
        return uri;
    }

    public static MediaStoreSaveResult publishFileToMediaStore(Context context,
                                                               File sourceFile,
                                                               String publicDirectory,
                                                               String fileName,
                                                               String mimeType,
                                                               boolean deleteSource) {
        Uri uri = null;
        try {
            CLog.d(STORAGE_TAG, "publishFileToMediaStore start source=" + fileInfo(sourceFile)
                    + ", publicDirectory=" + publicDirectory
                    + ", fileName=" + fileName
                    + ", mimeType=" + mimeType
                    + ", deleteSource=" + deleteSource
                    + ", sdk=" + Build.VERSION.SDK_INT);
            if (sourceFile == null || !sourceFile.exists()) {
                CLog.e(STORAGE_TAG, "publishFileToMediaStore fail source missing, source=" + fileInfo(sourceFile));
                return MediaStoreSaveResult.fail(null, "source file missing");
            }
            uri = createPendingFileUri(context, publicDirectory, fileName, mimeType);
            if (uri == null) {
                CLog.e(STORAGE_TAG, "publishFileToMediaStore fail create uri returned null");
                return MediaStoreSaveResult.fail(null, "create MediaStore uri returned null");
            }
            try (InputStream inputStream = new FileInputStream(sourceFile)) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream == null) {
                    CLog.e(STORAGE_TAG, "publishFileToMediaStore fail output stream null, uri=" + safeUri(uri));
                    context.getContentResolver().delete(uri, null, null);
                    return MediaStoreSaveResult.fail(uri, "open MediaStore output stream failed");
                }
                if (!CFileUtils.writeFile(inputStream, outputStream)) {
                    CLog.e(STORAGE_TAG, "publishFileToMediaStore fail copy returned false, uri=" + safeUri(uri));
                    context.getContentResolver().delete(uri, null, null);
                    return MediaStoreSaveResult.fail(uri, "copy file to MediaStore failed");
                }
            }
            CLog.d(STORAGE_TAG, "publishFileToMediaStore copy success uri=" + safeUri(uri));
            finishPendingFile(context, uri);
            if (deleteSource) {
                boolean deleted = sourceFile.delete();
                CLog.d(STORAGE_TAG, "publishFileToMediaStore delete source=" + sourceFile.getAbsolutePath() + ", deleted=" + deleted);
            }
            String path = getUriData(context, uri);
            CLog.d(STORAGE_TAG, "publishFileToMediaStore success uri=" + safeUri(uri) + ", dataPath=" + path);
            return MediaStoreSaveResult.success(uri, path);
        } catch (Exception e) {
            CLog.e(STORAGE_TAG, "publishFileToMediaStore exception uri=" + safeUri(uri)
                    + ", source=" + fileInfo(sourceFile)
                    + ", error=" + e.getMessage());
            if (uri != null) {
                try {
                    int deleted = context.getContentResolver().delete(uri, null, null);
                    CLog.d(STORAGE_TAG, "publishFileToMediaStore cleanup uri=" + safeUri(uri) + ", deleted=" + deleted);
                } catch (Exception cleanupError) {
                    CLog.e(STORAGE_TAG, "publishFileToMediaStore cleanup exception uri=" + safeUri(uri)
                            + ", error=" + cleanupError.getMessage());
                }
            }
            return MediaStoreSaveResult.fail(uri, e.getMessage());
        }
    }

    private static Uri createPendingFileUri(Context context, String publicDirectory, String fileName, String mimeType) {
        CLog.d(STORAGE_TAG, "createPendingFileUri start publicDirectory=" + publicDirectory
                + ", fileName=" + fileName
                + ", mimeType=" + mimeType
                + ", sdk=" + Build.VERSION.SDK_INT);
        long currentTime = System.currentTimeMillis() / 1000;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, currentTime);
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, currentTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, publicDirectory);
            contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, 1);
            contentValues.put(MediaStore.Files.FileColumns.DATE_TAKEN, currentTime);
        } else {
            String path = CPDFStorageManager.getLegacyPublicDirectoryPath(publicDirectory) + File.separator + fileName;
            contentValues.put(MediaStore.Files.FileColumns.DATA, path);
            File parentFile = new File(path).getParentFile();
            if (parentFile != null) {
                boolean created = parentFile.exists() || parentFile.mkdirs();
                CLog.d(STORAGE_TAG, "createPendingFileUri legacy dataPath=" + path + ", parentReady=" + created);
            }
        }
        Uri collectionUri = getMediaStoreCollectionUri(publicDirectory, mimeType);
        CLog.d(STORAGE_TAG, "createPendingFileUri collectionUri=" + safeUri(collectionUri));
        Uri uri = context.getContentResolver().insert(collectionUri, contentValues);
        CLog.d(STORAGE_TAG, "createPendingFileUri result uri=" + safeUri(uri));
        return uri;
    }

    private static Uri getMediaStoreCollectionUri(String publicDirectory, String mimeType) {
        String primaryDirectory = getPrimaryDirectory(publicDirectory);
        if (Environment.DIRECTORY_DOWNLOADS.equals(primaryDirectory)
                || Environment.DIRECTORY_DOCUMENTS.equals(primaryDirectory)) {
            CLog.d(STORAGE_TAG, "getMediaStoreCollectionUri use Files collection for primaryDirectory=" + primaryDirectory
                    + ", mimeType=" + mimeType);
            return MediaStore.Files.getContentUri("external");
        }
        if (!TextUtils.isEmpty(mimeType)) {
            String lowerMimeType = mimeType.toLowerCase();
            if (lowerMimeType.startsWith("image/")) {
                CLog.d(STORAGE_TAG, "getMediaStoreCollectionUri use Images collection for primaryDirectory=" + primaryDirectory
                        + ", mimeType=" + mimeType);
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
            if (lowerMimeType.startsWith("video/")) {
                CLog.d(STORAGE_TAG, "getMediaStoreCollectionUri use Video collection for primaryDirectory=" + primaryDirectory
                        + ", mimeType=" + mimeType);
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
            if (lowerMimeType.startsWith("audio/")) {
                CLog.d(STORAGE_TAG, "getMediaStoreCollectionUri use Audio collection for primaryDirectory=" + primaryDirectory
                        + ", mimeType=" + mimeType);
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
        }
        CLog.d(STORAGE_TAG, "getMediaStoreCollectionUri use Files collection by default primaryDirectory=" + primaryDirectory
                + ", mimeType=" + mimeType);
        return MediaStore.Files.getContentUri("external");
    }

    private static String getPrimaryDirectory(String publicDirectory) {
        if (TextUtils.isEmpty(publicDirectory)) {
            return "";
        }
        String normalized = publicDirectory.replace('\\', '/');
        int slashIndex = normalized.indexOf('/');
        return slashIndex >= 0 ? normalized.substring(0, slashIndex) : normalized;
    }

    private static void finishPendingFile(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            CLog.d(STORAGE_TAG, "finishPendingFile skipped sdk=" + Build.VERSION.SDK_INT + ", uri=" + safeUri(uri));
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, 0);
        int updated = context.getContentResolver().update(uri, contentValues, null, null);
        CLog.d(STORAGE_TAG, "finishPendingFile uri=" + safeUri(uri) + ", updated=" + updated);
    }

    private static String fileInfo(File file) {
        if (file == null) {
            return "null";
        }
        return file.getAbsolutePath()
                + ", exists=" + file.exists()
                + ", size=" + (file.exists() ? file.length() : -1)
                + ", canRead=" + file.canRead();
    }

    public static class MediaStoreSaveResult {

        private final boolean success;

        private final Uri uri;

        private final String path;

        private final String errorMessage;

        private MediaStoreSaveResult(boolean success, Uri uri, String path, String errorMessage) {
            this.success = success;
            this.uri = uri;
            this.path = path;
            this.errorMessage = errorMessage;
        }

        public static MediaStoreSaveResult success(Uri uri, String path) {
            return new MediaStoreSaveResult(true, uri, path, null);
        }

        public static MediaStoreSaveResult fail(Uri uri) {
            return fail(uri, null);
        }

        public static MediaStoreSaveResult fail(Uri uri, String errorMessage) {
            return new MediaStoreSaveResult(false, uri, null, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public Uri getUri() {
            return uri;
        }

        public String getPath() {
            return path;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
