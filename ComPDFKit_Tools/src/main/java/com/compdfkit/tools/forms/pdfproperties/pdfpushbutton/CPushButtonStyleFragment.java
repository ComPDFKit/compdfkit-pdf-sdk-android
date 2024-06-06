/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfpushbutton;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.CPropertiesSwitchView;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.font.CFontSpinnerAdapter;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.textfields.CTextFieldsView;

import java.util.ArrayList;
import java.util.List;


public class CPushButtonStyleFragment extends CBasicPropertiesFragment implements View.OnClickListener{

    public static CPushButtonStyleFragment newInstance() {
        return new CPushButtonStyleFragment();
    }

    private CTextFieldsView textFieldsView;

    private AppCompatEditText etBtnText;

    private ColorListView borderColorListView;

    private ColorListView backgroundColorListView;

    private ColorListView textColorListView;

    private AppCompatImageView ivFontItalic;

    private AppCompatImageView ivFontBold;

    private CPropertiesSwitchView hideFormSwitch;

    private CSliderBar fontSizeSliderBar;

    private Spinner fontSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_properties_push_button_style_fragment, container, false);
        textFieldsView = rootView.findViewById(R.id.text_field_view);
        etBtnText = rootView.findViewById(R.id.et_btn_text);
        borderColorListView = rootView.findViewById(R.id.border_color_list_view);
        backgroundColorListView = rootView.findViewById(R.id.background_color_list_view);
        textColorListView = rootView.findViewById(R.id.text_color_list_view);
        ivFontItalic = rootView.findViewById(R.id.iv_font_italic);
        ivFontBold = rootView.findViewById(R.id.iv_font_bold);
        fontSizeSliderBar = rootView.findViewById(R.id.font_size_slider_bar);
        fontSpinner = rootView.findViewById(R.id.spinner_font);
        hideFormSwitch = rootView.findViewById(R.id.switch_hide_form);
        ivFontItalic.setOnClickListener(this);
        ivFontBold.setOnClickListener(this);
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
            textFieldsView.setText(annotStyle.getFormFieldName());
            borderColorListView.setSelectColor(annotStyle.getLineColor());
            backgroundColorListView.setSelectColor(annotStyle.getFillColor());
            textColorListView.setSelectColor(annotStyle.getTextColor());
            ivFontBold.setSelected(annotStyle.isFontBold());
            ivFontItalic.setSelected(annotStyle.isFontItalic());
            hideFormSwitch.setChecked(annotStyle.isHideForm());
            fontSizeSliderBar.setProgress(annotStyle.getFontSize());
            etBtnText.setText(annotStyle.getFormDefaultValue());
        }
        viewModel.addStyleChangeListener(this);
        borderColorListView.setOnColorSelectListener(color -> {
            if (viewModel != null) {
                viewModel.getStyle().setBorderColor(color);
            }
        });
        borderColorListView.setColorPickerClickListener(() -> {
            CAnnotStyle cAnnotStyle = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment) -> {
                colorPickerFragment.initColor(cAnnotStyle.getLineColor(), cAnnotStyle.getLineColorOpacity());
                colorPickerFragment.showAlphaSliderBar(false);
                colorPickerFragment.setColorPickerListener(color -> {
                    if (viewModel != null) {
                        viewModel.getStyle().setBorderColor(color);
                    }
                });
            });
        });
        backgroundColorListView.setOnColorSelectListener(color -> {
            if (viewModel != null) {
                viewModel.getStyle().setFillColor(color);
            }
        });
        backgroundColorListView.setColorPickerClickListener(() -> {
            CAnnotStyle cAnnotStyle = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment) -> {
                colorPickerFragment.initColor(cAnnotStyle.getFillColor(), cAnnotStyle.getFillColorOpacity());
                colorPickerFragment.showAlphaSliderBar(false);
                colorPickerFragment.setColorPickerListener(color -> {
                    if (viewModel != null) {
                        viewModel.getStyle().setFillColor(color);
                    }
                });
            });
        });
        textColorListView.setOnColorSelectListener(color -> {
            if (viewModel != null) {
                viewModel.getStyle().setFontColor(color);
            }
        });
        textColorListView.setColorPickerClickListener(() -> {
            CAnnotStyle cAnnotStyle = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment) -> {
                colorPickerFragment.initColor(cAnnotStyle.getTextColor(), cAnnotStyle.getTextColorOpacity());
                colorPickerFragment.showAlphaSliderBar(false);
                colorPickerFragment.setColorPickerListener(color -> {
                    if (viewModel != null) {
                        viewModel.getStyle().setFontColor(color);
                    }
                });
            });
        });
        fontSizeSliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (isStop) {
                if (viewModel != null) {
                    viewModel.getStyle().setFontSize(progress);
                }
            }
        });
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
        textFieldsView.setTextChangedListener((s, start, before, count) -> {
            if (viewModel != null) {
                if (TextUtils.isEmpty(s)){
                    viewModel.getStyle().setFormFieldName("");
                }else {
                    viewModel.getStyle().setFormFieldName(s.toString());
                }
            }
        });
        etBtnText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (viewModel != null){
                    if (TextUtils.isEmpty(s)){
                        viewModel.getStyle().setFormDefaultValue("");
                    }else {
                        viewModel.getStyle().setFormDefaultValue(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hideFormSwitch.setListener((buttonView, isChecked) -> {
            if (viewModel != null) {
                viewModel.getStyle().setHideForm(isChecked);
            }
        });
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
        } else {}
    }

    @Override
    public void onChangeTextColor(int textColor) {
        if (!isOnResume) {
            if (textColorListView != null) {
                textColorListView.setSelectColor(textColor);
            }
        }
    }

    @Override
    public void onChangeLineColor(int color) {
        if (!isOnResume) {
            if (borderColorListView != null) {
                borderColorListView.setSelectColor(color);
            }
        }
    }

    @Override
    public void onChangeFillColor(int color) {
        if (!isOnResume) {
            if (backgroundColorListView != null) {
                backgroundColorListView.setSelectColor(color);
            }
        }
    }
}
