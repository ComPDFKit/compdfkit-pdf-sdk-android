/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.contenteditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.core.edit.OnEditStatusChangeListener;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;
import com.compdfkit.tools.common.pdf.config.ContentEditorConfig;
import com.compdfkit.tools.common.utils.CListUtil;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;

import java.util.Arrays;
import java.util.List;

public class CEditToolbar extends RelativeLayout implements View.OnClickListener {
    public static final int SELECT_AREA_NONE = 0;
    public static final int SELECT_AREA_TEXT = 1;
    public static final int SELECT_AREA_IMAGE = 2;

    private ConstraintLayout clEdit;
    private LinearLayout llTools;
    private CPDFViewCtrl pdfView;
    private androidx.appcompat.widget.AppCompatImageView ivEditText;
    private androidx.appcompat.widget.AppCompatImageView ivEditImage;
    private androidx.appcompat.widget.AppCompatImageView ivProper;
    private androidx.appcompat.widget.AppCompatImageView ivUndo;
    private androidx.appcompat.widget.AppCompatImageView ivRedo;

    private View.OnClickListener proPerClickListener;

    public CEditToolbar(Context context) {
        super(context);
        init(context);
    }

    public CEditToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CEditToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CEditToolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.tools_edit_toolbar, this);
        CViewUtils.applyViewBackground(this);
        clEdit = findViewById(R.id.cl_edit);
        ivEditText = findViewById(R.id.iv_edit_text);
        ivEditImage = findViewById(R.id.iv_edit_image);
        llTools = findViewById(R.id.ll_tools);
        ivEditText.setOnClickListener(this);
        ivEditImage.setOnClickListener(this);
        ivEditText.setSelected(false);
        ivEditImage.setSelected(false);
        updateUndo(false);
        updateRedo(false);

        if (isInEditMode()){
            setTools(Arrays.asList(AnnotationsConfig.AnnotationTools.Setting, AnnotationsConfig.AnnotationTools.Undo, AnnotationsConfig.AnnotationTools.Redo));
        }
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        initEditorManager();
    }

    public void updateUndo(boolean canUndo) {
        if (ivUndo != null) {
            ivUndo.setEnabled(canUndo);
        }
    }

    public void updateRedo(boolean canRedo) {
        if (ivRedo != null) {
            ivRedo.setEnabled(canRedo);
        }
    }

    public void updateUndoRedo() {
        if (pdfView == null || pdfView.getCPdfReaderView() == null || pdfView.getCPdfReaderView().getEditManager() == null) {
            return;
        }
        CPDFEditManager editManager = pdfView.getCPdfReaderView().getEditManager();
        updateUndo(editManager.canUndo());
        updateRedo(editManager.canRedo());
    }

    private void changeEditType() {
        if (pdfView == null || pdfView.getCPdfReaderView() == null || pdfView.getCPdfReaderView().getEditManager() == null) {
            return;
        }
        CPDFEditManager editManager = pdfView.getCPdfReaderView().getEditManager();
        if (!editManager.isEditMode()) {
            editManager.enable();
        }
        IContextMenuShowListener menuShowListener = pdfView.getCPdfReaderView().getContextMenuShowListener();
        if (menuShowListener != null) {
            menuShowListener.dismissContextMenu();
        }
        if (ivEditText.isSelected() && !ivEditImage.isSelected()) {
            pdfView.getCPdfReaderView().getEditManager().changeEditType(CPDFEditPage.LoadText);
        } else if (!ivEditText.isSelected() && ivEditImage.isSelected()) {
            pdfView.getCPdfReaderView().getEditManager().changeEditType(CPDFEditPage.LoadImage);
        } else if (!ivEditText.isSelected() && !ivEditImage.isSelected()) {
            pdfView.getCPdfReaderView().getEditManager().changeEditType(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
        }
    }

    @Override
    public void onClick(View view) {
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return;
        }
        if (view.getId() == R.id.iv_edit_text) {
            boolean sel = view.isSelected();
            view.setSelected(!sel);
            if (!sel) {
                ivEditImage.setSelected(false);
            }
            changeEditType();
        } else if (view.getId() == R.id.iv_edit_image) {
            boolean sel = view.isSelected();
            view.setSelected(!sel);
            if (!sel) {
                ivEditText.setSelected(false);
            }
            changeEditType();
        }
    }

    public void setEditPropertyBtnClickListener(View.OnClickListener listener) {
        proPerClickListener = listener;
    }

    private void initEditorManager(){
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return;
        }
        CPDFEditManager editManager = pdfView.getCPdfReaderView().getEditManager();
        if (editManager == null) {
            return;
        }
        editManager.enable();
        pdfView.getCPdfReaderView().setSelectEditAreaChangeListener(type -> {
            if (ivProper != null) {
                if (type == SELECT_AREA_NONE) {
                    ivProper.setEnabled(false);
                } else {
                    ivProper.setEnabled(true);
                }
            }
        });
        pdfView.addEditStatusChangeListener(new OnEditStatusChangeListener() {
            @Override
            public void onBegin(int i) {
                CThreadPoolUtils.getInstance().executeMain(() -> {
                    if (pdfView == null || pdfView.getCPdfReaderView() == null || pdfView.getCPdfReaderView().getEditManager() == null) {
                        return;
                    }
                    updateTypeStatus();

                });
            }

            @Override
            public void onUndoRedo(int pageIndex, boolean canUndo, boolean canRedo) {
                CThreadPoolUtils.getInstance().executeMain(() -> {
                    if (pdfView.getCPdfReaderView() == null) {
                        return;
                    }
                    if (pageIndex == pdfView.getCPdfReaderView().getPageNum()) {
                        updateUndo(canUndo);
                        updateRedo(canRedo);
                    }
                });
            }

            @Override
            public void onExit() {

            }
        });
    }

    public void setEditMode(boolean beginedit) {
        CPDFEditManager editManager = pdfView.getCPdfReaderView().getEditManager();
        if (editManager == null) {
            return;
        }
        if (beginedit) {
            editManager.beginEdit(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
        }
    }

    public void updateTypeStatus(){
        int type = pdfView.getCPdfReaderView().getLoadType();
        if (type == CPDFEditPage.LoadText) {
            ivEditText.setSelected(true);
            ivEditImage.setSelected(false);
        } else if (type == CPDFEditPage.LoadImage) {
            ivEditText.setSelected(false);
            ivEditImage.setSelected(true);
        } else {
            ivEditText.setSelected(false);
            ivEditImage.setSelected(false);
        }
    }

    public void setEditType(ContentEditorConfig.ContentEditorType... types){
        if (types == null || types.length == 0){
            ivEditImage.setVisibility(View.GONE);
            ivEditText.setVisibility(View.GONE);
            return;
        }

        if (types.length == 1){
            ContentEditorConfig.ContentEditorType type = types[0];
            if (type == ContentEditorConfig.ContentEditorType.EditorText){
                ivEditImage.setVisibility(View.GONE);
            }else {
                ivEditText.setVisibility(View.GONE);
            }
        }else {
            ContentEditorConfig.ContentEditorType type = types[0];
            if (type == ContentEditorConfig.ContentEditorType.EditorImage){
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(clEdit);
                constraintSet.connect(ivEditImage.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(ivEditImage.getId(), ConstraintSet.END, ivEditText.getId(), ConstraintSet.START);
                constraintSet.connect(ivEditText.getId(), ConstraintSet.START, ivEditImage.getId(), ConstraintSet.END);
                constraintSet.connect(ivEditText.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.applyTo(clEdit);
            }
        }
    }

    public void setTools(List<AnnotationsConfig.AnnotationTools> tools) {
        llTools.setVisibility(tools != null && tools.size() > 0 ? VISIBLE : GONE);
        if (tools != null && tools.size() >0) {
            tools = CListUtil.distinct(tools);
        }
        for (AnnotationsConfig.AnnotationTools tool : tools) {
            AppCompatImageView toolView = (AppCompatImageView) LayoutInflater.from(getContext())
                    .inflate(R.layout.tools_annot_tool_bar_tools_item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    CDimensUtils.dp2px(getContext(), 30),CDimensUtils.dp2px(getContext(), 30)
            );
            layoutParams.setMarginStart(CDimensUtils.dp2px(getContext(), 12));
            toolView.setLayoutParams(layoutParams);
            switch (tool) {
                case Setting:
                    toolView.setEnabled(false);
                    toolView.setImageResource(R.drawable.tools_ic_annotation_setting);
                    toolView.setOnClickListener(proPerClickListener);
                    ivProper = toolView;
                    break;
                case Undo:
                    toolView.setImageResource(R.drawable.tools_ic_annotation_undo);
                    toolView.setOnClickListener(v -> {
                        pdfView.getCPdfReaderView().onEditUndo();
                    });
                    ivUndo = toolView;
                    updateUndo(false);
                    break;
                case Redo:
                    toolView.setImageResource(R.drawable.tools_ic_annotation_redo);
                    toolView.setOnClickListener(v -> {
                        pdfView.getCPdfReaderView().onEditRedo();
                    });
                    ivRedo = toolView;
                    updateRedo(false);
                    break;
                default:
                    break;
            }
            llTools.addView(toolView);
        }
    }


    public void resetStatus() {
        if (pdfView != null) {
            pdfView.getCPdfReaderView().removeAllAnnotFocus();
            updateUndo(false);
            updateRedo(false);
        }
        ivEditImage.setSelected(false);
        ivEditText.setSelected(false);
        if (ivProper != null) {
            ivProper.setEnabled(false);
        }
    }
}
