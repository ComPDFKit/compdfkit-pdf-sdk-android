/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfshape;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.preview.CAnnotLineTypePreviewView;
import com.compdfkit.tools.common.views.pdfproperties.preview.CStylePreviewView;

public class CShapeStyleFragment extends CBasicPropertiesFragment {

    private ColorListView borderColorListView;

    private ColorListView fillColorListView;

    private CSliderBar opacitySliderBar;

    private CStylePreviewView stylePreviewView;

    private CSliderBar borderWidthSliderBar;

    private CSliderBar dashedSliderBar;

    private ConstraintLayout clStartLineType;

    private ConstraintLayout clTailLineType;

    private CAnnotLineTypePreviewView startLinePreview;

    private CAnnotLineTypePreviewView tailLinePreview;


    public static CShapeStyleFragment newInstance() {
        return new CShapeStyleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_properties_shape_style_fragment, container, false);
        borderColorListView = rootView.findViewById(R.id.border_color_list_view);
        fillColorListView = rootView.findViewById(R.id.fill_color_list_view);
        stylePreviewView = rootView.findViewById(R.id.style_preview);
        opacitySliderBar = rootView.findViewById(R.id.slider_bar);
        borderWidthSliderBar = rootView.findViewById(R.id.slider_bar_border_width);
        dashedSliderBar = rootView.findViewById(R.id.dashed_slider_bar);
        clStartLineType = rootView.findViewById(R.id.cl_start_line_type);
        clTailLineType = rootView.findViewById(R.id.cl_tail_line_type);
        startLinePreview = rootView.findViewById(R.id.preview_start_line);
        tailLinePreview = rootView.findViewById(R.id.preview_tail_line);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle cAnnotStyle = viewModel.getStyle();
        if (cAnnotStyle != null) {
            stylePreviewView.setAnnotType(cAnnotStyle.getType());
            stylePreviewView.setColor(cAnnotStyle.getFillColor());
            stylePreviewView.setColorOpacity(cAnnotStyle.getFillColorOpacity());
            stylePreviewView.setBorderWidth((int) cAnnotStyle.getBorderWidth());
            stylePreviewView.setBorderColor(cAnnotStyle.getLineColor());
            stylePreviewView.setBorderColorOpacity(cAnnotStyle.getLineColorOpacity());
            stylePreviewView.setDashedSpaceWidth((int) cAnnotStyle.getBorderStyle().getDashArr()[1]);
            stylePreviewView.setStartLineType(cAnnotStyle.getStartLineType());
            stylePreviewView.setTailLineType(cAnnotStyle.getTailLineType());
            startLinePreview.setStartLineType(cAnnotStyle.getStartLineType());
            tailLinePreview.setTailLineType(cAnnotStyle.getTailLineType());

            borderColorListView.setSelectColor(cAnnotStyle.getLineColor());
            fillColorListView.setSelectColor(cAnnotStyle.getFillColor());
            borderWidthSliderBar.setProgress((int) cAnnotStyle.getBorderWidth());
            opacitySliderBar.setProgress(cAnnotStyle.getLineColorOpacity());
            dashedSliderBar.setProgress((int) cAnnotStyle.getBorderStyle().getDashArr()[1]);
            if (cAnnotStyle.getType() == CStyleType.ANNOT_ARROW || cAnnotStyle.getType() == CStyleType.ANNOT_LINE ){
                fillColorListView.setVisibility(View.GONE);
                borderColorListView.setTitle(R.string.tools_color);
                clStartLineType.setVisibility(View.VISIBLE);
                clTailLineType.setVisibility(View.VISIBLE);
            }
        }
        viewModel.addStyleChangeListener(this);
        opacitySliderBar.setChangeListener((opacity, percentageValue, isStop) -> updateColorOpacity(opacity));
        borderColorListView.setOnColorSelectListener(this::updateBorderColor);
        fillColorListView.setOnColorSelectListener(this::updateFillColor);
        borderWidthSliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (viewModel != null) {
                viewModel.getStyle().setBorderWidth(progress);
            }
        });
        dashedSliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (viewModel != null) {
                CPDFBorderStyle style = viewModel.getStyle().getBorderStyle();
                if (style == null) {
                    style = new CPDFBorderStyle();
                }
                if (progress == 0) {
                    style.setStyle(CPDFBorderStyle.Style.Border_Solid);
                    float[] dashAttr = new float[]{8.0F, progress};
                    if (style.getDashArr() != null) {
                        dashAttr = new float[]{style.getDashArr()[0], progress};
                    }
                    style.setDashArr(dashAttr);
                } else {
                    float[] dashAttr = new float[]{8.0F, progress};
                    if (style.getDashArr() != null) {
                        dashAttr = new float[]{style.getDashArr()[0], progress};
                    }
                    style.setStyle(CPDFBorderStyle.Style.Border_Dashed);
                    style.setDashArr(dashAttr);
                }
                viewModel.getStyle().setBorderStyle(style);
            }
        });
        borderColorListView.setColorPickerClickListener(() -> {
            CAnnotStyle style = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment)->{
                colorPickerFragment.initColor(style.getLineColor(), style.getLineColorOpacity());
                colorPickerFragment.setColorPickerListener(this::updateBorderColor);
                colorPickerFragment.setColorAlphaChangeListener(this::updateColorOpacity);
            });
        });
        fillColorListView.setColorPickerClickListener(() -> {
            CAnnotStyle style = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment)->{
                colorPickerFragment.initColor(style.getFillColor(), style.getFillColorOpacity());
                colorPickerFragment.setColorPickerListener(this::updateFillColor);
                colorPickerFragment.setColorAlphaChangeListener(this::updateColorOpacity);
            });
        });
        clStartLineType.setOnClickListener(v -> {
            CAnnotStyle style = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.lineType(R.string.tools_annot_start_line_style), (CLineArrowTypeListFragment lineTypeFragment)->{
                lineTypeFragment.setLineType(style.getStartLineType(), true);
                lineTypeFragment.setLineTypeListener(style::setStartLineType);
            });
        });
        clTailLineType.setOnClickListener(v -> {
            CAnnotStyle style = viewModel.getStyle();
            showFragment(CStyleFragmentDatas.lineType(R.string.tools_annot_tail_line_style), (CLineArrowTypeListFragment lineTypeFragment)->{
                lineTypeFragment.setLineType(style.getTailLineType(), false);
                lineTypeFragment.setLineTypeListener(style::setTailLineType);
            });
        });
    }

    private void updateBorderColor(int color) {
        if (viewModel != null) {
            CAnnotStyle style = viewModel.getStyle();
            if (style.getLineColor() == Color.TRANSPARENT) {
                style.setLineColorOpacity(opacitySliderBar.getProgress());
            }
            viewModel.getStyle().setBorderColor(color);
            if (style.getType() == CStyleType.ANNOT_ARROW){
                updateFillColor(color);
            }
        }
    }

    private void updateFillColor(int fillColor) {
        if (viewModel != null) {
            CAnnotStyle style = viewModel.getStyle();
            if (style.getFillColor() == Color.TRANSPARENT) {
                style.setFillColorOpacity(opacitySliderBar.getProgress());
            }
            style.setFillColor(fillColor);
        }
    }

    private void updateColorOpacity(int opacity) {
        if (viewModel != null) {
            CAnnotStyle style = viewModel.getStyle();
            if (style.getFillColor() != Color.TRANSPARENT) {
                style.setFillColorOpacity(opacity);
            }
            if (style.getLineColor() != Color.TRANSPARENT) {
                style.setLineColorOpacity(opacity);
            }
        }
    }

    @Override
    public void onChangeFillColor(int color) {
        if (stylePreviewView != null) {
            stylePreviewView.setColor(color);
        }
        if (!isOnResume) {
            if (fillColorListView != null) {
                fillColorListView.setSelectColor(color);
            }
        }
    }

    @Override
    public void onChangeFillColorOpacity(int opacity) {
        if (stylePreviewView != null) {
            stylePreviewView.setColorOpacity(opacity);
        }
        if (!isOnResume) {
            if (opacitySliderBar != null) {
                opacitySliderBar.setProgress(opacity);
            }
        }
    }

    @Override
    public void onChangeBorderWidth(float borderWidth) {
        if (stylePreviewView != null) {
            stylePreviewView.setBorderWidth((int) borderWidth);
        }
    }

    @Override
    public void onChangeLineColor(int color) {
        if (stylePreviewView != null) {
            stylePreviewView.setBorderColor(color);
        }
        if (!isOnResume) {
            if (borderColorListView != null) {
                borderColorListView.setSelectColor(color);
            }
        }
    }

    @Override
    public void onChangeLineColorOpacity(int opacity) {
        if (stylePreviewView != null) {
            stylePreviewView.setBorderColorOpacity(opacity);
        }
        if (!isOnResume) {
            if (opacitySliderBar != null) {
                opacitySliderBar.setProgress(opacity);
            }
        }
    }

    @Override
    public void onChangeBorderStyle(CPDFBorderStyle style) {
        super.onChangeBorderStyle(style);
        if (stylePreviewView != null) {
            stylePreviewView.setDashedSpaceWidth((int) style.getDashArr()[1]);
        }
    }

    @Override
    public void onChangeStartLineType(CPDFLineAnnotation.LineType lineType) {
        super.onChangeStartLineType(lineType);
        if (stylePreviewView != null) {
            stylePreviewView.setStartLineType(lineType);
        }
        if (startLinePreview != null) {
            startLinePreview.setStartLineType(lineType);
        }
    }

    @Override
    public void onChangeTailLineType(CPDFLineAnnotation.LineType lineType) {
        super.onChangeTailLineType(lineType);
        if (stylePreviewView != null) {
            stylePreviewView.setTailLineType(lineType);
        }
        if (tailLinePreview != null) {
            tailLinePreview.setTailLineType(lineType);
        }
    }
}
