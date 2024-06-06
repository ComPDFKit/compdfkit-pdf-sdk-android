/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfmarkup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class CMarkupStyleFragment extends CBasicPropertiesFragment implements ColorPickerView.COnColorChangeListener,ColorPickerView.COnColorAlphaChangeListener {

    private ColorListView colorListView;

    private CSliderBar colorOpacitySliderBar;

    private CStylePreviewView previewView;

    public static CMarkupStyleFragment newInstance() {
        return new CMarkupStyleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_properties_markup_style_fragment, container, false);
        colorListView = rootView.findViewById(R.id.color_list_view);
        colorOpacitySliderBar = rootView.findViewById(R.id.slider_bar);
        previewView = rootView.findViewById(R.id.style_preview);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Initialize the preview attributes for a style.
        previewView.setAnnotType(viewModel.getStyle().getType());
        previewView.setColor(viewModel.getStyle().getColor());
        previewView.setColorOpacity(viewModel.getStyle().getOpacity());
        //Set the current color opacity to the slider bar
        colorOpacitySliderBar.setProgress(viewModel.getStyle().getOpacity());
        //Add a listener for color list
        colorListView.setSelectColor(viewModel.getStyle().getColor());
        colorListView.setOnColorSelectListener(this::color);
        //Add a click listener for the color picker button
        colorListView.setColorPickerClickListener(()->{
            //show color picker fragment and set color change listener
            CAnnotStyle style = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment)->{
                colorPickerFragment.initColor(style.getColor(), style.getOpacity());
                colorPickerFragment.setColorPickerListener(this);
                colorPickerFragment.setColorAlphaChangeListener(this);
            });
        });
        //Add a listener for color opacity changes
        colorOpacitySliderBar.setChangeListener((opacity, percentageValue, isStop) ->{
            opacity(opacity);
        });
        colorListView.showColorPicker(true);
        viewModel.addStyleChangeListener(this);
    }

    @Override
    public void color(int color) {
        //Get the selected color from the ColorPickerFragment
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
        if (!isOnResume){
            //When the selected color changes are detected, update the selected color in the list
            if (colorListView != null) {
                colorListView.setSelectColor(color);
            }
        }
        //Update the preview color of the note annotation
        if (previewView != null) {
            previewView.setColor(color);
        }
    }

    @Override
    public void onChangeOpacity(int opacity) {
        super.onChangeOpacity(opacity);
        if (!isOnResume){
            if (colorOpacitySliderBar != null) {
                colorOpacitySliderBar.setProgress(opacity);
            }
        }
        //Update the preview color opacity of the note annotation
        if (previewView != null) {
            previewView.setColorOpacity(opacity);
        }
    }
}
