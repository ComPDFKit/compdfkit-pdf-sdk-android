/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationbar;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.undo.CPDFUndoManager;
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationbar.adapter.CPDFAnnotationToolListAdapter;
import com.compdfkit.tools.annotation.pdfannotationbar.bean.CAnnotToolBean;
import com.compdfkit.tools.annotation.pdfannotationbar.data.CAnnotationToolDatas;
import com.compdfkit.tools.common.interfaces.COnAnnotationChangeListener;
import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;
import com.compdfkit.tools.common.utils.CListUtil;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CBasicOnStyleChangeListener;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleUIParams;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.proxy.attach.IInkDrawCallback;
import com.compdfkit.ui.proxy.attach.IInkDrawCallback.Mode;
import com.compdfkit.ui.reader.CPDFReaderView;

import com.compdfkit.ui.reader.CPDFReaderView.ViewMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CAnnotationToolbar extends FrameLayout {

    private RecyclerView rvAnnotationList;

    @Nullable
    private AppCompatImageView ivSetting;

    @Nullable
    private AppCompatImageView ivUndo;

    @Nullable
    private AppCompatImageView ivRedo;

    public CPDFAnnotationToolListAdapter toolListAdapter;

    private CPDFViewCtrl pdfView;

    private COnAnnotationChangeListener annotationChangeListener;

    private LinearLayout llAnnotTools;

    public enum UndoManagerType {
        AnnotUndo,

        InkUndo
    }

    private UndoManagerType undoManagerType = UndoManagerType.AnnotUndo;

    public CAnnotationToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CAnnotationToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CAnnotationToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.tools_annot_tool_bar, this);
        rvAnnotationList = findViewById(R.id.rv_annotation);
        llAnnotTools = findViewById(R.id.ll_annotation_attr);
        toolListAdapter = new CPDFAnnotationToolListAdapter();
        rvAnnotationList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAnnotationList.setAdapter(toolListAdapter);
        initListener();
    }

    private void initListener() {
        toolListAdapter.setOnItemClickListener((adapter, view, position) -> {
            CAnnotToolBean bean = adapter.list.get(position);
            if (bean.isSelect()){
                switchAnnotationUnknown();
            }else {
                switchAnnotationType(bean.getType());
            }
        });
    }

    public void initWithPDFView(CPDFViewCtrl pdfView) {
        this.pdfView = pdfView;
        toolListAdapter.setList(CAnnotationToolDatas.getAnnotationList(pdfView));
        this.pdfView.addOnPDFFocusedTypeChangeListener(type -> {
            if (type == CPDFAnnotation.Type.UNKNOWN) {
                if (toolListAdapter.hasSelectAnnotType()) {
                    if (toolListAdapter.getCurrentAnnotType() != CAnnotationType.INK_ERASER){
                        toolListAdapter.selectByType(CAnnotationType.UNKNOWN);
                    }
                    if (ivSetting != null) {
                        ivSetting.setEnabled(toolListAdapter.annotEnableSetting());
                    }
                }
                if (toolListAdapter.getCurrentAnnotType() != CAnnotationType.INK_ERASER) {
                    setUndoManagerType(UndoManagerType.AnnotUndo);
                }
            }else if (type == CPDFAnnotation.Type.INK){
                if (toolListAdapter.getCurrentAnnotType() == CAnnotationType.INK) {
                    setUndoManagerType(UndoManagerType.InkUndo);
                }
            }else {
                setUndoManagerType(UndoManagerType.AnnotUndo);
            }
        });
    }
    private void setUndoManagerType(UndoManagerType undoManagerType) {
        this.undoManagerType = undoManagerType;
        if (undoManagerType == UndoManagerType.InkUndo) {
            CPDFReaderView.TInkDrawHelper helper = pdfView.getCPdfReaderView().getInkDrawHelper();
            if (ivUndo != null) {
                ivUndo.setEnabled(helper.canUndo());
            }
            if (ivRedo != null) {
                ivRedo.setEnabled(helper.canRedo());
            }
        } else {
            CPDFUndoManager undoManager = pdfView.getCPdfReaderView().getUndoManager();
            if (ivRedo != null) {
                ivRedo.setEnabled(undoManager.canRedo());
            }
            if (ivUndo != null) {
                ivUndo.setEnabled(undoManager.canUndo());
            }
        }

    }

    private void showAnnotStyleDialog() {
        saveInk();
        CViewUtils.hideKeyboard(this);
        CStyleManager styleManager = new CStyleManager(pdfView);
        CStyleType styleType = toolListAdapter.getCurrentAnnotType().getStyleType();
        CAnnotStyle style = styleManager.getStyle(styleType);
        CStyleUIParams styleUiParams = CStyleUIParams.defaultStyle(getContext(), styleType);
        CStyleDialogFragment dialogFragment = CStyleDialogFragment.newInstance(style);
        dialogFragment.setStyleUiConfig(styleUiParams);
        styleManager.setAnnotStyleFragmentListener(dialogFragment);
        dialogFragment.addAnnotStyleChangeListener(new CBasicOnStyleChangeListener() {
            @Override
            public void onChangeColor(int color) {
                super.onChangeColor(color);
                toolListAdapter.updateItemColor(toolListAdapter.getCurrentAnnotType(), color);
            }

            @Override
            public void onChangeOpacity(int opacity) {
                super.onChangeOpacity(opacity);
                toolListAdapter.updateItemColorOpacity(toolListAdapter.getCurrentAnnotType(), opacity);
            }

        });
        dialogFragment.setStyleDialogDismissListener(() -> {
            CAnnotStyle style1 = dialogFragment.getAnnotStyle();
            if (style1.getType() == CStyleType.ANNOT_STAMP || style1.getType() == CStyleType.ANNOT_SIGNATURE || style1.getType() == CStyleType.ANNOT_PIC) {
                if (style1.getTextStamp() == null
                        && style1.getStandardStamp() == null
                        && TextUtils.isEmpty(style1.getImagePath())) {
                    pdfView.resetAnnotationType();
                }
            }
        });
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
        if (fragmentActivity != null) {
            dialogFragment.show(fragmentActivity.getSupportFragmentManager(), "annotStyleDialogFragment");
        }
    }

    public void switchAnnotationUnknown(){
        if (toolListAdapter.getCurrentAnnotType() == CAnnotationType.INK_ERASER) {
            setUndoManagerType(UndoManagerType.AnnotUndo);
        }
        toolListAdapter.selectByType(CAnnotationType.UNKNOWN);
        if (ivSetting != null) {
            ivSetting.setEnabled(toolListAdapter.annotEnableSetting());
        }
        pdfView.resetAnnotationType();
        pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
        pdfView.getCPdfReaderView().getInkDrawHelper().setMode(Mode.DRAW);
        if (annotationChangeListener != null) {
            annotationChangeListener.change(CAnnotationType.UNKNOWN);
        }
    }

    public void switchAnnotationType(CAnnotationType type) {
        toolListAdapter.selectByType(type);
        if (ivSetting != null) {
            ivSetting.setEnabled(toolListAdapter.annotEnableSetting());
        }

        pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
        pdfView.getCPdfReaderView().removeAllAnnotFocus();
        switch (type) {
            case TEXT:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.TEXT);
                break;
            case INK:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.INK);
                pdfView.getCPdfReaderView().getInkDrawHelper().setMode(Mode.DRAW);
                pdfView.getCPdfReaderView().getInkDrawHelper().setEffect(IInkDrawCallback.Effect.NORMAL);
                break;
            case INK_ERASER:
                setUndoManagerType(UndoManagerType.AnnotUndo);
                pdfView.resetAnnotationType();
                pdfView.getCPdfReaderView().setTouchMode(CPDFReaderView.TouchMode.ERASE_INK);
                break;
            case ARROW: {
                pdfView.changeAnnotationType(CPDFAnnotation.Type.LINE);
                CStyleManager styleManager = new CStyleManager(pdfView);
                CAnnotStyle style = styleManager.getStyle(CStyleType.ANNOT_ARROW);
                styleManager.updateStyle(style);
                break;
            }
            case LINE:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.LINE);
                CStyleManager styleManager = new CStyleManager(pdfView);
                CAnnotStyle style = styleManager.getStyle(CStyleType.ANNOT_LINE);
                styleManager.updateStyle(style);
                break;
            case SIGNATURE:
            case STAMP:
            case PIC:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.STAMP);
                showAnnotStyleDialog();
                break;
            default:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.valueOf(type.name()));
                break;
        }
        if (annotationChangeListener != null) {
            annotationChangeListener.change(type);
        }
    }

    private void redoUndoManager() {
        pdfView.getCPdfReaderView().getUndoManager().enable(true);
        CPDFUndoManager undoManager = pdfView.getCPdfReaderView().getUndoManager();
        if (ivRedo != null) {
            ivRedo.setEnabled(undoManager.canRedo());
        }
        if (ivUndo != null) {
            ivUndo.setEnabled(undoManager.canUndo());
        }
        pdfView.getCPdfReaderView().getUndoManager().addOnUndoHistoryChangeListener((cpdfUndoManager, operation, type) -> {
            if (undoManagerType == UndoManagerType.AnnotUndo){
                boolean canUndo = cpdfUndoManager.canUndo();
                boolean canRedo = cpdfUndoManager.canRedo();
                if (ivUndo != null) {
                    ivUndo.setEnabled(canUndo);
                }
                if (ivRedo != null) {
                    ivRedo.setEnabled(canRedo);
                }
            }
        });

        CPDFReaderView.TInkDrawHelper helper = pdfView.getCPdfReaderView().getInkDrawHelper();
        helper.setInkUndoRedoCallback((undo, redo) -> {
            if (undoManagerType == UndoManagerType.InkUndo) {
                if (ivUndo != null) {
                    ivUndo.setEnabled(undo);
                }
                if (ivRedo != null) {
                    ivRedo.setEnabled(redo);
                }
            }
        });
    }

    public void undo() {
        if (undoManagerType == UndoManagerType.InkUndo) {
            inkUndo();
        }else {
            annotUndo();
        }
    }

    public void redo() {
        if (undoManagerType == UndoManagerType.InkUndo) {
            inkRedo();
        }else {
            annotRedo();
        }
    }

    public void annotUndo(){
        try {
            CPDFUndoManager undoManager = pdfView.getCPdfReaderView().getUndoManager();
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (Exception e) {
        }
    }

    public void annotRedo() {
        try {
            CPDFUndoManager undoManager = pdfView.getCPdfReaderView().getUndoManager();
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (Exception e) {

        }
    }

    private void inkUndo(){
        if (pdfView != null) {
            pdfView.getCPdfReaderView().getInkDrawHelper().onUndo();
        }
    }

    private void inkRedo() {
        if (pdfView != null) {
            pdfView.getCPdfReaderView().getInkDrawHelper().onRedo();
        }
    }

    public void updateItemColor() {
        if (pdfView == null) {
            return;
        }
        if (toolListAdapter == null || toolListAdapter.list.isEmpty()){
            return;
        }
        CStyleManager styleManager = new CStyleManager(pdfView);
        CAnnotStyle noteStyle = styleManager.getStyle(CStyleType.ANNOT_TEXT);
        toolListAdapter.updateItem(CAnnotationType.TEXT, noteStyle.getColor(), noteStyle.getOpacity());

        CAnnotStyle highStyle = styleManager.getStyle(CStyleType.ANNOT_HIGHLIGHT);
        toolListAdapter.updateItem(CAnnotationType.HIGHLIGHT, highStyle.getColor(), highStyle.getOpacity());

        CAnnotStyle underLineStyle = styleManager.getStyle(CStyleType.ANNOT_UNDERLINE);
        toolListAdapter.updateItem(CAnnotationType.UNDERLINE, underLineStyle.getColor(), underLineStyle.getOpacity());

        CAnnotStyle strikeoutStyle = styleManager.getStyle(CStyleType.ANNOT_STRIKEOUT);
        toolListAdapter.updateItem(CAnnotationType.STRIKEOUT, strikeoutStyle.getColor(), strikeoutStyle.getOpacity());

        CAnnotStyle squigglyStyle = styleManager.getStyle(CStyleType.ANNOT_SQUIGGLY);
        toolListAdapter.updateItem(CAnnotationType.SQUIGGLY, squigglyStyle.getColor(), squigglyStyle.getOpacity());

        CAnnotStyle inkStyle = styleManager.getStyle(CStyleType.ANNOT_INK);
        toolListAdapter.updateItem(CAnnotationType.INK, inkStyle.getColor(), inkStyle.getOpacity());
    }

    public void setAnnotationChangeListener(COnAnnotationChangeListener annotationChangeListener) {
        this.annotationChangeListener = annotationChangeListener;
    }

    public void setTools(List<AnnotationsConfig.AnnotationTools> tools) {
        llAnnotTools.setVisibility(tools != null && tools.size() > 0 ? VISIBLE : GONE);
        if (tools != null && tools.size() > 0) {
            tools = CListUtil.distinct(tools);
        }
        for (AnnotationsConfig.AnnotationTools tool : tools) {
            AppCompatImageView toolView = (AppCompatImageView) LayoutInflater.from(getContext())
                    .inflate(R.layout.tools_annot_tool_bar_tools_item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    CDimensUtils.dp2px(getContext(), 30), CDimensUtils.dp2px(getContext(), 30)
            );
            layoutParams.setMarginStart(CDimensUtils.dp2px(getContext(), 12));
            toolView.setLayoutParams(layoutParams);
            switch (tool) {
                case Setting:
                    toolView.setEnabled(false);
                    toolView.setImageResource(R.drawable.tools_ic_annotation_setting);
                    toolView.setOnClickListener(v -> {
                        showAnnotStyleDialog();
                    });
                    ivSetting = toolView;
                    break;
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
            llAnnotTools.addView(toolView);
        }
        redoUndoManager();
    }

    private void saveInk() {
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        if (readerView.getViewMode() == ViewMode.ANNOT){
            if (readerView.getCurrentFocusedType() == CPDFAnnotation.Type.INK){
                readerView.getInkDrawHelper().onSave();
            }
        }
    }



    public AppCompatImageView getSettingButton() {
        return ivSetting;
    }

    public AppCompatImageView getRedoButton() {
        return ivRedo;
    }

    public AppCompatImageView getUndoButton() {
        return ivUndo;
    }

    public void setSettingImageResource(@DrawableRes int drawableRes) {
        if (ivSetting != null) {
            ivSetting.setImageResource(drawableRes);
            ivSetting.setImageTintList(null);
        }
    }

    public void setRedoImageResource(@DrawableRes int drawableRes) {
        if (ivRedo != null) {
            ivRedo.setImageResource(drawableRes);
            ivRedo.setImageTintList(null);
        }
    }

    public void setUndoImageResource(@DrawableRes int drawableRes) {
        if (ivUndo != null) {
            ivUndo.setImageResource(drawableRes);
            ivUndo.setImageTintList(null);
        }
    }

    public void setAnnotationList(List<CAnnotToolBean> list) {
        toolListAdapter.setList(list);
    }

    public void setAnnotationList(CAnnotationType... types) {
        if (pdfView == null) {
            CLog.e("ComPDFKit_Tools", "CAnnotationToolbar.setAnnotationList(), pdfView cannot be null");
            return;
        }
        if (toolListAdapter == null) {
            CLog.e("ComPDFKit_Tools", "CAnnotationToolbar.toolListAdapter, toolListAdapter cannot be null");
            return;
        }
        List<CAnnotationType> typeList = Arrays.asList(types);
        List<CAnnotToolBean> list = CAnnotationToolDatas.getAnnotationList(pdfView);
        for (int i = list.size() - 1; i >= 0; i--) {
            CAnnotToolBean bean = list.get(i);
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

    public void reset() {
        toolListAdapter.selectByType(CAnnotationType.UNKNOWN);
        rvAnnotationList.scrollToPosition(0);
        redoUndoManager();
    }
}
