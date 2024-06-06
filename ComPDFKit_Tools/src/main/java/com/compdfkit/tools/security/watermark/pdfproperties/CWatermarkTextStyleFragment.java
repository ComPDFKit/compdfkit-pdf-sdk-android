/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.security.watermark.pdfproperties;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CWatermarkTextStyleFragment extends CBasicPropertiesFragment implements View.OnClickListener,
        ColorPickerView.COnColorChangeListener,ColorPickerView.COnColorAlphaChangeListener {

    private ColorListView colorListView;

    private CSliderBar opacitySliderBar;

    private AppCompatImageView ivFontItalic;

    private AppCompatImageView ivFontBold;

    private CSliderBar fontSizeSliderBar;

    private Spinner fontSpinner;

    private Spinner pageRangeSpinner;

    private SwitchCompat swTile;

    private AppCompatImageView ivLocationTop;

    private AppCompatImageView ivLocationBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_cpdf_security_watermark_text_style_fragment, container, false);
        colorListView = rootView.findViewById(R.id.border_color_list_view);
        opacitySliderBar = rootView.findViewById(R.id.slider_bar);
        ivFontItalic = rootView.findViewById(R.id.iv_font_italic);
        ivFontBold = rootView.findViewById(R.id.iv_font_bold);
        fontSizeSliderBar = rootView.findViewById(R.id.font_size_slider_bar);
        fontSpinner = rootView.findViewById(R.id.spinner_font);
        swTile = rootView.findViewById(R.id.sw_tile);
        pageRangeSpinner = rootView.findViewById(R.id.spinner_page_range);
        ivLocationTop = rootView.findViewById(R.id.iv_location_top);
        ivLocationBottom = rootView.findViewById(R.id.iv_location_bottom);
        ivFontItalic.setOnClickListener(this);
        ivFontBold.setOnClickListener(this);
        ivLocationTop.setOnClickListener(this);
        ivLocationBottom.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle annotStyle = viewModel.getStyle();
        if (annotStyle != null) {
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
            Map<String, Object> extraMap = annotStyle.getCustomExtraMap();
            CPageRange pageRange = CPageRange.AllPages;
            if (extraMap.containsKey("pageRange")){
                pageRange = CPageRange.valueOf((String) extraMap.get("pageRange"));
            }
            // init pageRange spinner adapter
            List<CPageRange> pageRanges = Arrays.asList(CPageRange.AllPages, CPageRange.CurrentPage);
            CWatermarkPageRangeAdapter pageRangeAdapter = new CWatermarkPageRangeAdapter(getContext(), pageRanges, pageRange);
            pageRangeSpinner.setAdapter(pageRangeAdapter);
            pageRangeSpinner.setSelection(pageRangeAdapter.getSelectItemIndex());
            pageRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CPageRange pageRange = pageRangeAdapter.list.get(position);
                    pageRangeAdapter.setSelectItem(pageRange);
                    if (viewModel != null && viewModel.getStyle() != null) {
                        Map<String, Object> extraMap = viewModel.getStyle().getCustomExtraMap();
                        extraMap.put("pageRange", pageRange.name());
                        viewModel.getStyle().setCustomExtraMap(extraMap);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            colorListView.setSelectColor(annotStyle.getTextColor());
            opacitySliderBar.setProgress(annotStyle.getTextColorOpacity());
            ivFontBold.setSelected(annotStyle.isFontBold());
            ivFontItalic.setSelected(annotStyle.isFontItalic());
            fontSizeSliderBar.setProgress(annotStyle.getFontSize());
            fontSizeSliderBar.setSliderBarMinValue(10);
            swTile.setChecked(annotStyle.isChecked());
            boolean isFront = true;
            if (extraMap.containsKey("front")){
                isFront = (boolean) extraMap.get("front");
            }
            ivLocationTop.setSelected(isFront);
            ivLocationBottom.setSelected(!isFront);
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
        swTile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (viewModel != null && viewModel.getStyle() != null) {
                viewModel.getStyle().setChecked(isChecked);
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
        } else if (v.getId() == R.id.iv_location_top) {
            ivLocationTop.setSelected(true);
            ivLocationBottom.setSelected(false);
            if (viewModel != null && viewModel.getStyle() != null) {
                Map<String, Object> extraMap = viewModel.getStyle().getCustomExtraMap();
                extraMap.put("front", true);
                viewModel.getStyle().setCustomExtraMap(extraMap);
            }
        } else if (v.getId() == R.id.iv_location_bottom) {
            ivLocationTop.setSelected(false);
            ivLocationBottom.setSelected(true);
            if (viewModel != null && viewModel.getStyle() != null) {
                Map<String, Object> extraMap = viewModel.getStyle().getCustomExtraMap();
                extraMap.put("front", false);
                viewModel.getStyle().setCustomExtraMap(extraMap);
            }
        }
    }

    @Override
    public void onChangeTextColor(int textColor) {
        if (!isOnResume) {
            if (colorListView != null) {
                colorListView.setSelectColor(textColor);
            }
        }
    }

    @Override
    public void onChangeTextColorOpacity(int textColorOpacity) {
        if (!isOnResume) {
            if (colorListView != null) {
                opacitySliderBar.setProgress(textColorOpacity);
            }
        }
    }
}
