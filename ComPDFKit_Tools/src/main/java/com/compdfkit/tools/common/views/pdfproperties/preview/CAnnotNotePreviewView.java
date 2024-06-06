/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;


import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;

import com.compdfkit.tools.R;

 class CAnnotNotePreviewView extends CBasicAnnotPreviewView {

    private AppCompatImageView ivPreview;

    public CAnnotNotePreviewView(@NonNull Context context) {
        super(context);
    }

     @Override
     protected void bindView() {
         inflate(getContext(), R.layout.tools_annot_preview_note, this);
     }

    @Override
    protected void initView() {
        ivPreview = findViewById(R.id.iv_preview_note);
    }

    @Override
    public void setColor(int color) {
        if (ivPreview != null) {
            ImageViewCompat.setImageTintList(ivPreview, ColorStateList.valueOf(color));
        }
    }

    @Override
    public void setOpacity(int colorOpacity) {

    }

}
