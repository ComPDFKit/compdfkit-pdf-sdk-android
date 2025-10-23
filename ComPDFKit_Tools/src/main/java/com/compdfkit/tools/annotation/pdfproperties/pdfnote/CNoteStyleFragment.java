/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfnote;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.colorpicker.widget.ColorPickerView;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.preview.CStylePreviewView;

/**
 * Create a fragment for modifying the attributes of a note annotation in a PDF,
 * specifically for changing the color of the annotation icon.
 * This fragment should be used for annotations of type {@link CAnnotationType#TEXT}
 *
 * @see CAnnotationType#TEXT
 */
public class CNoteStyleFragment extends CBasicPropertiesFragment
        implements ColorPickerView.COnColorChangeListener,ColorPickerView.COnColorAlphaChangeListener {

    private ColorListView colorListView;

    private CStylePreviewView stylePreviewView;

    public static CNoteStyleFragment newInstance() {
        return new CNoteStyleFragment();
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_note_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        colorListView = rootView.findViewById(R.id.color_list_view);
        stylePreviewView = rootView.findViewById(R.id.style_preview);
        stylePreviewView.setAnnotType(CStyleType.ANNOT_TEXT);
        stylePreviewView.setColor(viewModel.getStyle().getColor());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel.getStyle() != null) {
            colorListView.setSelectColor(viewModel.getStyle().getColor());
        }
        //Add a listener to the color list that responds to color selection.
        colorListView.setOnColorSelectListener(this::color);
        //Add a listener for clicks on the color picker item, which will display the color picker fragment
        colorListView.setColorPickerClickListener(()->{
            //display the color picker fragment
            CAnnotStyle style = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment)->{
                colorPickerFragment.initColor(style.getColor(), style.getOpacity());
                colorPickerFragment.setColorPickerListener(this);
                colorPickerFragment.setColorAlphaChangeListener(this);
            });
        });
        viewModel.addStyleChangeListener(this);
    }

    @Override
    public void color(int color) {
        //Get the selected color from the ColorPickerFragment
        if (viewModel != null) {
            //Set the color attribute for the icon of a note annotation.
            viewModel.getStyle().setColor(color);
        }
    }

    @Override
    public void opacity(int opacity) {
        if (viewModel != null){
            viewModel.getStyle().setOpacity(opacity);
        }
    }

    @Override
    public void onChangeColor(int color) {
        if (!isOnResume) {
            //When the selected color changes are detected, update the selected color in the list
            if (colorListView != null) {
                colorListView.setSelectColor(color);
            }
        }
        //Update the preview color of the note annotation
        if (stylePreviewView != null) {
            stylePreviewView.setColor(color);
        }
    }

    @Override
    public void onChangeOpacity(int opacity) {
        super.onChangeOpacity(opacity);
        //Update the preview color opacity of the note annotation
        if (stylePreviewView != null) {
            stylePreviewView.setColorOpacity(opacity);
        }
    }
}
