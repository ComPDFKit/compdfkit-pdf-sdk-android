/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdflnk;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.colorpicker.widget.ColorPickerView;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.preview.CStylePreviewView;

public class CInkStyleFragment extends CBasicPropertiesFragment implements ColorPickerView.COnColorChangeListener,
        ColorPickerView.COnColorAlphaChangeListener {

    private ColorListView colorListView;

    private CSliderBar colorOpacitySliderBar;

    private CStylePreviewView stylePreviewView;

    private CSliderBar borderWidthSliderBar;

    public static CInkStyleFragment newInstance() {
        return new CInkStyleFragment();
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_ink_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        colorListView = rootView.findViewById(R.id.color_list_view);
        stylePreviewView = rootView.findViewById(R.id.style_preview);
        colorOpacitySliderBar = rootView.findViewById(R.id.slider_bar);
        borderWidthSliderBar = rootView.findViewById(R.id.slider_bar_border_width);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle cAnnotStyle = viewModel.getStyle();
        if (cAnnotStyle != null) {
            //Initialize the preview attributes for a style.
            stylePreviewView.setColor(cAnnotStyle.getColor());
            stylePreviewView.setColorOpacity(cAnnotStyle.getOpacity());
            stylePreviewView.setBorderWidth((int) cAnnotStyle.getBorderWidth());
            borderWidthSliderBar.setProgress((int) cAnnotStyle.getBorderWidth());
            colorListView.setSelectColor(cAnnotStyle.getColor());
            colorOpacitySliderBar.setProgress(cAnnotStyle.getOpacity());
        }
        //Add a listener for clicks on the color picker item, which will display the color picker fragment
        colorListView.setColorPickerClickListener(() -> {
            //display the color picker fragment
            CAnnotStyle style = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment) -> {
                colorPickerFragment.initColor(style.getColor(), style.getOpacity());
                colorPickerFragment.setColorPickerListener(this);
                colorPickerFragment.setColorAlphaChangeListener(this);
            });
        });
        //Add a listener for color opacity changes
        colorOpacitySliderBar.setChangeListener((opacity, percentageValue, isStop) -> opacity(opacity));
        //Add a listener for color list
        colorListView.setOnColorSelectListener(this::color);
        //Add a listener for the border width progress bar changes
        borderWidthSliderBar.setChangeListener((borderWidth, percentageValue, isStop) -> {
            if (viewModel != null) {
                viewModel.getStyle().setBorderWidth(borderWidth);
            }
        });
        viewModel.addStyleChangeListener(this);
    }

    @Override
    public void color(int color) {
        if (viewModel != null) {
            viewModel.getStyle().setColor(color);
        }
    }

    @Override
    public void opacity(int opacity) {
        if (viewModel != null) {
            viewModel.getStyle().setOpacity(opacity);
        }
    }

    @Override
    public void onChangeColor(int color) {
        //Update the preview color of the note annotation
        if (stylePreviewView != null) {
            stylePreviewView.setColor(color);
        }
    }

    @Override
    public void onChangeOpacity(int opacity) {
        //Update the preview color of the note annotation
        if (stylePreviewView != null) {
            stylePreviewView.setColorOpacity(opacity);
        }
        if (!isOnResume) {
            if (colorOpacitySliderBar != null) {
                colorOpacitySliderBar.setProgress(opacity);
            }
        }
    }

    @Override
    public void onChangeBorderWidth(float borderWidth) {
        //Update the preview color of the note annotation
        if (stylePreviewView != null) {
            stylePreviewView.setBorderWidth((int) borderWidth);
        }
    }
}
