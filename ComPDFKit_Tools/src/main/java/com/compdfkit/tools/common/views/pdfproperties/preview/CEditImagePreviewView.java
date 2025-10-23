/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.glide.transformation.RotateTransformation;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;

public class CEditImagePreviewView extends CBasicAnnotPreviewView {

    private AppCompatImageView imageView;

    public CEditImagePreviewView(@NonNull Context context) {
        this(context, null);
    }

    public CEditImagePreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CEditImagePreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void bindView() {
        imageView = new AppCompatImageView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        imageView.setImageResource(R.drawable.tools_edit_image_property_preview);
        addView(imageView, layoutParams);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void setOpacity(int opacity) {
        super.setOpacity(opacity);
        if (imageView != null) {
            imageView.setAlpha(opacity / 255F);
        }
    }

    private float rotationAngle = 0;

    @Override
    public void setRotationAngle(float angle) {
        if (imageView != null) {
            rotationAngle = (rotationAngle + angle) % 360;
            if (rotationAngle < 0) {
                rotationAngle += 360;
            }
            reloadBitmap();
        }
    }

    private boolean horizontalMirror = false;

    private boolean verticalMirror = false;

    @Override
    public void setMirror(CAnnotStyle.Mirror mirror) {
        if (imageView != null) {
            if (mirror == CAnnotStyle.Mirror.Horizontal) {
                horizontalMirror = !horizontalMirror;
                imageView.setScaleX(horizontalMirror ? -1 : 1);
            } else {
                verticalMirror = !verticalMirror;
                imageView.setScaleY(verticalMirror ? -1 : 1);
            }
        }
    }

    private Bitmap bitmap;

    @Override
    public void setImage(Bitmap bitmap) {
        super.setImage(bitmap);
        this.bitmap = bitmap;
        reloadBitmap();
    }

    private void reloadBitmap() {
        if (bitmap != null) {
            Glide.with(this)
                    .load(bitmap)
                    .apply(RequestOptions.bitmapTransform(new RotateTransformation(rotationAngle)))
                    .into(imageView);
        }
    }

}
