/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdftextfield;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.utils.viewutils.EditTextUtils;
import com.compdfkit.tools.common.views.pdfproperties.CPropertiesSwitchView;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.font.CPDFFontView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.textfields.CTextFieldsView;

import java.util.ArrayList;
import java.util.List;


public class CTextFieldStyleFragment extends CBasicPropertiesFragment implements View.OnClickListener {

    public static CTextFieldStyleFragment newInstance() {
        return new CTextFieldStyleFragment();
    }

    private CTextFieldsView textFieldsView;

    private AppCompatEditText etDefaultValue;

    private ColorListView borderColorListView;

    private ColorListView backgroundColorListView;

    private ColorListView textColorListView;

    private LinearLayout llAlignment;

    private AppCompatImageView ivAlignmentLeft;

    private AppCompatImageView ivAlignmentCenter;

    private AppCompatImageView ivAlignmentRight;

    private CPropertiesSwitchView hideFormSwitch;

    private CPropertiesSwitchView multiLineSwitch;

    private CSliderBar fontSizeSliderBar;

    private CPDFFontView fontView;

    private List<View> alignmentViews = new ArrayList<>();

    @Override
    protected int layoutId() {
        return R.layout.tools_properties_text_fields_style_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        textFieldsView = rootView.findViewById(R.id.text_field_view);
        borderColorListView = rootView.findViewById(R.id.border_color_list_view);
        backgroundColorListView = rootView.findViewById(R.id.background_color_list_view);
        textColorListView = rootView.findViewById(R.id.text_color_list_view);
        llAlignment = rootView.findViewById(R.id.ll_alignment_type);
        ivAlignmentLeft = rootView.findViewById(R.id.iv_alignment_left);
        ivAlignmentCenter = rootView.findViewById(R.id.iv_alignment_center);
        ivAlignmentRight = rootView.findViewById(R.id.iv_alignment_right);
        fontSizeSliderBar = rootView.findViewById(R.id.font_size_slider_bar);
        etDefaultValue = rootView.findViewById(R.id.et_default_value);
        hideFormSwitch = rootView.findViewById(R.id.switch_hide_form);
        multiLineSwitch = rootView.findViewById(R.id.switch_multi_line);
        fontView = rootView.findViewById(R.id.font_view);
        ivAlignmentLeft.setOnClickListener(this);
        ivAlignmentCenter.setOnClickListener(this);
        ivAlignmentRight.setOnClickListener(this);
        alignmentViews.add(ivAlignmentLeft);
        alignmentViews.add(ivAlignmentCenter);
        alignmentViews.add(ivAlignmentRight);
        etDefaultValue.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN &&
                (event.getSource() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE &&
                (event.getButtonState() & MotionEvent.BUTTON_PRIMARY) != 0) {
                etDefaultValue.requestFocus();
            }
            return false;
        });
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
            multiLineSwitch.setChecked(annotStyle.isFormMultiLine());
            etDefaultValue.setText(annotStyle.getFormDefaultValue());
            etDefaultValue.setFilters(new InputFilter[]{ EditTextUtils.emojiFilter() });
            switch (annotStyle.getAlignment()) {
                case LEFT:
                case UNKNOWN:
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
        fontView.setFontChangeListener(new CPDFFontView.CFontChangeListener() {
            @Override
            public void font(String psName) {
                if (viewModel != null) {
                    viewModel.getStyle().setExternFontName(psName);
                }
            }
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
        etDefaultValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (viewModel != null) {
                    if (TextUtils.isEmpty(s)) {
                        viewModel.getStyle().setFormDefaultValue("");
                    } else {
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
        multiLineSwitch.setListener((buttonView, isChecked) -> {
            if (viewModel != null) {
                viewModel.getStyle().setFormMultiLine(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_alignment_left) {
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

    private void selectAlignmentView(AppCompatImageView alignmentView) {
        for (View view : alignmentViews) {
            view.setSelected(view == alignmentView);
        }
    }

    private void setAlignment(CAnnotStyle.Alignment alignment) {
        if (viewModel != null) {
            viewModel.getStyle().setAlignment(alignment);
        }
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
