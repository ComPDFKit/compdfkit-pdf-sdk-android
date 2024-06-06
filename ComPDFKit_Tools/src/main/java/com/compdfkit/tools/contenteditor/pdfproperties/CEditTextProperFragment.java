package com.compdfkit.tools.contenteditor.pdfproperties;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.utils.CPDFSysFontUtils;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.view.colorpicker.CColorPickerFragment;
import com.compdfkit.tools.common.utils.view.colorpicker.widget.ColorPickerView;
import com.compdfkit.tools.common.utils.view.sliderbar.CSliderBar;
import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;
import com.compdfkit.tools.common.views.pdfproperties.colorlist.ColorListView;
import com.compdfkit.tools.common.views.pdfproperties.font.CExternFontSpinnerAdapter;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleFragmentDatas;
import com.compdfkit.tools.common.views.pdfproperties.preview.CStylePreviewView;

import java.util.ArrayList;
import java.util.List;

public class CEditTextProperFragment extends CBasicPropertiesFragment
        implements View.OnClickListener, ColorPickerView.COnColorChangeListener , ColorPickerView.COnColorAlphaChangeListener{

    private CStylePreviewView previewView;

    private ColorListView colorListView;

    private CSliderBar opacitySliderBar;

    private AppCompatTextView tvFontType;

    private AppCompatImageView tvFontItalic;

    private AppCompatImageView tvFontBold;

    private LinearLayout llAlignment;

    private AppCompatImageView ivAlignmentLeft;

    private AppCompatImageView ivAlignmentCenter;

    private AppCompatImageView ivAlignmentRight;

    private CSliderBar fontSizeSliderBar;

    private Spinner fontSpinner;

    CExternFontSpinnerAdapter fontSpinnerAdapter;

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
        previewView = rootView.findViewById(R.id.style_preview);
        colorListView = rootView.findViewById(R.id.border_color_list_view);
        opacitySliderBar = rootView.findViewById(R.id.slider_bar);
        tvFontType = rootView.findViewById(R.id.tv_font_type);
        tvFontItalic = rootView.findViewById(R.id.iv_font_italic);
        tvFontBold = rootView.findViewById(R.id.iv_font_bold);
        llAlignment = rootView.findViewById(R.id.ll_alignment_type);
        ivAlignmentLeft = rootView.findViewById(R.id.iv_alignment_left);
        ivAlignmentCenter = rootView.findViewById(R.id.iv_alignment_center);
        ivAlignmentRight = rootView.findViewById(R.id.iv_alignment_right);
        fontSizeSliderBar = rootView.findViewById(R.id.font_size_slider_bar);
        fontSpinner = rootView.findViewById(R.id.spinner_font);
        tvFontType.setOnClickListener(this);
        tvFontItalic.setOnClickListener(this);
        tvFontBold.setOnClickListener(this);
        ivAlignmentLeft.setOnClickListener(this);
        ivAlignmentCenter.setOnClickListener(this);
        ivAlignmentRight.setOnClickListener(this);
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
            previewView.setTextColor(annotStyle.getTextColor());
            previewView.setTextAlignment(annotStyle.getAlignment());
            previewView.setTextColorOpacity(annotStyle.getTextColorOpacity());
            previewView.setFontBold(annotStyle.isFontBold());
            previewView.setFontItalic(annotStyle.isFontItalic());
            previewView.setFontType(annotStyle.getFontType());
            previewView.setFontSize(annotStyle.getFontSize());
            List<CPDFTextAttribute.FontNameHelper.FontType> fontTypes = new ArrayList<>();
            fontTypes.add(CPDFTextAttribute.FontNameHelper.FontType.Unknown);
            fontTypes.add(CPDFTextAttribute.FontNameHelper.FontType.Courier);
            fontTypes.add(CPDFTextAttribute.FontNameHelper.FontType.Helvetica);
            fontTypes.add(CPDFTextAttribute.FontNameHelper.FontType.Times_Roman);
            fontSpinnerAdapter = new CExternFontSpinnerAdapter(getContext(), fontTypes, CPDFSysFontUtils.GetSysFontName());
            fontSpinner.setAdapter(fontSpinnerAdapter);

            String externFontName = annotStyle.getExternFontName();
            if (TextUtils.isEmpty(externFontName)) {
                switch (annotStyle.getFontType()) {
                    case Unknown:
                        fontSpinner.setSelection(0);
                        break;
                    case Courier:
                        fontSpinner.setSelection(1);
                        break;
                    case Helvetica:
                        fontSpinner.setSelection(2);
                        break;
                    case Times_Roman:
                        fontSpinner.setSelection(3);
                        break;
                    default:
                        break;
                }
            } else {
                List<String> fontNameList = CPDFSysFontUtils.GetSysFontName();
                if (fontNameList != null) {
                    for (int i = 0; i < fontNameList.size(); i++) {
                        if (fontNameList.get(i).contains(externFontName)) {
                            fontSpinner.setSelection(fontSpinnerAdapter.getStandardFontCount() + i);
                            break;
                        }
                    }
                }
            }

            colorListView.setSelectColor(annotStyle.getTextColor());
            opacitySliderBar.setProgress(annotStyle.getTextColorOpacity());
            tvFontBold.setSelected(annotStyle.isFontBold());
            tvFontItalic.setSelected(annotStyle.isFontItalic());
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
        opacitySliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (isStop) {
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.TextColorOpacity);
                opacity(progress);
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
            } else {
                if (previewView != null) {
                    previewView.setTextColorOpacity(progress);
                }
            }
        });
        fontSizeSliderBar.setChangeListener((progress, percentageValue, isStop) -> {
            if (viewModel != null && isStop) {
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.FontSize);
                viewModel.getStyle().setFontSize(progress);
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
            } else if (isStop == false){
                this.onChangeFontSize(progress);
            }
        });
        viewModel.addStyleChangeListener(this);
        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (viewModel != null) {
                    setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.FontType);
                    if (position < fontSpinnerAdapter.getStandardFontCount()) {
                        CPDFTextAttribute.FontNameHelper.FontType fontType = (CPDFTextAttribute.FontNameHelper.FontType) fontSpinner.getItemAtPosition(position);
                        viewModel.getStyle().setFontType(fontType);
                    } else {
                        String fontName = (String) fontSpinner.getItemAtPosition(position);
                        viewModel.getStyle().setExternFontName(fontName);
                    }
                    setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_font_bold) {
            tvFontBold.setSelected(!tvFontBold.isSelected());
            if (viewModel != null) {
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.Bold);
                viewModel.getStyle().setFontBold(tvFontBold.isSelected());
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
            }
        } else if (v.getId() == R.id.iv_font_italic) {
            tvFontItalic.setSelected(!tvFontItalic.isSelected());
            if (viewModel != null) {
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.Italic);
                viewModel.getStyle().setFontItalic(tvFontItalic.isSelected());
                setUpdatePropertyType(CAnnotStyle.EditUpdatePropertyType.All);
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
    public void onChangeAnnotExternFontType(String fontName) {
        if (previewView != null) {
            List<String> fontList = CPDFSysFontUtils.getSysFontPathList();
            String font = "";
            for (String item : fontList) {
                if (TextUtils.isEmpty(item)) {
                    continue;
                }
                if (item.contains(fontName + ".")) {
                    font = item;
                    break;
                }
            }
            previewView.setExternFontType(font);
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
