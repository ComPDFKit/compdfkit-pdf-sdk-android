/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.contenteditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.core.edit.OnEditStatusChangeListener;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;

public class CEditToolbar extends RelativeLayout implements View.OnClickListener {
    public static final int SELECT_AREA_NONE = 0;
    public static final int SELECT_AREA_TEXT = 1;
    public static final int SELECT_AREA_IMAGE = 2;

    private CPDFViewCtrl pdfView;
    private androidx.appcompat.widget.AppCompatImageView ivEditText;
    private androidx.appcompat.widget.AppCompatImageView ivEditImage;
    private androidx.appcompat.widget.AppCompatImageView ivProper;
    private androidx.appcompat.widget.AppCompatImageView ivUndo;
    private androidx.appcompat.widget.AppCompatImageView ivRedo;

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
        ivEditText = findViewById(R.id.iv_edit_text);
        ivEditImage = findViewById(R.id.iv_edit_image);
        ivProper = findViewById(R.id.iv_proper);
        ivUndo = findViewById(R.id.iv_undo);
        ivRedo = findViewById(R.id.iv_redo);
        ivEditText.setOnClickListener(this);
        ivEditImage.setOnClickListener(this);
        ivEditText.setSelected(false);
        ivEditImage.setSelected(false);
        ivUndo.setOnClickListener(this);
        ivRedo.setOnClickListener(this);
        ivProper.setEnabled(false);
        updateUndo(false);
        updateRedo(false);
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
    }

    public void updateUndo(boolean canUndo) {
        ivUndo.setEnabled(canUndo);
    }

    public void updateRedo(boolean canRedo) {
        ivRedo.setEnabled(canRedo);
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
            pdfView.getCPdfReaderView().getEditManager().changeEditType(CPDFEditPage.LoadTextImage);
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
            if (sel == false) {
                ivEditImage.setSelected(false);
            }
            changeEditType();
        } else if (view.getId() == R.id.iv_edit_image) {
            boolean sel = view.isSelected();
            view.setSelected(!sel);
            if (sel == false) {
                ivEditText.setSelected(false);
            }
            changeEditType();
        } else if (view.getId() == R.id.iv_undo) {
            pdfView.getCPdfReaderView().onEditUndo();
        } else if (view.getId() == R.id.iv_redo) {
            pdfView.getCPdfReaderView().onEditRedo();
        }
    }

    public void setEditPropertyBtnClickListener(View.OnClickListener listener) {
        ivProper.setOnClickListener(listener);
    }

    public void setEditMode(boolean beginedit) {
        if (pdfView == null || pdfView.getCPdfReaderView() == null) {
            return;
        }
        CPDFEditManager editManager = pdfView.getCPdfReaderView().getEditManager();
        if (editManager == null) {
            return;
        }
        editManager.enable();
        pdfView.getCPdfReaderView().setSelectEditAreaChangeListener(type -> {
            if (type == SELECT_AREA_NONE) {
                ivProper.setEnabled(false);
            } else {
                ivProper.setEnabled(true);
            }
        });

        editManager.addEditStatusChangeListener(new OnEditStatusChangeListener() {
            @Override
            public void onBegin(int i) {
                CThreadPoolUtils.getInstance().executeMain(() -> {
                    if (pdfView == null || pdfView.getCPdfReaderView() == null || pdfView.getCPdfReaderView().getEditManager() == null) {
                        return;
                    }
                    int type = pdfView.getCPdfReaderView().getLoadType();
                    if (type == CPDFEditPage.LoadText) {
                        ivEditText.setSelected(true);
                        ivEditImage.setSelected(false);
                    } else if (type == CPDFEditPage.LoadImage) {
                        ivEditText.setSelected(false);
                        ivEditImage.setSelected(true);
                    } else if (type == CPDFEditPage.LoadTextImage) {
                        ivEditText.setSelected(false);
                        ivEditImage.setSelected(false);
                    }
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
        if (beginedit) {
            editManager.beginEdit(CPDFEditPage.LoadTextImage);
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
        ivProper.setEnabled(false);
    }
}
