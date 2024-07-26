/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.pdfdisplaysettings;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.ContextCompat;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.dialog.CDialogFragmentUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CToolBar;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CPDFDisplaySettingDialogFragment extends BottomSheetDialogFragment
        implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private CToolBar toolBar;

    private RadioGroup readerModeRadioGroup;

    private AppCompatRadioButton rbSinglePage;

    private AppCompatRadioButton rbDoublePage;

    private AppCompatRadioButton rbCoverDoublePage;

    private Switch swIsContinue;

    private RadioGroup rgScrollDirection;

    private AppCompatRadioButton rbVertical;

    private AppCompatRadioButton rbHorizontal;

    private Switch swIsCropMode;

    private RadioGroup readerBgRadioGroup;

    private AppCompatRadioButton rbLightMode;

    private AppCompatRadioButton rbDarkMode;

    private AppCompatRadioButton rbSepiaMode;

    private AppCompatRadioButton rbResedaMode;

    private CPDFViewCtrl pdfView;

    public static CPDFDisplaySettingDialogFragment newInstance() {
        return new CPDFDisplaySettingDialogFragment();
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int themeId = R.style.ComPDFKit_Theme_BottomSheetDialog_Light_SettingsDialog;
        int isLightThemeValue = CViewUtils.getThemeAttrData(getContext().getTheme(), R.attr.isLightTheme);
        boolean isDarkTheme = isLightThemeValue == 0;
        if (isDarkTheme){
            themeId = R.style.ComPDFKit_Theme_BottomSheetDialog_Dark_SettingsDialog;
        }
        setStyle(STYLE_NORMAL, themeId);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!CViewUtils.isLandScape(getContext())){
            CDialogFragmentUtil.setDimAmount(getDialog(), 0F);
        }
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        CDialogFragmentUtil.setBottomSheetDialogFragmentFullScreen(getDialog(), behavior);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tools_display_settings_dialog_fragment, container, false);
        toolBar = rootView.findViewById(R.id.tool_bar);
        toolBar.setBackBtnClickListener(v -> dismiss());
        readerModeRadioGroup = rootView.findViewById(R.id.radio_group_reader_mode);
        rbSinglePage = rootView.findViewById(R.id.r_btn_single_page);
        rbDoublePage = rootView.findViewById(R.id.r_btn_double_page);
        rbCoverDoublePage = rootView.findViewById(R.id.r_btn_cover_double_page);
        swIsContinue = rootView.findViewById(R.id.sw_is_continue);
        rgScrollDirection = rootView.findViewById(R.id.radio_group_scroll_direction);
        rbVertical = rootView.findViewById(R.id.r_btn_vertical);
        rbHorizontal = rootView.findViewById(R.id.r_btn_horizontal);
        swIsCropMode = rootView.findViewById(R.id.sw_is_crop);
        readerBgRadioGroup = rootView.findViewById(R.id.radio_group_reader_bg);
        rbLightMode = rootView.findViewById(R.id.r_btn_light_mode);
        rbDarkMode = rootView.findViewById(R.id.r_btn_dark_mode);
        rbSepiaMode = rootView.findViewById(R.id.r_btn_sepia_mode);
        rbResedaMode = rootView.findViewById(R.id.r_btn_reseda_mode);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (pdfView != null) {
            com.compdfkit.ui.reader.CPDFReaderView readerView = pdfView.getCPdfReaderView();
            if (readerView.isDoublePageMode()) {
                if (readerView.isCoverPageMode()) {
                    rbCoverDoublePage.setChecked(true);
                } else {
                    rbDoublePage.setChecked(true);
                }
            } else {
                rbSinglePage.setChecked(true);
            }
            swIsContinue.setChecked(readerView.isContinueMode());
            if (readerView.isVerticalMode()){
                rbVertical.setChecked(true);
            }else {
                rbHorizontal.setChecked(true);
            }
            swIsCropMode.setChecked(readerView.isCropMode());
            initThemes(readerView);
        }

        swIsContinue.setOnCheckedChangeListener(this);
        swIsCropMode.setOnCheckedChangeListener(this);

        readerModeRadioGroup.setOnCheckedChangeListener(this);
        readerBgRadioGroup.setOnCheckedChangeListener(this);
        rgScrollDirection.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        AppCompatRadioButton radioButton = group.findViewById(checkedId);
        if (radioButton == null || !radioButton.isChecked() || getContext() == null) {
            return;
        }
        if (checkedId == R.id.r_btn_light_mode) {
            setReaderBackgroundColor(ContextCompat.getColor(getContext(), R.color.tools_themes_light));
            pdfView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tools_pdf_view_ctrl_background_color));
        } else if (checkedId == R.id.r_btn_dark_mode) {
            int color = ContextCompat.getColor(getContext(), R.color.tools_themes_dark);
            setReaderBackgroundColor(color);
            pdfView.setBackgroundColor(CViewUtils.getColor(color, 190));
        } else if (checkedId == R.id.r_btn_sepia_mode) {
            int color = ContextCompat.getColor(getContext(), R.color.tools_themes_sepia);
            setReaderBackgroundColor(color);
            pdfView.setBackgroundColor(CViewUtils.getColor(color, 190));
        } else if (checkedId == R.id.r_btn_reseda_mode) {
            int color = ContextCompat.getColor(getContext(), R.color.tools_themes_reseda);
            setReaderBackgroundColor(color);
            pdfView.setBackgroundColor(CViewUtils.getColor(color, 190));
        } else if (checkedId == R.id.r_btn_single_page) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setDoublePageMode(false);
                pdfView.getCPdfReaderView().setCoverPageMode(false);
            }
        } else if (checkedId == R.id.r_btn_double_page) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setDoublePageMode(true);
                pdfView.getCPdfReaderView().setCoverPageMode(false);
            }
        } else if (checkedId == R.id.r_btn_cover_double_page) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setDoublePageMode(true);
                pdfView.getCPdfReaderView().setCoverPageMode(true);
            }
        } else if (checkedId == R.id.r_btn_vertical ){
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setVerticalMode(true);
            }
        } else if (checkedId == R.id.r_btn_horizontal) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setVerticalMode(false);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.sw_is_continue) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setContinueMode(isChecked);
            }
        } else if (buttonView.getId() == R.id.sw_is_crop) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().setCropMode(isChecked);
            }
        }
    }

    private void initThemes(CPDFReaderView readerView) {
        if (getContext() == null) {
            return;
        }
        int bgColor = readerView.getReadBackgroundColor();
        if (bgColor == ContextCompat.getColor(getContext(), R.color.tools_themes_light)) {
            rbLightMode.setChecked(true);
        } else if (bgColor == ContextCompat.getColor(getContext(), R.color.tools_themes_dark)) {
            rbDarkMode.setChecked(true);
        } else if (bgColor == ContextCompat.getColor(getContext(), R.color.tools_themes_sepia)) {
            rbSepiaMode.setChecked(true);
        } else if (bgColor == ContextCompat.getColor(getContext(), R.color.tools_themes_reseda)) {
            rbResedaMode.setChecked(true);
        } else {

        }
    }

    private void setReaderBackgroundColor(int color) {
        if (pdfView != null) {
            pdfView.getCPdfReaderView().setReadBackgroundColor(color);
        }
    }
}
