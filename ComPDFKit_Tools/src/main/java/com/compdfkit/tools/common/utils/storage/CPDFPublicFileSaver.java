package com.compdfkit.tools.common.utils.storage;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.compdfkit.tools.common.utils.CLog;

import java.io.File;

/**
 * Saves files created by ComPDFKit SDK APIs to a user-selected public directory.
 *
 * <p>Most ComPDFKit SDK save APIs still write to a local file path. Use this helper when a
 * feature lets the user choose a public folder in {@code CFileDirectoryDialog}:</p>
 *
 * <pre>
 * CPDFPublicFileSaver.SaveResult result = CPDFPublicFileSaver.savePdfToSelectedDirectory(
 *     context,
 *     selectedDirectory,
 *     "Document.pdf",
 *     false,
 *     tempPath -> document.saveAs(tempPath, false)
 * );
 * </pre>
 *
 * <p>On Android 10+, the SDK writes to an app cache file first, then this helper publishes a
 * public copy through MediaStore. Prefer opening the saved file with {@link SaveResult#getPublicUri()}.
 * Set {@code keepLocalFileForOpen} to true only when existing code can open files by String path
 * but cannot yet accept Uri.</p>
 *
 * <p>On Android 5-9, this helper copies the temp file to the selected public folder and returns
 * that public file path as {@link SaveResult#getOpenPath()}.</p>
 */
public class CPDFPublicFileSaver {

    public static final String MIME_TYPE_PDF = "application/pdf";

    public static final String MIME_TYPE_XFDF = "application/vnd.adobe.xfdf";

    private static final String TAG = "CPDFStorage";

    private CPDFPublicFileSaver() {
    }

    public static SaveResult savePdfToSelectedDirectory(Context context,
                                                        String selectedDirectory,
                                                        String fileName,
                                                        boolean keepLocalFileForOpen,
                                                        FileWriter writer) {
        return savePdfToSelectedDirectory(context, selectedDirectory, null, fileName,
                keepLocalFileForOpen, writer);
    }

    public static SaveResult savePdfToSelectedDirectory(Context context,
                                                        String selectedDirectory,
                                                        String subDirectory,
                                                        String fileName,
                                                        boolean keepLocalFileForOpen,
                                                        FileWriter writer) {
        return saveToSelectedDirectory(context, selectedDirectory, subDirectory, fileName,
                MIME_TYPE_PDF, keepLocalFileForOpen, writer);
    }

    public static SaveResult saveXfdfToSelectedDirectory(Context context,
                                                         String selectedDirectory,
                                                         String fileName,
                                                         FileWriter writer) {
        return saveToSelectedDirectory(context, selectedDirectory, null, fileName,
                MIME_TYPE_XFDF, false, writer);
    }

