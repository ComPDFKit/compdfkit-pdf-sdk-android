/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdflnk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.proxy.attach.IInkDrawCallback;
import com.compdfkit.ui.reader.CPDFReaderView;

/**
 * Create a PDF Ink Control View that allows for controlling ink annotations with options for switching between erase,
 * undo, redo, delete, and confirm operations.
 */
public class CInkCtrlView extends FrameLayout implements View.OnClickListener {

    private AppCompatImageView ivSetting;

    private AppCompatImageView ivEraser;

    private AppCompatImageView ivUndo;

    private AppCompatImageView ivRedo;

    private CPDFViewCtrl pdfView;

    public CInkCtrlView(@NonNull Context context) {
        this(context, null);
    }

    public CInkCtrlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CInkCtrlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_properties_ink_ctrl_layout, this);
        ivSetting = findViewById(R.id.iv_ink_setting);
        ivEraser = findViewById(R.id.iv_ink_eraser);
        ivUndo = findViewById(R.id.iv_ink_undo);
        ivRedo = findViewById(R.id.iv_ink_redo);
        AppCompatTextView tvClear = findViewById(R.id.tv_ink_clean);
        AppCompatTextView tvSave = findViewById(R.id.tv_ink_save);
        ivSetting.setOnClickListener(this);
        ivEraser.setOnClickListener(this);
        ivUndo.setOnClickListener(this);
        ivRedo.setOnClickListener(this);
        tvClear.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        CPDFReaderView.TInkDrawHelper helper = pdfView.getCPdfReaderView().getInkDrawHelper();
        helper.setInkUndoRedoCallback((b, b1) -> {
            ivUndo.setEnabled(b);
            ivRedo.setEnabled(b1);
        });
        ivUndo.setEnabled(helper.canUndo());
        ivRedo.setEnabled(helper.canRedo());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_ink_eraser) {
            if (pdfView != null) {
                if (ivEraser.isSelected()) {
                    ivEraser.setSelected(false);
                    pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.DRAW);
                } else {
                    ivEraser.setSelected(true);
                    pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.ERASE);
                }
            }
        } else if (v.getId() == R.id.iv_ink_undo) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().getInkDrawHelper().onUndo();
            }
        } else if (v.getId() == R.id.iv_ink_redo) {
            if (pdfView != null) {
                pdfView.getCPdfReaderView().getInkDrawHelper().onRedo();
            }
        } else if (v.getId() == R.id.tv_ink_clean) {
            if (pdfView != null) {
                ivEraser.setSelected(false);
                pdfView.getCPdfReaderView().getInkDrawHelper().onClean();
                pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.DRAW);
                pdfView.resetAnnotationType();
            }
        } else if (v.getId() == R.id.tv_ink_save) {
            if (pdfView != null) {
                ivEraser.setSelected(false);
                pdfView.getCPdfReaderView().getInkDrawHelper().setMode(IInkDrawCallback.Mode.DRAW);
                pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
                pdfView.resetAnnotationType();
            }
        } else if (v.getId() == R.id.iv_ink_setting) {
            ivSetting.setSelected(true);
            CStyleManager styleManager = new CStyleManager(pdfView);
            CAnnotStyle style = styleManager.getStyle(CStyleType.ANNOT_INK);
            CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(style);
            styleDialogFragment.setStyleDialogDismissListener(()->{
                ivSetting.setSelected(false);
                changeAnnotToolbarInkColor();
            });
            styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
            if (fragmentActivity != null) {
                styleDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "annotStyleDialogFragment");
            }
        }
    }

    private void changeAnnotToolbarInkColor(){
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity != null){
            Fragment fragment = fragmentActivity.getSupportFragmentManager()
                    .findFragmentByTag("documentFragment");
            if (fragment != null && fragment instanceof CPDFDocumentFragment){
                CPDFDocumentFragment documentFragment = (CPDFDocumentFragment) fragment;
                documentFragment.annotationToolbar.updateItemColor();
            }
        }
    }
}
