/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfcheckbox;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.views.pdfproperties.CPropertiesSwitchView;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.textfields.CTextFieldsView;


public class CheckBoxStyleFragment extends CBasicPropertiesFragment implements View.OnClickListener {

    public static CheckBoxStyleFragment newInstance() {
        return new CheckBoxStyleFragment();
    }

    private CTextFieldsView textFieldsView;

    private ColorListView borderColorListView;

    private ColorListView backgroundColorListView;

    private ColorListView checkColorListView;

    private CPropertiesSwitchView hideFormSwitch;

    private CPropertiesSwitchView presetSelectSwitch;

    private ConstraintLayout clCheckBoxItem;

    private AppCompatImageView ivCheckBox;

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_check_box_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        textFieldsView = rootView.findViewById(R.id.text_field_view);
        borderColorListView = rootView.findViewById(R.id.border_color_list_view);
        backgroundColorListView = rootView.findViewById(R.id.background_color_list_view);
        checkColorListView = rootView.findViewById(R.id.check_color_list_view);
        hideFormSwitch = rootView.findViewById(R.id.switch_hide_form);
        presetSelectSwitch = rootView.findViewById(R.id.switch_preset_to_selected);
        clCheckBoxItem = rootView.findViewById(R.id.cl_check_box_type_item);
        ivCheckBox = rootView.findViewById(R.id.iv_check_box);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle annotStyle = viewModel.getStyle();
        if (annotStyle != null) {
            textFieldsView.setText(annotStyle.getFormFieldName());
            borderColorListView.setSelectColor(annotStyle.getLineColor());
            backgroundColorListView.setSelectColor(annotStyle.getFillColor());
            checkColorListView.setSelectColor(annotStyle.getColor());
            hideFormSwitch.setChecked(annotStyle.isHideForm());
            presetSelectSwitch.setChecked(annotStyle.isChecked());
            updateCheckStylePreview(annotStyle.getCheckStyle());
        }
        clCheckBoxItem.setOnClickListener(this);
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
        checkColorListView.setOnColorSelectListener(color -> {
            if (viewModel != null) {
                viewModel.getStyle().setColor(color);
            }
        });
        checkColorListView.setColorPickerClickListener(() -> {
            CAnnotStyle cAnnotStyle = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment) -> {
                colorPickerFragment.initColor(cAnnotStyle.getColor(), cAnnotStyle.getOpacity());
                colorPickerFragment.showAlphaSliderBar(false);
                colorPickerFragment.setColorPickerListener(color -> {
                    if (viewModel != null) {
                        viewModel.getStyle().setColor(color);
                    }
                });
            });
        });
        textFieldsView.setTextChangedListener((s, start, before, count) -> {
            if (viewModel != null) {
                if (TextUtils.isEmpty(s)) {
                    viewModel.getStyle().setFormFieldName("");
                } else {
                    viewModel.getStyle().setFormFieldName(s.toString());
                }
            }
        });
        hideFormSwitch.setListener((buttonView, isChecked) -> {
            if (viewModel != null) {
                viewModel.getStyle().setHideForm(isChecked);
            }
        });
        presetSelectSwitch.setListener((buttonView, isChecked) -> {
            if (viewModel != null) {
                viewModel.getStyle().setChecked(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cl_check_box_type_item) {
            if (viewModel != null) {
                showFragment(CStyleFragmentDatas.checkBoxStyle(), (CheckBoxStyleListFragment checkBoxStyleListFragment) ->{
                    checkBoxStyleListFragment.setCheckStyle(viewModel.getStyle().getCheckStyle());
                    checkBoxStyleListFragment.setSelectCheckBoxStyleListener(style ->
                            viewModel.getStyle().setCheckStyle(style));
                });
            }
        }
    }

    private void updateCheckStylePreview(CPDFWidget.CheckStyle checkStyle) {
        switch (checkStyle) {
            case CK_Check:
                ivCheckBox.setImageResource(R.drawable.tools_ic_check_box_check);
                break;
            case CK_Circle:
                ivCheckBox.setImageResource(R.drawable.tools_ic_check_box_circle);
                break;
            case CK_Cross:
                ivCheckBox.setImageResource(R.drawable.tools_ic_check_box_cross);
                break;
            case CK_Diamond:
                ivCheckBox.setImageResource(R.drawable.tools_ic_check_box_diamond);
                break;
            case CK_Square:
                ivCheckBox.setImageResource(R.drawable.tools_ic_check_box_square);
                break;
            case CK_Star:
                ivCheckBox.setImageResource(R.drawable.tools_ic_check_box_star);
                break;
            default:
                break;
        }
    }

    @Override
    public void onChangeColor(int color) {
        if (!isOnResume) {
            if (checkColorListView != null) {
                checkColorListView.setSelectColor(color);
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

    @Override
    public void onChangeCheckStyle(CPDFWidget.CheckStyle checkStyle) {
        if (!isOnResume){
            updateCheckStylePreview(checkStyle);
        }
    }
}