    public static SaveResult saveToSelectedDirectory(Context context,
                                                     String selectedDirectory,
                                                     String subDirectory,
                                                     String fileName,
                                                     String mimeType,
                                                     boolean keepLocalFileForOpen,
                                                     FileWriter writer) {
        CLog.d(TAG, "saveToSelectedDirectory start selectedDirectory=" + selectedDirectory
                + ", subDirectory=" + subDirectory
                + ", fileName=" + fileName
                + ", mimeType=" + mimeType
                + ", keepLocalFileForOpen=" + keepLocalFileForOpen
                + ", sdk=" + Build.VERSION.SDK_INT);
        if (context == null) {
            CLog.e(TAG, "saveToSelectedDirectory fail context null");
            return SaveResult.fail("context is null");
        }
        if (TextUtils.isEmpty(selectedDirectory)) {
            CLog.e(TAG, "saveToSelectedDirectory fail selected directory empty");
            return SaveResult.fail("selected directory is empty");
        }
        if (TextUtils.isEmpty(fileName)) {
            CLog.e(TAG, "saveToSelectedDirectory fail fileName empty");
            return SaveResult.fail("file name is empty");
        }
        if (writer == null) {
            CLog.e(TAG, "saveToSelectedDirectory fail writer null");
            return SaveResult.fail("file writer is null");
        }
        String normalizedSubDir = normalizeSubDirectory(subDirectory);

        File tempFile = CPDFStorageManager.createTempFileForPublicSave(context, fileName);
        boolean writeSuccess = false;
        try {
            writeSuccess = writer.write(tempFile.getAbsolutePath());
        } catch (Exception e) {
            CLog.e(TAG, "saveToSelectedDirectory writer exception temp=" + fileInfo(tempFile)
                    + ", error=" + e.getMessage());
        }
        if (!writeSuccess || !tempFile.exists()) {
            CLog.e(TAG, "saveToSelectedDirectory writer failed temp=" + fileInfo(tempFile));
            deleteTempFile(tempFile);
            return SaveResult.fail("write temp file failed");
        }

        boolean deleteTempAfterPublish = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                || !keepLocalFileForOpen;
        CPDFStorageManager.PublicSaveResult publicSaveResult =
                CPDFStorageManager.publishTempFileToSelectedDirectory(context,
                        tempFile,
                        buildTargetDirectory(selectedDirectory, normalizedSubDir),
                        fileName,
                        mimeType,
                        deleteTempAfterPublish);
        if (!publicSaveResult.isSuccess()) {
            CLog.e(TAG, "saveToSelectedDirectory publish failed temp=" + fileInfo(tempFile)
                    + ", error=" + publicSaveResult.getErrorMessage());
            deleteTempFile(tempFile);
            return SaveResult.fail(publicSaveResult.getErrorMessage());
        }

        String openPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && keepLocalFileForOpen) {
            openPath = tempFile.getAbsolutePath();
        } else {
            openPath = publicSaveResult.getPath();
        }
        CLog.d(TAG, "saveToSelectedDirectory success openPath=" + openPath
                + ", publicPath=" + publicSaveResult.getPath()
                + ", publicUri=" + publicSaveResult.getUri()
                + ", publicTarget=" + publicSaveResult.getTarget());
        return SaveResult.success(openPath,
                publicSaveResult.getPath(),
                publicSaveResult.getUri(),
                publicSaveResult.getTarget());
    }

    private static String normalizeSubDirectory(String subDirectory) {
        if (TextUtils.isEmpty(subDirectory)) {
            return null;
        }
        String normalized = subDirectory.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static String buildTargetDirectory(String selectedDirectory, String normalizedSubDir) {
        if (TextUtils.isEmpty(normalizedSubDir)) {
            return selectedDirectory;
        }
        return new File(selectedDirectory, normalizedSubDir).getAbsolutePath();
    }

    private static void deleteTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            boolean deleted = tempFile.delete();
            CLog.d(TAG, "saveToSelectedDirectory delete temp=" + tempFile.getAbsolutePath()
                    + ", deleted=" + deleted);
        }
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

    public interface FileWriter {
        boolean write(String tempFilePath) throws Exception;
    }

    public static class SaveResult {

        private final boolean success;

        private final String openPath;

        private final String publicPath;

        private final Uri publicUri;

        private final String publicTarget;

        private final String errorMessage;

        private SaveResult(boolean success,
                           String openPath,
                           String publicPath,
                           Uri publicUri,
                           String publicTarget,
                           String errorMessage) {
            this.success = success;
            this.openPath = openPath;
            this.publicPath = publicPath;
            this.publicUri = publicUri;
            this.publicTarget = publicTarget;
            this.errorMessage = errorMessage;
        }

        public static SaveResult success(String openPath,
                                         String publicPath,
                                         Uri publicUri,
                                         String publicTarget) {
            return new SaveResult(true, openPath, publicPath, publicUri, publicTarget, null);
        }

        public static SaveResult fail(String errorMessage) {
            return new SaveResult(false, null, null, null, null, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        /**
         * Path suitable for Android 5-9 direct file opening, or an app cache path when keepLocalFileForOpen is true.
         */
        public String getOpenPath() {
            return openPath;
        }

        /**
         * Data path returned by MediaStore or the Android 5-9 public file path.
         */
        public String getPublicPath() {
            return publicPath;
        }

        /**
         * Uri of the public file created through MediaStore. May be null on Android 5-9.
         */
        public Uri getPublicUri() {
            return publicUri;
        }

        /**
         * Relative public directory on Android 10+, or target file path on Android 5-9.
         */
        public String getPublicTarget() {
            return publicTarget;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }
}
