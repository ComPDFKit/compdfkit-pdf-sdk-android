package com.compdfkit.tools.common.utils.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Key;

import java.security.MessageDigest;

final class CPDFCacheKey implements Key {

    private final String cacheKey;

    @Nullable
    private volatile byte[] cacheKeyBytes;

    private int hashCode;

    CPDFCacheKey(@NonNull String sourceKey, int width, int height) {
        this.cacheKey = sourceKey + "_" + width + "_" + height;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(getCacheKeyBytes());
    }

    @NonNull
    private byte[] getCacheKeyBytes() {
        if (cacheKeyBytes == null) {
            cacheKeyBytes = cacheKey.getBytes(CHARSET);
        }
        return cacheKeyBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CPDFCacheKey) {
            CPDFCacheKey other = (CPDFCacheKey) o;
            return cacheKey.equals(other.cacheKey);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = cacheKey.hashCode();
        }
        return hashCode;
    }
}
