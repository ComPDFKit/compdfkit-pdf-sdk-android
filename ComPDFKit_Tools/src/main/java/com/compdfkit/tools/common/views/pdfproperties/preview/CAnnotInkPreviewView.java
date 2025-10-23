/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.preview;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.common.utils.view.CSineCurveView;

public class CAnnotInkPreviewView extends CBasicAnnotPreviewView{

    private CSineCurveView sineCurveView;

    public CAnnotInkPreviewView(@NonNull Context context) {
        super(context);
    }

    public CAnnotInkPreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CAnnotInkPreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void bindView() {
        sineCurveView = new CSineCurveView(getContext());
        addView(sineCurveView);
    }


    @Override
    protected void initView() {

    }

    @Override
    public void setColor(int color) {
        if (sineCurveView != null) {
            sineCurveView.setLineColor(color);
        }
    }

    @Override
    public void setOpacity(int colorOpacity) {
        if (sineCurveView != null) {
            sineCurveView.setLineAlpha(colorOpacity);
        }
    }

    @Override
    public void setBorderWidth(int borderWidth) {
        if (sineCurveView != null){
            sineCurveView.setBorderWidth(borderWidth);
        }
    }
}
