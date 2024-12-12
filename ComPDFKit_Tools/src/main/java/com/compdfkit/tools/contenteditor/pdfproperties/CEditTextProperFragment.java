package com.compdfkit.tools.contenteditor.pdfproperties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.compdfkit.tools.common.views.pdfproperties.font.CPDFFontView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;

import java.util.ArrayList;
import java.util.List;

public class CEditTextProperFragment extends CBasicPropertiesFragment
        implements View.OnClickListener, ColorPickerView.COnColorChangeListener, ColorPickerView.COnColorAlphaChangeListener {

    private ColorListView colorListView;

    private CSliderBar opacitySliderBar;

    private LinearLayout llAlignment;

    private AppCompatImageView ivAlignmentLeft;

    private AppCompatImageView ivAlignmentCenter;

    private AppCompatImageView ivAlignmentRight;

    private CSliderBar fontSizeSliderBar;

    private CPDFFontView fontView;

    private AppCompatImageView ivStyleUnderLine;
    private AppCompatImageView ivStyleRemoveUnderLine;

    private AppCompatImageView ivStyleStrikeLine;
    private AppCompatImageView ivStyleRemoveStrikeLine;

    private List<View> alignmentViews = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_edit_text_property_fragment, container, false);
        colorListView = rootView.findViewById(R.id.border_color_list_view);
        opacitySliderBar = rootView.findViewById(R.id.slider_bar);
        llAlignment = rootView.findViewById(R.id.ll_alignment_type);
        ivAlignmentLeft = rootView.findViewById(R.id.iv_alignment_left);
        ivAlignmentCenter = rootView.findViewById(R.id.iv_alignment_center);
        ivAlignmentRight = rootView.findViewById(R.id.iv_alignment_right);
        fontSizeSliderBar = rootView.findViewById(R.id.font_size_slider_bar);
        fontView = rootView.findViewById(R.id.font_view);
        ivStyleUnderLine = rootView.findViewById(R.id.iv_style_underline);
        ivStyleRemoveUnderLine = rootView.findViewById(R.id.iv_style_remove_underline);
        ivStyleStrikeLine = rootView.findViewById(R.id.iv_style_strike_line);
        ivStyleRemoveStrikeLine = rootView.findViewById(R.id.iv_style_remove_strike_line);
        ivAlignmentLeft.setOnClickListener(this);
        ivAlignmentCenter.setOnClickListener(this);
        ivAlignmentRight.setOnClickListener(this);
        ivStyleRemoveUnderLine.setOnClickListener(this);
        ivStyleUnderLine.setOnClickListener(this);
        ivStyleStrikeLine.setOnClickListener(this);
        ivStyleRemoveStrikeLine.setOnClickListener(this);
        alignmentViews.add(ivAlignmentLeft);
        alignmentViews.add(ivAlignmentCenter);
        alignmentViews.add(ivAlignmentRight);
        return rootView;
    }

    private void setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType type) {
        if (viewModel != null && viewModel.getStyle() != null) {
            viewModel.getStyle().setUpdatePropertyType(type);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CAnnotStyle annotStyle = viewModel.getStyle();
        if (annotStyle != null) {
            fontView.initFont(annotStyle.getExternFontName());

            colorListView.setSelectColor(annotStyle.getTextColor());
            opacitySliderBar.setProgress(annotStyle.getTextColorOpacity());
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
            showFragment(CStyleFragmentDatas.colorPicker(), (CColorPickerFragment colorPickerFragment) -> {
                colorPickerFragment.initColor(cAnnotStyle.getTextColor(), cAnnotStyle.getTextColorOpacity());
                colorPickerFragment.setColorPickerListener(this);
                colorPickerFragment.setColorAlphaChangeListener(this);
            });
        });
        opacitySliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (isStop) {
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.TextColorOpacity);
                opacity(progress);
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
            }
        });
        fontSizeSliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (viewModel != null && isStop) {
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.FontSize);
                viewModel.getStyle().setFontSize(progress);
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
            } else if (isStop == false) {
                this.onChangeFontSize(progress);
            }
        });
        viewModel.addStyleChangeListener(this);
        fontView.setFontChangeListener(psName -> {
            if (viewModel != null) {
                boolean isBold = CPDFTextAttribute.FontNameHelper.isBold(psName);
                boolean isItalic = CPDFTextAttribute.FontNameHelper.isItalic(psName);
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.FontType);
                viewModel.getStyle().setBold(isBold);
                viewModel.getStyle().setItalic(isItalic);
                viewModel.getStyle().setExternFontName(psName);
            }
            setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
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
        } else if (v.getId() == R.id.iv_style_underline) {
            viewModel.getStyle().setEditTextUnderLine(true);
        } else if (v.getId() == R.id.iv_style_strike_line) {
            viewModel.getStyle().setEditTextStrikeThrough(true);
        } else if (v.getId() == R.id.iv_style_remove_underline) {
            viewModel.getStyle().setEditTextUnderLine(false);
        } else if (v.getId() == R.id.iv_style_remove_strike_line) {
            viewModel.getStyle().setEditTextStrikeThrough(false);
        } else {

        }
    }

    private void selectAlignmentView(AppCompatImageView alignmentView) {
        for (View view : alignmentViews) {
            view.setSelected(view == alignmentView);
        }
    }

    private void setAlignment(CAnnotStyle.Alignment alignment) {
        if (viewModel != null) {
            setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.Alignment);
            viewModel.getStyle().setAlignment(alignment);
            setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
        }
    }

    @Override
    public void color(int color) {
        if (viewModel != null && viewModel.getStyle() != null) {
            setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.TextColor);
            viewModel.getStyle().setFontColor(color);
            setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
        }
    }

    @Override
    public void opacity(int opacity) {
        if (viewModel != null) {
            viewModel.getStyle().setTextColorOpacity(opacity);
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
