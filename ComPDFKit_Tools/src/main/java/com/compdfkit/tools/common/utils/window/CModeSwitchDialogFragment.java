package com.compdfkit.tools.common.utils.window;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.color.MaterialColors;

import java.util.LinkedHashSet;

public class CModeSwitchDialogFragment extends CBasicBottomSheetDialogFragment
        implements View.OnClickListener {

    private OnPreviewModeChangeListener changeListener;

    private AppCompatImageView ivClose;

    private AppCompatTextView tvTitle;

    private RadioGroup radioGroup;

    private AppCompatRadioButton rbViewer;

    private AppCompatRadioButton rbAnnotation;

    private AppCompatRadioButton rbPDFEdit;

    private AppCompatRadioButton rbForm;

    private AppCompatRadioButton rbPageEdit;

    private AppCompatRadioButton rbSignature;

    private LinkedHashSet<CPreviewMode> previewModes = new LinkedHashSet<>();

    private CPreviewMode selectMode;

    public static CModeSwitchDialogFragment newInstance(LinkedHashSet<CPreviewMode> modes, CPreviewMode selectMode) {
        Bundle args = new Bundle();
        CModeSwitchDialogFragment fragment = new CModeSwitchDialogFragment();
        fragment.setArguments(args);
        fragment.setModes(modes);
        fragment.setSelectMode(selectMode);
        return fragment;
    }

    public void setModes(LinkedHashSet<CPreviewMode> modes){
        previewModes = modes;
    }

    public void setSelectMode(CPreviewMode selectMode){
        this.selectMode = selectMode;
    }


    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
        behavior.setSkipCollapsed(true);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    protected int themeResId() {
        return R.attr.compdfkit_BottomSheetDialog_Transparent_Theme;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_pdf_mode_switch_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        ivClose = rootView.findViewById(R.id.iv_tool_bar_close);
        tvTitle = rootView.findViewById(R.id.tv_tool_bar_title);
        radioGroup = rootView.findViewById(R.id.radio_group_mode);

    }

    @Override
    protected void onViewCreate() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null){
            if (selectMode == null && savedInstanceState.containsKey("select_mode")) {
                selectMode = CPreviewMode.valueOf(savedInstanceState.getString("select_mode"));
            }
            if (previewModes.size() <=0 && savedInstanceState.containsKey("modes")){
                try {
                    previewModes = (LinkedHashSet<CPreviewMode>) savedInstanceState.getSerializable("modes");
                }catch (Exception e){

                }
            }
        }
        tvTitle.setText(R.string.tools_pdf_mode_title);
        ivClose.setOnClickListener(v -> {
            dismiss();
        });
        modifyPopupMenuStatus();
        switch (selectMode){
            case Viewer:
                setChecked(rbViewer, true);
                break;
            case Annotation:
                setChecked(rbAnnotation, true);
                break;
            case Edit:
                setChecked(rbPDFEdit, true);
                break;
            case Form:
                setChecked(rbForm, true);
                break;
            case PageEdit:
                setChecked(rbPageEdit, true);
                break;
            case Signature:
                setChecked(rbSignature, true);
                break;
            default:break;
        }
        setOnClickListener(rbViewer);
        setOnClickListener(rbAnnotation);
        setOnClickListener(rbPDFEdit);
        setOnClickListener(rbForm);
        setOnClickListener(rbPageEdit);
        setOnClickListener(rbSignature);
    }

    @Override
    public void onClick(View v) {
        if (rbViewer != null && v.getId() == rbViewer.getId()) {
            rbViewer.setChecked(true);
            changeMode(CPreviewMode.Viewer);
        } else if (rbAnnotation != null && v.getId() == rbAnnotation.getId()) {
            rbAnnotation.setChecked(true);
            changeMode(CPreviewMode.Annotation);
        } else if (rbPDFEdit != null && v.getId() == rbPDFEdit.getId()) {
            rbPDFEdit.setChecked(true);
            changeMode(CPreviewMode.Edit);
        } else if (rbPageEdit != null && v.getId() == rbPageEdit.getId()) {
            rbPageEdit.setChecked(true);
            changeMode(CPreviewMode.PageEdit);
        } else if (rbForm != null && v.getId() == rbForm.getId()) {
            rbForm.setChecked(true);
            changeMode(CPreviewMode.Form);
        } else if (rbSignature != null && v.getId() == rbSignature.getId()){
            rbSignature.setChecked(true);
            changeMode(CPreviewMode.Signature);
        }
        dismiss();
    }


    private void modifyPopupMenuStatus() {
        radioGroup.removeAllViews();
        for (CPreviewMode previewMode : previewModes) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.tools_pdf_mode_radio_button_item, null);
            item.setId(View.generateViewId());
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CDimensUtils.dp2px(getContext(), 60));
            item.setLayoutParams(layoutParams);
            AppCompatRadioButton button = (AppCompatRadioButton) item;
            switch (previewMode) {
                case Viewer:
                    rbViewer = button;
                    setRadioButtonInfo(rbViewer, R.string.tools_pdf_viewer_mode, R.drawable.tools_ic_preview_settings);
                    break;
                case Annotation:
                    rbAnnotation = button;
                    setRadioButtonInfo(rbAnnotation, R.string.tools_pdf_annotation_mode, R.drawable.tools_annotation);
                    break;
                case Edit:
                    rbPDFEdit = button;
                    setRadioButtonInfo(rbPDFEdit, R.string.tools_pdf_edit_mode, R.drawable.tools_edit);
                    break;
                case Form:
                    rbForm = button;
                    setRadioButtonInfo(rbForm, R.string.tools_form_mode, R.drawable.tools_form);
                    break;
                case Signature:
                    rbSignature = button;
                    setRadioButtonInfo(rbSignature, R.string.tools_signatures, R.drawable.tools_mode_switch_digital_signature);
                    break;
                case PageEdit:
                    rbPageEdit = button;
                    setRadioButtonInfo(rbPageEdit, R.string.tools_page_edit_mode, R.drawable.tools_page_edit);
                    break;
                default:
                    break;
            }
            radioGroup.addView(button);
        }
    }

    private void setRadioButtonInfo(AppCompatRadioButton item, @StringRes int titleResId, @DrawableRes int startDrawableResId){
        item.setText(titleResId);
        Drawable startDrawable = ContextCompat.getDrawable(getContext(), startDrawableResId);
        int startDrawableColor = MaterialColors.getColor(getContext(), com.google.android.material.R.attr.colorOnPrimary, Color.BLACK);
        DrawableCompat.setTint(startDrawable, startDrawableColor);
        DrawableCompat.setTintMode(startDrawable, PorterDuff.Mode.SRC_ATOP);
        Drawable endDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tools_reader_settings_page_mode_radio_button);
        item.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null,endDrawable,null);
    }

    private void setChecked(AppCompatRadioButton item, boolean checked){
        if (item != null) {
            item.setChecked(checked);
        }
    }

    private void setOnClickListener(AppCompatRadioButton item){
        if (item != null) {
            item.setOnClickListener(this);
        }
    }


    private void changeMode(CPreviewMode mode) {
        selectMode = mode;
        if (changeListener != null) {
            changeListener.change(selectMode);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("select_mode", selectMode.name());
        outState.putSerializable("modes", previewModes);
        super.onSaveInstanceState(outState);
    }

    public void setSwitchModeListener(OnPreviewModeChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public interface OnPreviewModeChangeListener {
        void change(CPreviewMode mode);
    }
}
