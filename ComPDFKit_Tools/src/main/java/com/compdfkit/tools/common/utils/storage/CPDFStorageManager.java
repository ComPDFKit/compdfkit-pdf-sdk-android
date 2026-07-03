package com.compdfkit.tools.common.utils.storage;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CUriUtil;

import java.io.File;

public class CPDFStorageManager {

    private static final String TAG = "CPDFStorage";

    private static CPDFStorageConfig storageConfig = CPDFStorageConfig.getDefault();

    private CPDFStorageManager() {
    }

    public static void setStorageConfig(CPDFStorageConfig config) {
        storageConfig = config == null ? CPDFStorageConfig.getDefault() : config;
        CLog.d(TAG, "setStorageConfig cacheFolder=" + storageConfig.getCacheFolderName()
                + ", downloadPath=" + storageConfig.getDownloadRelativePath()
                + ", imagePath=" + storageConfig.getImageRelativePath()
                + ", certificatePath=" + storageConfig.getCertificateRelativePath());
    }

    public static CPDFStorageConfig getStorageConfig() {
        return storageConfig;
    }

    public static File createTempFile(Context context, String fileName) {
        File dir = new File(context.getCacheDir(), storageConfig.getCacheFolderName());
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            CLog.d(TAG, "createTempFile mkdir dir=" + dir.getAbsolutePath() + ", created=" + created);
        }
        File file = CFileUtils.renameNameSuffix(new File(dir, fileName));
        CLog.d(TAG, "createTempFile file=" + file.getAbsolutePath()
                + ", exists=" + file.exists()
                + ", parentExists=" + (file.getParentFile() != null && file.getParentFile().exists()));
        return file;
    }

    public static String getDownloadDisplayPath() {
        return storageConfig.getDownloadRelativePath();
    }

    public static String getDownloadRelativePath() {
        return storageConfig.getDownloadRelativePath();
    }

    public static String getImageRelativePath() {
        return storageConfig.getImageRelativePath();
    }

    public static String getCertificateRelativePath() {
        return storageConfig.getCertificateRelativePath();
    }

    public static String getDefaultDirectoryDialogPath() {
        return getLegacyPublicDirectoryPath(storageConfig.getDownloadRelativePath());
    }

    public static String getLegacyPublicDirectoryPath(String relativePath) {
        if (TextUtils.isEmpty(relativePath)) {
            relativePath = storageConfig.getDownloadRelativePath();
        }
        String normalized = relativePath.replace('\\', '/');
        String rootName;
        String childPath = "";
        int slashIndex = normalized.indexOf('/');
        if (slashIndex >= 0) {
            rootName = normalized.substring(0, slashIndex);
            childPath = normalized.substring(slashIndex + 1);
        } else {
            rootName = normalized;
        }
        File root = Environment.getExternalStoragePublicDirectory(rootName);
        String path = TextUtils.isEmpty(childPath) ? root.getAbsolutePath() : new File(root, childPath).getAbsolutePath();
        CLog.d(TAG, "getLegacyPublicDirectoryPath relativePath=" + relativePath + ", path=" + path);
        return path;
    }

    public static boolean shouldRequestLegacyWritePermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
    }

    public static File createTempFileForPublicSave(Context context, String fileName) {
        File file = createTempFile(context, fileName);
        CLog.d(TAG, "createTempFileForPublicSave file=" + fileInfo(file));
        return file;
    }

    public static PublicSaveResult publishTempFileToSelectedDirectory(Context context,
                                                                      File tempFile,
                                                                      String selectedDirectory,
                                                                      String fileName,
                                                                      String mimeType,
                                                                      boolean deleteSource) {
        CLog.d(TAG, "publishTempFileToSelectedDirectory start temp=" + fileInfo(tempFile)
                + ", selectedDirectory=" + selectedDirectory
                + ", fileName=" + fileName
                + ", mimeType=" + mimeType
                + ", deleteSource=" + deleteSource
                + ", sdk=" + Build.VERSION.SDK_INT);
        if (tempFile == null || !tempFile.exists()) {
            CLog.e(TAG, "publishTempFileToSelectedDirectory fail temp missing, temp=" + fileInfo(tempFile));
            return PublicSaveResult.fail("temp file missing");
        }
        if (TextUtils.isEmpty(selectedDirectory)) {
            CLog.e(TAG, "publishTempFileToSelectedDirectory fail selected directory empty");
            return PublicSaveResult.fail("selected directory empty");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File targetFile = CFileUtils.renameNameSuffix(new File(selectedDirectory, fileName));
            if (tempFile.equals(targetFile)) {
                CLog.d(TAG, "publishTempFileToSelectedDirectory legacy target is source, path=" + tempFile.getAbsolutePath());
                return PublicSaveResult.success(tempFile.getAbsolutePath(), null, tempFile.getAbsolutePath());
            }
            try {
                File parentFile = targetFile.getParentFile();
                if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
                    CLog.e(TAG, "publishTempFileToSelectedDirectory legacy mkdir fail target=" + targetFile.getAbsolutePath());
                    return PublicSaveResult.fail("create target directory failed");
                }
                boolean copyResult = CFileUtils.writeFile(new java.io.FileInputStream(tempFile), new java.io.FileOutputStream(targetFile));
                if (!copyResult) {
                    CLog.e(TAG, "publishTempFileToSelectedDirectory legacy copy fail target=" + targetFile.getAbsolutePath());
                    return PublicSaveResult.fail("copy file failed");
                }
                if (deleteSource) {
                    boolean deleted = tempFile.delete();
                    CLog.d(TAG, "publishTempFileToSelectedDirectory legacy delete temp=" + tempFile.getAbsolutePath() + ", deleted=" + deleted);
                }
                CLog.d(TAG, "publishTempFileToSelectedDirectory legacy success target=" + targetFile.getAbsolutePath());
                return PublicSaveResult.success(targetFile.getAbsolutePath(), null, targetFile.getAbsolutePath());
            } catch (Exception e) {
                CLog.e(TAG, "publishTempFileToSelectedDirectory legacy exception target=" + targetFile.getAbsolutePath()
                        + ", error=" + e.getMessage());
                return PublicSaveResult.fail(e.getMessage());
            }
        }
        String relativePath = toPublicRelativePath(selectedDirectory);
        if (TextUtils.isEmpty(relativePath)) {
            CLog.e(TAG, "publishTempFileToSelectedDirectory fail unsupported selected directory=" + selectedDirectory);
            return PublicSaveResult.fail("unsupported selected directory");
        }
        CUriUtil.MediaStoreSaveResult mediaStoreSaveResult = CUriUtil.publishFileToMediaStore(context,
                tempFile,
                relativePath,
                fileName,
                mimeType,
                deleteSource);
        if (!mediaStoreSaveResult.isSuccess()) {
            CLog.e(TAG, "publishTempFileToSelectedDirectory mediaStore fail relativePath=" + relativePath
                    + ", error=" + mediaStoreSaveResult.getErrorMessage());
            return PublicSaveResult.fail(mediaStoreSaveResult.getErrorMessage());
        }
        CLog.d(TAG, "publishTempFileToSelectedDirectory mediaStore success relativePath=" + relativePath
                + ", uri=" + mediaStoreSaveResult.getUri()
                + ", dataPath=" + mediaStoreSaveResult.getPath());
        return PublicSaveResult.success(mediaStoreSaveResult.getPath(), mediaStoreSaveResult.getUri(), relativePath);
    }

    public static String toPublicRelativePath(String publicDirectoryPath) {
        if (TextUtils.isEmpty(publicDirectoryPath)) {
            return null;
        }
        File externalRoot = Environment.getExternalStorageDirectory();
        String rootPath = externalRoot.getAbsolutePath();
        String normalizedPath = normalizeAbsolutePath(publicDirectoryPath);
        String normalizedRoot = normalizeAbsolutePath(rootPath);
        if (normalizedPath.equals(normalizedRoot)) {
            return "";
        }
        if (!normalizedPath.startsWith(normalizedRoot + "/")) {
            CLog.e(TAG, "toPublicRelativePath unsupported path=" + publicDirectoryPath + ", root=" + rootPath);
            return null;
        }
        String relativePath = normalizedPath.substring(normalizedRoot.length() + 1);
        CLog.d(TAG, "toPublicRelativePath path=" + publicDirectoryPath + ", relativePath=" + relativePath);
        return relativePath;
    }

    private static String normalizeAbsolutePath(String path) {
        String normalized = path.replace('\\', '/');
        while (normalized.endsWith("/") && normalized.length() > 1) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
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

    public static class PublicSaveResult {

        private final boolean success;

        private final String path;

        private final android.net.Uri uri;

        private final String target;

        private final String errorMessage;

        private PublicSaveResult(boolean success, String path, android.net.Uri uri, String target, String errorMessage) {
            this.success = success;
            this.path = path;
            this.uri = uri;
            this.target = target;
            this.errorMessage = errorMessage;
        }

        public static PublicSaveResult success(String path, android.net.Uri uri, String target) {
            return new PublicSaveResult(true, path, uri, target, null);
        }

        public static PublicSaveResult fail(String errorMessage) {
            return new PublicSaveResult(false, null, null, null, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getPath() {
            return path;
        }

        public android.net.Uri getUri() {
            return uri;
        }

        public String getTarget() {
            return target;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
