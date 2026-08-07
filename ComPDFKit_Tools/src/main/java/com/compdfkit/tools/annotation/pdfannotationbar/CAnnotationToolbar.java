/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfannotationbar;


import android.content.Context;
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
import com.compdfkit.tools.R;
import com.compdfkit.tools.annotation.pdfannotationbar.adapter.CPDFAnnotationToolListAdapter;
import com.compdfkit.tools.annotation.pdfannotationbar.bean.CAnnotToolBean;
import com.compdfkit.tools.annotation.pdfannotationbar.data.CAnnotationToolDatas;
import com.compdfkit.tools.common.interfaces.COnAnnotationChangeListener;
import com.compdfkit.tools.common.interfaces.COnAnnotationCreatePreparedListener;
import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;
import com.compdfkit.tools.common.pdf.undo.InkUndoRedoCoordinator;
import com.compdfkit.tools.common.utils.CListUtil;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.tools.common.utils.viewutils.CDimensUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfproperties.CAnnotationType;
import com.compdfkit.tools.common.views.pdfproperties.CTypeUtil;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CBasicOnStyleChangeListener;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleUIParams;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.proxy.attach.IInkDrawCallback;
import com.compdfkit.ui.proxy.attach.IInkDrawCallback.Mode;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.reader.CPDFReaderView.ViewMode;
import com.compdfkit.ui.reader.CPDFSelectAnnotCallback;
import com.compdfkit.ui.reader.OnViewModeChangedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    private List<COnAnnotationChangeListener> annotationChangeListeners = new ArrayList<>();

    private List<COnAnnotationCreatePreparedListener> annotationCreatePreparedListeners = new ArrayList<>();

    private LinearLayout llAnnotTools;

    @Nullable
    private CPDFBaseAnnotImpl<CPDFAnnotation> selectedAnnotImpl;

    @Nullable
    private CPDFPageView selectedAnnotPageView;

    @Nullable
    private CPDFSelectAnnotCallback selectAnnotCallback;

    @Nullable
    private OnViewModeChangedListener viewModeChangedListener;

    private boolean listeningSelectAnnot = false;

    private boolean isInkEditing;

    @Nullable
    private InkUndoRedoCoordinator undoRedoCoordinator;

    private final InkUndoRedoCoordinator.OnStateChangedListener undoRedoStateListener =
            this::updateUndoRedoButtons;

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
        undoRedoCoordinator = InkUndoRedoCoordinator.get(pdfView.getCPdfReaderView());
        undoRedoCoordinator.addOnStateChangedListener(undoRedoStateListener);
        toolListAdapter.setList(CAnnotationToolDatas.getAnnotationList(pdfView));
        setupSelectedAnnotationCallbacks();
        this.pdfView.addOnPDFFocusedTypeChangeListener(type -> {
            if (type == CPDFAnnotation.Type.UNKNOWN) {
                clearSelectedAnnotationStyleTarget();
                if (toolListAdapter.hasSelectAnnotType()) {
                    if (toolListAdapter.getCurrentAnnotType() != CAnnotationType.INK_ERASER){
                        toolListAdapter.selectByType(CAnnotationType.UNKNOWN);
                    }
                    updateSettingButtonState();
                }
                if (toolListAdapter.getCurrentAnnotType() != CAnnotationType.INK_ERASER) {
                    setInkEditing(false);
                }
            }else if (type == CPDFAnnotation.Type.INK){
                if (toolListAdapter.getCurrentAnnotType() == CAnnotationType.INK) {
                    setInkEditing(true);
                }
            }else {
                setInkEditing(false);
            }
        });
        syncSelectAnnotListenerWithViewMode(pdfView.getCPdfReaderView().getViewMode());
    }

    private void setupSelectedAnnotationCallbacks() {
        if (selectAnnotCallback == null) {
            selectAnnotCallback = new CPDFSelectAnnotCallback() {
                @Override
                public void onAnnotationSelected(CPDFPageView pageView,
                                                 CPDFBaseAnnotImpl<CPDFAnnotation> annotImpl) {
                    if (canShowSelectedAnnotationStyleDialog(annotImpl)) {
                        selectedAnnotPageView = pageView;
                        selectedAnnotImpl = annotImpl;
                    } else {
                        clearSelectedAnnotationStyleTarget();
                    }
                    updateSettingButtonState();
                }

                @Override
                public void onAnnotationDeselected(CPDFPageView pageView,
                                                   CPDFBaseAnnotImpl<CPDFAnnotation> annotImpl) {
                    if (selectedAnnotImpl == annotImpl
                            || (selectedAnnotImpl != null && annotImpl != null
                            && selectedAnnotImpl.getId() == annotImpl.getId())) {
                        clearSelectedAnnotationStyleTarget();
                        updateSettingButtonState();
                    }
                }
            };
        }
        if (viewModeChangedListener == null) {
            viewModeChangedListener = this::syncSelectAnnotListenerWithViewMode;
            pdfView.addOnPDFViewModeChangeListener(viewModeChangedListener);
        }
    }

    private void syncSelectAnnotListenerWithViewMode(ViewMode viewMode) {
        if (viewMode == ViewMode.ANNOT) {
            startListenSelectAnnot();
        } else {
            stopListenSelectAnnot();
            clearSelectedAnnotationStyleTarget();
            updateSettingButtonState();
        }
    }

    private void startListenSelectAnnot() {
        if (!listeningSelectAnnot && pdfView != null && selectAnnotCallback != null) {
            pdfView.addOnPDFSelectAnnotChangeListener(selectAnnotCallback);
            listeningSelectAnnot = true;
        }
    }

    private void stopListenSelectAnnot() {
        if (listeningSelectAnnot && pdfView != null && selectAnnotCallback != null) {
            pdfView.removeOnPDFSelectAnnotChangeListener(selectAnnotCallback);
            listeningSelectAnnot = false;
        }
    }

    private void clearSelectedAnnotationStyleTarget() {
        selectedAnnotImpl = null;
        selectedAnnotPageView = null;
    }

    private boolean hasSelectedAnnotationStyleTarget() {
        return isAnnotationMode()
                && selectedAnnotImpl != null
                && selectedAnnotPageView != null
                && canShowSelectedAnnotationStyleDialog(selectedAnnotImpl);
    }

    private boolean isAnnotationMode() {
        return pdfView != null && pdfView.getCPdfReaderView().getViewMode() == ViewMode.ANNOT;
    }

    private boolean canShowSelectedAnnotationStyleDialog(@Nullable CPDFBaseAnnotImpl<CPDFAnnotation> annotImpl) {
        if (annotImpl == null) {
            return false;
        }
        switch (annotImpl.getAnnotType()) {
            case TEXT:
            case HIGHLIGHT:
            case UNDERLINE:
            case SQUIGGLY:
            case STRIKEOUT:
            case INK:
            case SQUARE:
            case CIRCLE:
            case LINE:
            case FREETEXT:
                return true;
            default:
                return false;
        }
    }

    private void updateSettingButtonState() {
        if (ivSetting != null) {
            ivSetting.setEnabled(hasSelectedAnnotationStyleTarget()
                    || toolListAdapter.annotEnableSetting());
        }
    }
    private void setInkEditing(boolean isInkEditing) {
        this.isInkEditing = isInkEditing;
        updateUndoRedoButtons();
    }

    private void updateUndoRedoButtons() {
        if (pdfView == null) {
            return;
        }
        if (undoRedoCoordinator == null) {
            return;
        }
        if (ivUndo != null) {
            ivUndo.setEnabled(undoRedoCoordinator.canUndo(getInkUndoRedoMode(), isInkEditing));
        }
        if (ivRedo != null) {
            ivRedo.setEnabled(undoRedoCoordinator.canRedo(getInkUndoRedoMode(), isInkEditing));
        }
    }

    private AnnotationsConfig.InkUndoRedoMode getInkUndoRedoMode() {
        if (pdfView == null || pdfView.getCPDFConfiguration() == null
                || pdfView.getCPDFConfiguration().annotationsConfig == null
                || pdfView.getCPDFConfiguration().annotationsConfig.inkUndoRedoMode == null) {
            return AnnotationsConfig.InkUndoRedoMode.HYBRID;
        }
        return pdfView.getCPDFConfiguration().annotationsConfig.inkUndoRedoMode;
    }

    private void showAnnotStyleDialog() {
        if (hasSelectedAnnotationStyleTarget()) {
            showSelectedAnnotStyleDialog();
            return;
        }
        CStyleType styleType = toolListAdapter.getCurrentAnnotType().getStyleType();
        showAnnotStyleDialog(styleType);
    }

    private void showSelectedAnnotStyleDialog() {
        saveInk();
        CViewUtils.hideKeyboard(this);
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(getContext());
        CPDFBaseAnnotImpl<CPDFAnnotation> annotImpl = selectedAnnotImpl;
        CPDFPageView pageView = selectedAnnotPageView;
        if (fragmentActivity != null && annotImpl != null && pageView != null) {
            CPDFAnnotationManager.showPropertiesDialog(
                    fragmentActivity.getSupportFragmentManager(), annotImpl, pageView);
        }
    }

    public void showAnnotStyleDialog(CStyleType styleType) {
        saveInk();
        CViewUtils.hideKeyboard(this);
        CStyleManager styleManager = new CStyleManager(pdfView);
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

    private boolean hasAnnotationStyleSelection(CAnnotStyle style) {
        return style.getTextStamp() != null
                || style.getStandardStamp() != null
                || !TextUtils.isEmpty(style.getImagePath());
    }

    public void switchAnnotationUnknown(){
        if (toolListAdapter.getCurrentAnnotType() == CAnnotationType.INK_ERASER) {
            setInkEditing(false);
        }
        toolListAdapter.selectByType(CAnnotationType.UNKNOWN);
        clearSelectedAnnotationStyleTarget();
        updateSettingButtonState();
        pdfView.resetAnnotationType();
        pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
        invalidateRedoHistory();
        pdfView.getCPdfReaderView().getInkDrawHelper().setMode(Mode.DRAW);
        annotationChangeListenersChanged(CAnnotationType.UNKNOWN);
    }

    public void switchAnnotationType(CAnnotationType type) {
        if(type == CAnnotationType.UNKNOWN){
            switchAnnotationUnknown();
            return;
        }
        toolListAdapter.selectByType(type);
        clearSelectedAnnotationStyleTarget();
        updateSettingButtonState();
        AnnotationsConfig annotationsConfig = pdfView.getCPDFConfiguration().annotationsConfig;
        pdfView.getCPdfReaderView().getInkDrawHelper().onSave();
        invalidateRedoHistory();
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
                setInkEditing(false);
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
                pdfView.changeAnnotationType(CPDFAnnotation.Type.STAMP);
                if (annotationsConfig.autoShowSignPicker){
                    showAnnotStyleDialog();
                } else {
                    annotationCreatePreparedListenersChanged(CAnnotationType.SIGNATURE, null);
                }
                break;
            case STAMP:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.STAMP);
                if (annotationsConfig.autoShowStampPicker){
                    showAnnotStyleDialog();
                } else {
                    annotationCreatePreparedListenersChanged(CAnnotationType.STAMP, null);
                }
                break;
            case PIC:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.STAMP);
                if (annotationsConfig.autoShowPicPicker){
                    showAnnotStyleDialog();
                } else {
                    annotationCreatePreparedListenersChanged(CAnnotationType.PIC, null);
                }
                break;
            default:
                pdfView.changeAnnotationType(CPDFAnnotation.Type.valueOf(type.name()));
                break;
        }
        annotationChangeListenersChanged(type);
    }

    public void annotationChangeListenersChanged(CAnnotationType type) {
        for (COnAnnotationChangeListener listener : annotationChangeListeners) {
            listener.change(type);
        }
    }

    public void annotationCreatePreparedListenersChanged(CAnnotationType type,CPDFAnnotation annotation) {
        for (COnAnnotationCreatePreparedListener listener : annotationCreatePreparedListeners) {
            listener.prepared(type, annotation);
        }
    }

    private void redoUndoManager() {
        if (undoRedoCoordinator == null) {
            undoRedoCoordinator = InkUndoRedoCoordinator.get(pdfView.getCPdfReaderView());
            undoRedoCoordinator.addOnStateChangedListener(undoRedoStateListener);
        }
        updateUndoRedoButtons();
    }

    public void undo() {
        if (pdfView == null) {
            return;
        }
        if (undoRedoCoordinator != null) {
            undoRedoCoordinator.undo(getInkUndoRedoMode(), isInkEditing);
        }
        updateUndoRedoButtons();
    }

    public void redo() {
        if (pdfView == null) {
            return;
        }
        if (undoRedoCoordinator != null) {
            undoRedoCoordinator.redo(getInkUndoRedoMode(), isInkEditing);
        }
        updateUndoRedoButtons();
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
                    updateSettingButtonState();
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
                invalidateRedoHistory();
            }
        }
    }

    private void invalidateRedoHistory() {
        if (undoRedoCoordinator != null) {
            undoRedoCoordinator.invalidateRedoHistory();
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

                if (index1 == -1 && index2 == -1) {
                    return 0; 
                } else if (index1 == -1) {
                    return 1; 
                } else if (index2 == -1) {
                    return -1; 
                }

                return Integer.compare(index1, index2);
            });
        }
        toolListAdapter.setList(list);
    }

    public void reset() {
        toolListAdapter.selectByType(CAnnotationType.UNKNOWN);
        clearSelectedAnnotationStyleTarget();
        updateSettingButtonState();
        rvAnnotationList.scrollToPosition(0);
        redoUndoManager();
    }

    public void addAnnotationChangeListener(COnAnnotationChangeListener listener) {
        annotationChangeListeners.add(listener);
    }

    public void addAnnotationCreatePreparedListener(COnAnnotationCreatePreparedListener listener) {
        annotationCreatePreparedListeners.add(listener);
    }

    public void release() {
        stopListenSelectAnnot();
        if (pdfView != null && viewModeChangedListener != null) {
            pdfView.removeOnPDFViewModeChangeListener(viewModeChangedListener);
        }
        viewModeChangedListener = null;
        if (undoRedoCoordinator != null) {
            undoRedoCoordinator.removeOnStateChangedListener(undoRedoStateListener);
            undoRedoCoordinator = null;
        }
        clearSelectedAnnotationStyleTarget();
        updateSettingButtonState();
    }


}
