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
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.span.CWavyUnderLineSpan;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;

class CAnnotMarkupPreviewView extends CBasicAnnotPreviewView {

    private CStyleType styleType = CStyleType.ANNOT_HIGHLIGHT;

    private AppCompatTextView tvSample;

    private View viewUnderline;

    private View viewStrikeout;

    private int color = Color.TRANSPARENT;

    private int colorOpacity = 255;

    private StrikethroughSpan strikethroughSpan = new StrikethroughSpan();

    private BackgroundColorSpan backgroundColorSpan;

    private CWavyUnderLineSpan wavyUnderLineSpan;

    private SpannableString spannableString;

    public CAnnotMarkupPreviewView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void bindView() {
        inflate(getContext(), R.layout.tools_annot_preview_markup, this);
    }

    @Override
    protected void initView() {
        tvSample = findViewById(R.id.tv_preview_markup);
        viewUnderline = findViewById(R.id.view_under_line);
        viewStrikeout = findViewById(R.id.view_strikeout_line);
        spannableString = new SpannableString(getContext().getString(R.string.tools_sample));
        backgroundColorSpan = new BackgroundColorSpan(color);
        wavyUnderLineSpan = new CWavyUnderLineSpan(color, colorOpacity, 3);
    }

    @Override
    public void setColor(int color) {
        this.color = Color.argb(colorOpacity, Color.red(color), Color.green(color), Color.blue(color));
        updateStyle();
    }

    @Override
    public void setOpacity(int colorOpacity) {
        this.colorOpacity = colorOpacity;
        this.color = Color.argb(colorOpacity, Color.red(color), Color.green(color), Color.blue(color));
        updateStyle();
    }

    public void setMarkupType(CStyleType type){
        this.styleType = type;
        updateStyle();
    }

    private void updateStyle(){
        switch(styleType){
            case ANNOT_HIGHLIGHT:
                backgroundColorSpan = new BackgroundColorSpan(color);
                spannableString.setSpan(backgroundColorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSample.setText(spannableString);
                break;
            case ANNOT_UNDERLINE:
                tvSample.setText(spannableString);
                viewUnderline.setVisibility(VISIBLE);
                viewUnderline.setBackgroundColor(color);
                break;
            case ANNOT_SQUIGGLY:
                spannableString.removeSpan(wavyUnderLineSpan);
                wavyUnderLineSpan = new CWavyUnderLineSpan(color, colorOpacity, 5);
                spannableString.setSpan(wavyUnderLineSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSample.setText(spannableString);
                break;
            case ANNOT_STRIKEOUT:
                tvSample.setText(spannableString);
                viewStrikeout.setVisibility(VISIBLE);
                viewStrikeout.setBackgroundColor(color);
                break;
            default:break;
        }
    }

}
