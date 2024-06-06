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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;


public class CAnnotFreeTextPreviewView extends CBasicAnnotPreviewView {

    private int textColorOpacity = 255;

    private AppCompatTextView textView;

    private String fontPsName;

    public CAnnotFreeTextPreviewView(@NonNull Context context) {
        this(context, null);
    }

    public CAnnotFreeTextPreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CAnnotFreeTextPreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void bindView() {
        fontPsName = CPDFTextAttribute.FontNameHelper.FontType.Helvetica.name();
        textView = new AppCompatTextView(getContext());
        textView.setTextSize(24);
        textView.setText(R.string.tools_sample);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(textView, layoutParams);
    }

    @Override
    protected void initView() {
        textView.setTypeface(getTypeFace());
    }


    @Override
    public void setTextColor(int textColor) {
        textView.setTextColor(textColor);
    }

    @Override
    public void setTextColorOpacity(int textColorOpacity) {
        super.setTextColorOpacity(textColorOpacity);
        this.textColorOpacity = textColorOpacity;
        textView.setTextColor(textView.getTextColors().withAlpha(textColorOpacity));
    }

    private Typeface getTypeFace(){
        return CPDFTextAttribute.FontNameHelper.getTypeface(getContext(), fontPsName);
    }

    @Override
    public void setTextAlignment(CAnnotStyle.Alignment alignment) {
        super.setTextAlignment(alignment);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        switch (alignment) {
            case LEFT:
                layoutParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case CENTER:
                layoutParams.gravity = Gravity.CENTER;
                break;
            case RIGHT:
                layoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
            default:
                break;
        }
        textView.setLayoutParams(layoutParams);
    }

    @Override
    public void setFontSize(int fontSize) {
        super.setFontSize(fontSize);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize* 2F);
    }

    @Override
    public void setFontPsName(String fontPsName) {
        this.fontPsName = fontPsName;
        textView.setTypeface(getTypeFace());
    }
}
