/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdflistbox;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.CPropertiesSwitchView;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.font.CPDFFontView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.textfields.CTextFieldsView;


public class CListBoxStyleFragment extends CBasicPropertiesFragment implements View.OnClickListener{

    public static CListBoxStyleFragment newInstance() {
        return new CListBoxStyleFragment();
    }

    private CTextFieldsView textFieldsView;

    private ColorListView borderColorListView;

    private ColorListView backgroundColorListView;

    private ColorListView textColorListView;

    private CPropertiesSwitchView hideFormSwitch;

    private CSliderBar fontSizeSliderBar;

    private CPDFFontView fontView;

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_list_box_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        textFieldsView = rootView.findViewById(R.id.text_field_view);
        borderColorListView = rootView.findViewById(R.id.border_color_list_view);
        backgroundColorListView = rootView.findViewById(R.id.background_color_list_view);
        textColorListView = rootView.findViewById(R.id.text_color_list_view);
        fontView = rootView.findViewById(R.id.font_view);
        fontSizeSliderBar = rootView.findViewById(R.id.font_size_slider_bar);
        hideFormSwitch = rootView.findViewById(R.id.switch_hide_form);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle annotStyle = viewModel.getStyle();
        if (annotStyle != null) {
            fontView.initFont(annotStyle.getExternFontName());
            textFieldsView.setText(annotStyle.getFormFieldName());
            borderColorListView.setSelectColor(annotStyle.getLineColor());
            backgroundColorListView.setSelectColor(annotStyle.getFillColor());
            textColorListView.setSelectColor(annotStyle.getTextColor());
            hideFormSwitch.setChecked(annotStyle.isHideForm());
            fontSizeSliderBar.setProgress(annotStyle.getFontSize());
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
        fontView.setFontChangeListener(psName -> {
            if (viewModel != null) {
                viewModel.getStyle().setExternFontName(psName);
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
        hideFormSwitch.setListener((buttonView, isChecked) -> {
            if (viewModel != null) {
                viewModel.getStyle().setHideForm(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {

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
