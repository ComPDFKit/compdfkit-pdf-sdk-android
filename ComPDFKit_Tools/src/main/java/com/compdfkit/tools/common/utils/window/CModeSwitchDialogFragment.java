package com.compdfkit.tools.common.utils.window;


import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.LinkedHashSet;

public class CModeSwitchDialogFragment extends CBasicBottomSheetDialogFragment
        implements View.OnClickListener {

    private OnPreviewModeChangeListener changeListener;

    private AppCompatImageView ivClose;

    private AppCompatTextView tvTitle;

    private RadioGroup radioGroup;

    private RadioButton rbViewer;

    private RadioButton rbAnnotation;

    private RadioButton rbPDFEdit;

    private RadioButton rbForm;

    private RadioButton rbPageEdit;

    private RadioButton rbSignature;

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
    protected int getStyle() {
        return R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle_TopCorners;
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
        rbViewer = rootView.findViewById(R.id.r_btn_viewer_mode);
        rbAnnotation = rootView.findViewById(R.id.r_btn_annotation_mode);
        rbPDFEdit = rootView.findViewById(R.id.r_btn_pdf_edit_mode);
        rbForm = rootView.findViewById(R.id.r_btn_form_mode);
        rbPageEdit = rootView.findViewById(R.id.r_btn_page_edit_mode);
        rbSignature = rootView.findViewById(R.id.r_btn_signature_mode);
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
        switch (selectMode){
            case Viewer:
                rbViewer.setChecked(true);
                break;
            case Annotation:
                rbAnnotation.setChecked(true);
                break;
            case Edit:
                rbPDFEdit.setChecked(true);
                break;
            case Form:
                rbForm.setChecked(true);
                break;
            case PageEdit:
                rbPageEdit.setChecked(true);
                break;
            case Signature:
                rbSignature.setChecked(true);
                break;
            default:break;
        }
        modifyPopupMenuStatus();
        rbViewer.setOnClickListener(this);
        rbAnnotation.setOnClickListener(this);
        rbPDFEdit.setOnClickListener(this);
        rbForm.setOnClickListener(this);
        rbPageEdit.setOnClickListener(this);
        rbSignature.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.r_btn_viewer_mode) {
            rbViewer.setChecked(true);
            changeMode(CPreviewMode.Viewer);
        } else if (v.getId() == R.id.r_btn_annotation_mode) {
            rbAnnotation.setChecked(true);
            changeMode(CPreviewMode.Annotation);
        } else if (v.getId() == R.id.r_btn_pdf_edit_mode) {
            rbPDFEdit.setChecked(true);
            changeMode(CPreviewMode.Edit);
        } else if (v.getId() == R.id.r_btn_page_edit_mode) {
            rbPageEdit.setChecked(true);
            changeMode(CPreviewMode.PageEdit);
        } else if (v.getId() == R.id.r_btn_form_mode) {
            rbForm.setChecked(true);
            changeMode(CPreviewMode.Form);
        } else if (v.getId() == R.id.r_btn_signature_mode){
            rbSignature.setChecked(true);
            changeMode(CPreviewMode.Signature);
        }
        dismiss();
    }


    private void modifyPopupMenuStatus() {
        if (rbViewer != null) {
            rbViewer.setVisibility(previewModes.contains(CPreviewMode.Viewer) ? View.VISIBLE : View.GONE);
        }
        if (rbAnnotation != null) {
            rbAnnotation.setVisibility(previewModes.contains(CPreviewMode.Annotation) ? View.VISIBLE : View.GONE);
        }
        if (rbPDFEdit != null) {
            rbPDFEdit.setVisibility(previewModes.contains(CPreviewMode.Edit) ? View.VISIBLE : View.GONE);
        }
        if (rbForm != null) {
            rbForm.setVisibility(previewModes.contains(CPreviewMode.Form) ? View.VISIBLE : View.GONE);
        }
        if (rbPageEdit != null) {
            rbPageEdit.setVisibility(previewModes.contains(CPreviewMode.PageEdit) ? View.VISIBLE : View.GONE);
        }
        if (rbSignature != null) {
            rbSignature.setVisibility(previewModes.contains(CPreviewMode.Signature) ? View.VISIBLE : View.GONE);
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
