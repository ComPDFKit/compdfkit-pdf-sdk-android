/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.colorpicker;

import android.graphics.Color;
import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.widget.ColorPickerView;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;


public class CColorPickerFragment extends CBasicPropertiesFragment {

    public static CColorPickerFragment newInstance() {
        return new CColorPickerFragment();
    }

    private ColorPickerView colorPickerView;

    private int mSetColor = Color.BLACK;

    private int mSetColorOpacity = 255;

    private ColorPickerView.COnColorChangeListener colorChangeListener;

    private ColorPickerView.COnColorAlphaChangeListener colorAlphaChangeListener;

    @Override
    protected int layoutId() {
        return R.layout.tools_color_pick_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        colorPickerView = rootView.findViewById(R.id.color_picker_view);
        colorPickerView.initColor(mSetColor, mSetColorOpacity);
        colorPickerView.setColorChangeListener(color -> {
            if (colorChangeListener != null) {
                colorChangeListener.color(color);
            }
        });
        colorPickerView.setColorAlphaChangeListener(opacity -> {
            if (colorAlphaChangeListener != null) {
                colorAlphaChangeListener.opacity(opacity);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (colorPickerView != null && viewModel != null && viewModel.getStyle() != null) {
            int color = viewModel.getStyle().getColor();
            int alpha = viewModel.getStyle().getOpacity();
            colorPickerView.initColor(color, alpha);
        }
    }

    public void initColor(int color, int opacity){
        this.mSetColor = color;
        this.mSetColorOpacity = opacity;
        if (colorPickerView != null) {
            colorPickerView.initColor(mSetColor, mSetColorOpacity);
        }
    }

    public void showAlphaSliderBar(boolean show){
        if (colorPickerView != null) {
            colorPickerView.setShowAlphaSliderBar(show);
        }
    }

    public void setColorPickerListener(ColorPickerView.COnColorChangeListener colorChangeListener) {
        this.colorChangeListener = colorChangeListener;
    }

    public void setColorAlphaChangeListener(ColorPickerView.COnColorAlphaChangeListener colorAlphaChangeListener) {
        this.colorAlphaChangeListener = colorAlphaChangeListener;
    }
}
