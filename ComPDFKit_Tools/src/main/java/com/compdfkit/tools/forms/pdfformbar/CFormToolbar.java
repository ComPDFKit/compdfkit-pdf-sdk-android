/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfformbar;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.undo.CPDFUndoManager;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.COnFormChangeListener;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.forms.pdfformbar.adapter.CPDFFormToolListAdapter;
import com.compdfkit.tools.forms.pdfformbar.bean.CFormToolBean;
import com.compdfkit.tools.forms.pdfformbar.data.CFormToolDatas;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;

import java.util.Arrays;
import java.util.List;

public class CFormToolbar extends FrameLayout {

    private RecyclerView rvFormList;

    private AppCompatImageView ivUndo;

    private AppCompatImageView ivRedo;

    private CPDFFormToolListAdapter toolListAdapter;

    private CPDFViewCtrl pdfView;

    private LinearLayout llFormTool;

    private FragmentManager fragmentManager;

    private COnFormChangeListener onFormChangeListener;

    private Handler handler = new Handler(Looper.getMainLooper());

    public CFormToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CFormToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CFormToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_form_tool_bar, this);
        rvFormList = findViewById(R.id.rv_form);
        ivUndo = findViewById(R.id.iv_form_attr_undo);
        ivRedo = findViewById(R.id.iv_form_attr_redo);
        llFormTool = findViewById(R.id.ll_form_attr);
        toolListAdapter = new CPDFFormToolListAdapter();
        rvFormList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFormList.setAdapter(toolListAdapter);
        initListener();
    }

    private void initListener() {
        ivUndo.setOnClickListener(v -> undo());
        ivRedo.setOnClickListener(v -> redo());
        toolListAdapter.setOnItemClickListener((adapter, view, position) -> {
            CFormToolBean bean = adapter.list.get(position);
            toolListAdapter.selectItem(position);
            switchFormType(bean);
        });
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        toolListAdapter.setList(CFormToolDatas.getFormList(pdfView));
        pdfView.getCPdfReaderView().setSelectEditAreaChangeListener(type -> {
            CLog.e("SelectEdit", "type:"+ type);
        });
        redoUndoManager();
    }

    public void reset(){
        toolListAdapter.selectByType(CPDFWidget.WidgetType.Widget_Unknown);
        rvFormList.scrollToPosition(0);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private void switchFormType(CFormToolBean bean) {
        if (!bean.isSelect()) {
            pdfView.resetFormType();
            pdfView.getCPdfReaderView().getInkDrawHelper().onClean();
            if (onFormChangeListener != null) {
                onFormChangeListener.change(CPDFWidget.WidgetType.Widget_Unknown);
            }
            return;
        }
        IContextMenuShowListener listener = pdfView.getCPdfReaderView().getContextMenuShowListener();
        if (listener != null){
            listener.dismissContextMenu();
        }
        pdfView.changeFormType(bean.getType());
        if (onFormChangeListener != null) {
            onFormChangeListener.change(bean.getType());
        }
    }


    private void redoUndoManager() {
        CPDFUndoManager undoManager = pdfView.getCPdfReaderView().getUndoManager();
        ivRedo.setEnabled(undoManager.canRedo());
        ivUndo.setEnabled(undoManager.canRedo());
        pdfView.getCPdfReaderView().getUndoManager().enable(true);
        pdfView.getCPdfReaderView().getUndoManager().addOnUndoHistoryChangeListener((cpdfUndoManager, operation, type) -> {
            boolean canUndo = cpdfUndoManager.canUndo();
            boolean canRedo = cpdfUndoManager.canRedo();
            handler.post(() -> {
                ivUndo.setEnabled(canUndo);
                ivRedo.setEnabled(canRedo);
            });
        });
    }

    private void undo() {
        try {
            if (pdfView != null) {
                IContextMenuShowListener contextMenuShowListener =
                        pdfView.getCPdfReaderView().getContextMenuShowListener();
                if (contextMenuShowListener != null) {
                    contextMenuShowListener.dismissContextMenu();
                }
                pdfView.getCPdfReaderView().getUndoManager().undo();
            }
        } catch (Exception e) {
        }
    }

    private void redo() {
        try {
            if (pdfView != null) {
                IContextMenuShowListener contextMenuShowListener =
                        pdfView.getCPdfReaderView().getContextMenuShowListener();
                if (contextMenuShowListener != null) {
                    contextMenuShowListener.dismissContextMenu();
                }
                pdfView.getCPdfReaderView().getUndoManager().redo();
            }
        } catch (Exception e) {
        }
    }

    public void setOnFormChangeListener(COnFormChangeListener onFormChangeListener) {
        this.onFormChangeListener = onFormChangeListener;
    }

    public AppCompatImageView getRedoButton() {
        return ivRedo;
    }

    public AppCompatImageView getUndoButton() {
        return ivUndo;
    }

    public void setRedoImageResource(@DrawableRes int drawableRes){
        ivRedo.setImageResource(drawableRes);
    }

    public void setUndoImageResource(@DrawableRes int drawableRes){
        ivRedo.setImageResource(drawableRes);
    }

    public void setFormList(List<CFormToolBean> list){
        toolListAdapter.setList(list);
    }

    public void setFormList(CPDFWidget.WidgetType... types){
        if (pdfView == null){
            CLog.e("ComPDFKit_Tools", "CFormToolbar.setFormList(), pdfView cannot be null");
            return;
        }
        if (toolListAdapter == null){
            CLog.e("ComPDFKit_Tools", "CFormToolbar.toolListAdapter, toolListAdapter cannot be null");
            return;
        }
        List<CPDFWidget.WidgetType> typeList = Arrays.asList(types);
        List<CFormToolBean> list = CFormToolDatas.getFormList(pdfView);
        for (int i = list.size() - 1; i >= 0; i--) {
            CFormToolBean bean = list.get(i);
            if (!typeList.contains(bean.getType())) {
                list.remove(i);
            }
        }
        toolListAdapter.setList(list);
    }

    public void showUndoRedo(boolean show){
        llFormTool.setVisibility(show ? VISIBLE : GONE);
    }
}
