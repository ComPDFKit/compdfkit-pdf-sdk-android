/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdffreetext;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.colorpicker.widget.ColorPickerView;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.font.CFontSpinnerAdapter;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.preview.CStylePreviewView;

import java.util.ArrayList;
import java.util.List;

public class CFreeTextStyleFragment extends CBasicPropertiesFragment implements View.OnClickListener,
        ColorPickerView.COnColorChangeListener,ColorPickerView.COnColorAlphaChangeListener {

    private CStylePreviewView previewView;

    private ColorListView colorListView;

    private CSliderBar opacitySliderBar;

    private AppCompatImageView ivFontItalic;

    private AppCompatImageView ivFontBold;

    private LinearLayout llAlignment;

    private AppCompatImageView ivAlignmentLeft;

    private AppCompatImageView ivAlignmentCenter;

    private AppCompatImageView ivAlignmentRight;

    private CSliderBar fontSizeSliderBar;

    private Spinner fontSpinner;

    private List<View> alignmentViews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_properties_free_text_style_fragment, container, false);
        previewView = rootView.findViewById(R.id.style_preview);
        colorListView = rootView.findViewById(R.id.border_color_list_view);
        opacitySliderBar = rootView.findViewById(R.id.slider_bar);
        ivFontItalic = rootView.findViewById(R.id.iv_font_italic);
        ivFontBold = rootView.findViewById(R.id.iv_font_bold);
        llAlignment = rootView.findViewById(R.id.ll_alignment_type);
        ivAlignmentLeft = rootView.findViewById(R.id.iv_alignment_left);
        ivAlignmentCenter = rootView.findViewById(R.id.iv_alignment_center);
        ivAlignmentRight = rootView.findViewById(R.id.iv_alignment_right);
        fontSizeSliderBar = rootView.findViewById(R.id.font_size_slider_bar);
        fontSpinner = rootView.findViewById(R.id.spinner_font);
        ivFontItalic.setOnClickListener(this);
        ivFontBold.setOnClickListener(this);
        ivAlignmentLeft.setOnClickListener(this);
        ivAlignmentCenter.setOnClickListener(this);
        ivAlignmentRight.setOnClickListener(this);
        alignmentViews.add(ivAlignmentLeft);
        alignmentViews.add(ivAlignmentCenter);
        alignmentViews.add(ivAlignmentRight);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle annotStyle = viewModel.getStyle();
        if (annotStyle != null) {
            previewView.setTextColor(annotStyle.getTextColor());
            previewView.setTextAlignment(annotStyle.getAlignment());
            previewView.setTextColorOpacity(annotStyle.getTextColorOpacity());
            previewView.setFontBold(annotStyle.isFontBold());
            previewView.setFontItalic(annotStyle.isFontItalic());
            previewView.setFontType(annotStyle.getFontType());

            List<CPDFTextAttribute.FontNameHelper.FontType> fontTypes = new ArrayList<>();
            fontTypes.add(CPDFTextAttribute.FontNameHelper.FontType.Helvetica);
            fontTypes.add(CPDFTextAttribute.FontNameHelper.FontType.Courier);
            fontTypes.add(CPDFTextAttribute.FontNameHelper.FontType.Times_Roman);
            CFontSpinnerAdapter fontSpinnerAdapter = new CFontSpinnerAdapter(getContext(), fontTypes);
            fontSpinner.setAdapter(fontSpinnerAdapter);
            switch (annotStyle.getFontType()) {
                case Courier:
                    fontSpinner.setSelection(1);
                    break;
                case Times_Roman:
                    fontSpinner.setSelection(2);
                    break;
                default:
                    fontSpinner.setSelection(0);
                    break;
            }
            colorListView.setSelectColor(annotStyle.getTextColor());
            opacitySliderBar.setProgress(annotStyle.getTextColorOpacity());
            ivFontBold.setSelected(annotStyle.isFontBold());
            ivFontItalic.setSelected(annotStyle.isFontItalic());
            switch (annotStyle.getAlignment()) {
                case LEFT:
                    selectAlignmentView(ivAlignmentLeft);
                    break;
                case CENTER:
                    selectAlignmentView(ivAlignmentCenter);
                    break;
                case RIGHT:
                    selectAlignmentView(ivAlignmentRight);
                    break;
                default:
                    break;
            }
            fontSizeSliderBar.setProgress(annotStyle.getFontSize());
        }
        colorListView.setOnColorSelectListener(this::color);
        colorListView.setColorPickerClickListener(() -> {
            CAnnotStyle cAnnotStyle = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment)->{
                colorPickerFragment.initColor(cAnnotStyle.getTextColor(), cAnnotStyle.getTextColorOpacity());
                colorPickerFragment.setColorPickerListener(this);
                colorPickerFragment.setColorAlphaChangeListener(this);
            });
        });
        opacitySliderBar.setChangeListener((progress, percentageValue, isStop) -> opacity(progress));
        fontSizeSliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (viewModel != null) {
                viewModel.getStyle().setFontSize(progress);
            }
        });
        viewModel.addStyleChangeListener(this);
        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (viewModel != null) {
                    CPDFTextAttribute.FontNameHelper.FontType fontType = (CPDFTextAttribute.FontNameHelper.FontType) fontSpinner.getItemAtPosition(position);
                    viewModel.getStyle().setFontType(fontType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void color(int color) {
        if (viewModel != null && viewModel.getStyle() != null) {
            viewModel.getStyle().setFontColor(color);
        }
    }

    @Override
    public void opacity(int opacity) {
        if (viewModel.getStyle() != null) {
            viewModel.getStyle().setTextColorOpacity(opacity);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_font_bold) {
            ivFontBold.setSelected(!ivFontBold.isSelected());
            if (viewModel != null) {
                viewModel.getStyle().setFontBold(ivFontBold.isSelected());
            }
        } else if (v.getId() == R.id.iv_font_italic) {
            ivFontItalic.setSelected(!ivFontItalic.isSelected());
            if (viewModel != null) {
                viewModel.getStyle().setFontItalic(ivFontItalic.isSelected());
            }
        } else if (v.getId() == R.id.iv_alignment_left) {
            selectAlignmentView(ivAlignmentLeft);
            setAlignment(CAnnotStyle.Alignment.LEFT);
        } else if (v.getId() == R.id.iv_alignment_center) {
            selectAlignmentView(ivAlignmentCenter);
            setAlignment(CAnnotStyle.Alignment.CENTER);
        } else if (v.getId() == R.id.iv_alignment_right) {
            selectAlignmentView(ivAlignmentRight);
            setAlignment(CAnnotStyle.Alignment.RIGHT);
        }
    }

    private void selectAlignmentView(AppCompatImageView alignmentView){
        for (View view : alignmentViews) {
            view.setSelected(view == alignmentView);
        }
    }

    private void setAlignment(CAnnotStyle.Alignment alignment){
        if (viewModel != null) {
            viewModel.getStyle().setAlignment(alignment);
        }
    }

    @Override
    public void onChangeTextColor(int textColor) {
        if (!isOnResume) {
            if (colorListView != null) {
                colorListView.setSelectColor(textColor);
            }
        }
        if (previewView != null) {
            previewView.setTextColor(textColor);
        }
    }

    @Override
    public void onChangeTextColorOpacity(int textColorOpacity) {
        if (!isOnResume) {
            if (colorListView != null) {
                opacitySliderBar.setProgress(textColorOpacity);
            }
        }
        if (previewView != null) {
            previewView.setTextColorOpacity(textColorOpacity);
        }
    }

    @Override
    public void onChangeFontType(CPDFTextAttribute.FontNameHelper.FontType fontType) {
        if (previewView != null) {
            previewView.setFontType(fontType);
        }
    }

    @Override
    public void onChangeFontBold(boolean bold) {
        if (previewView != null) {
            previewView.setFontBold(bold);
        }
    }

    @Override
    public void onChangeFontItalic(boolean italic) {
        if (previewView != null) {
            previewView.setFontItalic(italic);
        }
    }

    @Override
    public void onChangeFontSize(int fontSize) {
        if (previewView != null) {
            previewView.setFontSize(fontSize);
        }
    }

    @Override
    public void onChangeTextAlignment(CAnnotStyle.Alignment alignment) {
        if (previewView != null) {
            previewView.setTextAlignment(alignment);
        }
    }
}