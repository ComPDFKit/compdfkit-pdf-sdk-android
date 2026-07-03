package com.compdfkit.tools.common.utils.storage;

import android.os.Environment;
import android.text.TextUtils;

import com.compdfkit.tools.common.utils.CFileUtils;

public class CPDFStorageConfig {

    private final String cacheFolderName;

    private final String downloadRelativePath;

    private final String imageRelativePath;

    private final String certificateRelativePath;

    private CPDFStorageConfig(Builder builder) {
        this.cacheFolderName = normalizeCacheFolder(builder.cacheFolderName);
        this.downloadRelativePath = normalizeRelativePath(builder.downloadRelativePath);
        this.imageRelativePath = normalizeRelativePath(builder.imageRelativePath);
        this.certificateRelativePath = normalizeRelativePath(builder.certificateRelativePath);
    }

    public String getCacheFolderName() {
        return cacheFolderName;
    }

    public String getDownloadRelativePath() {
        return downloadRelativePath;
    }

    public String getImageRelativePath() {
        return imageRelativePath;
    }

    public String getCertificateRelativePath() {
        return certificateRelativePath;
    }

    public static CPDFStorageConfig getDefault() {
        return new Builder().build();
    }

    private static String normalizeCacheFolder(String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            return CFileUtils.CACHE_FOLDER;
        }
        return trimSlashes(folderName);
    }

    private static String normalizeRelativePath(String relativePath) {
        if (TextUtils.isEmpty(relativePath)) {
            return Environment.DIRECTORY_DOWNLOADS + "/ComPDFKit";
        }
        return trimSlashes(relativePath);
    }

    private static String trimSlashes(String value) {
        String result = value.trim().replace('\\', '/');
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static class Builder {

        private String cacheFolderName = CFileUtils.CACHE_FOLDER;

        private String downloadRelativePath = Environment.DIRECTORY_DOWNLOADS + "/ComPDFKit";

        private String imageRelativePath = Environment.DIRECTORY_DOWNLOADS + "/ComPDFKit/ImageExports";

        private String certificateRelativePath = Environment.DIRECTORY_DOWNLOADS + "/ComPDFKit/Certificates";

        public Builder setCacheFolderName(String cacheFolderName) {
            this.cacheFolderName = cacheFolderName;
            return this;
        }

        public Builder setDownloadRelativePath(String downloadRelativePath) {
            this.downloadRelativePath = downloadRelativePath;
            return this;
        }

        public Builder setImageRelativePath(String imageRelativePath) {
            this.imageRelativePath = imageRelativePath;
            return this;
        }

        public Builder setCertificateRelativePath(String certificateRelativePath) {
            this.certificateRelativePath = certificateRelativePath;
            return this;
        }

        public CPDFStorageConfig build() {
            return new CPDFStorageConfig(this);
        }
    }
}
