/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.utils.glide.transformation;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class RotateTransformation extends BitmapTransformation {

    private static final byte[] ID_BYTES =
            "com.compdfkit.tools.common.utils.glide.transformation.RotateTransformation".getBytes(CHARSET);

    private final float rotateRotationAngle;

    public RotateTransformation(float rotateRotationAngle) {
        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        messageDigest.update(ByteBuffer.allocate(4).putFloat(rotateRotationAngle).array());
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (rotateRotationAngle == 0f) {
            return toTransform;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(-rotateRotationAngle);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RotateTransformation) {
            RotateTransformation other = (RotateTransformation) o;
            return Float.compare(other.rotateRotationAngle, rotateRotationAngle) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(rotateRotationAngle);
    }
}
