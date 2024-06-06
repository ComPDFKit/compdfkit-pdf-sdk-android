/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;

public class CEditImagePreviewView extends CBasicAnnotPreviewView{

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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

    @Override
    public void setRotationAngle(float angle) {
        if (imageView != null) {
            imageView.setPivotX(imageView.getWidth() / 2F);
            imageView.setPivotY(imageView.getHeight() / 2F);
            if (angle < 0){
                imageView.setRotation(imageView.getRotation() - Math.abs(angle));
            }else {
                imageView.setRotation(imageView.getRotation() + angle);
            }
        }
    }

    private boolean horizontalMirror = false;

    private boolean verticalMirror = false;

    @Override
    public void setMirror(CAnnotStyle.Mirror mirror) {
        if (imageView != null) {
            if (mirror == CAnnotStyle.Mirror.Horizontal){
                horizontalMirror = !horizontalMirror;
                imageView.setScaleX(horizontalMirror ? -1 : 1);
            }else {
                verticalMirror = !verticalMirror;
                imageView.setScaleY(verticalMirror ? -1 : 1);
            }
        }
    }
}
