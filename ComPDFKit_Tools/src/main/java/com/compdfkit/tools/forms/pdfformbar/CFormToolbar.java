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
import android.util.Log;
import android.view.LayoutInflater;
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
import com.compdfkit.tools.common.pdf.config.FormsConfig;
import com.compdfkit.tools.common.utils.CListUtil;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.forms.pdfformbar.adapter.CPDFFormToolListAdapter;
import com.compdfkit.tools.forms.pdfformbar.bean.CFormToolBean;
import com.compdfkit.tools.forms.pdfformbar.data.CFormToolDatas;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;

import java.util.Arrays;
import java.util.Collections;
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
        llFormTool = findViewById(R.id.ll_form_attr);
        toolListAdapter = new CPDFFormToolListAdapter();
        rvFormList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFormList.setAdapter(toolListAdapter);
        initListener();
    }

    private void initListener() {
        toolListAdapter.setOnItemClickListener((adapter, view, position) -> {
            CFormToolBean bean = adapter.list.get(position);
            toolListAdapter.selectItem(position);
            switchFormType(bean);
        });
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        toolListAdapter.setList(CFormToolDatas.getFormList());
        pdfView.getCPdfReaderView().setSelectEditAreaChangeListener(type -> {
            CLog.e("SelectEdit", "type:"+ type);
        });
        redoUndoManager();
    }

    public void reset(){
        toolListAdapter.selectByType(CPDFWidget.WidgetType.Widget_Unknown);
        rvFormList.scrollToPosition(0);
        CPDFUndoManager undoManager = pdfView.getCPdfReaderView().getUndoManager();
        if (ivRedo != null) {
            ivRedo.setEnabled(undoManager.canRedo());
        }
        if (ivUndo != null) {
            ivUndo.setEnabled(undoManager.canUndo());
        }
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
        if (ivRedo != null) {
            ivRedo.setEnabled(undoManager.canRedo());
        }
        if (ivUndo != null) {
            ivUndo.setEnabled(undoManager.canUndo());
        }
        pdfView.getCPdfReaderView().getUndoManager().enable(true);
        pdfView.getCPdfReaderView().getUndoManager().addOnUndoHistoryChangeListener((cpdfUndoManager, operation, type) -> {
            boolean canUndo = cpdfUndoManager.canUndo();
            boolean canRedo = cpdfUndoManager.canRedo();
            handler.post(() -> {
                if (ivUndo != null) {
                    ivUndo.setEnabled(canUndo);
                }
                if (ivRedo != null) {
                    ivRedo.setEnabled(canRedo);
                }
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
        if (ivRedo != null) {
            ivRedo.setImageResource(drawableRes);
        }
    }

    public void setUndoImageResource(@DrawableRes int drawableRes){
        if (ivUndo != null) {
            ivUndo.setImageResource(drawableRes);
        }
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
        List<CFormToolBean> list = CFormToolDatas.getFormList();
        for (int i = list.size() - 1; i >= 0; i--) {
            CFormToolBean bean = list.get(i);
            if (!typeList.contains(bean.getType())) {
                list.remove(i);
            }
        }
        if (list.size() > 0) {
            Collections.sort(list, (o1, o2) -> {
                int index1 = typeList.indexOf(o1.getType());
                int index2 = typeList.indexOf(o2.getType());
                // 处理不在categorys中的元素
                if (index1 == -1 && index2 == -1) {
                    return 0; // 两个元素都不在categorys中，保持原有顺序
                } else if (index1 == -1) {
                    return 1; // item1不在categorys中，将item1排到后面
                } else if (index2 == -1) {
                    return -1; // item2不在categorys中，将item2排到后面
                }
                return Integer.compare(index1, index2);
            });
        }
        toolListAdapter.setList(list);
    }

    public void setTools(List<FormsConfig.FormsTools> tools) {
        llFormTool.setVisibility(tools != null && tools.size() > 0 ? VISIBLE : GONE);
        if (tools != null && tools.size() > 0){
            tools = CListUtil.distinct(tools);
        }
        for (FormsConfig.FormsTools tool : tools) {
            AppCompatImageView toolView = (AppCompatImageView) LayoutInflater.from(getContext())
                    .inflate(R.layout.tools_annot_tool_bar_tools_item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    CDimensUtils.dp2px(getContext(), 30),CDimensUtils.dp2px(getContext(), 30)
            );
            layoutParams.setMarginStart(CDimensUtils.dp2px(getContext(), 12));
            toolView.setLayoutParams(layoutParams);
            switch (tool) {
                case Undo:
                    toolView.setImageResource(R.drawable.tools_ic_annotation_undo);
                    toolView.setOnClickListener(v -> {
                        undo();
                    });
                    ivUndo = toolView;
                    break;
                case Redo:
                    toolView.setImageResource(R.drawable.tools_ic_annotation_redo);
                    toolView.setOnClickListener(v -> {
                        redo();
                    });
                    ivRedo = toolView;
                    break;
                default:
                    break;
            }
            llFormTool.addView(toolView);
        }
        redoUndoManager();
    }

    public void showUndoRedo(boolean show){
        llFormTool.setVisibility(show ? VISIBLE : GONE);
    }
}
