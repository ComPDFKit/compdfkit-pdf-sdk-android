/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import static com.compdfkit.tools.common.views.pdfview.CPreviewMode.Annotation;
import static com.compdfkit.tools.common.views.pdfview.CPreviewMode.Edit;
import static com.compdfkit.tools.common.views.pdfview.CPreviewMode.Form;
import static com.compdfkit.tools.common.views.pdfview.CPreviewMode.Signature;
import static com.compdfkit.tools.common.views.pdfview.CPreviewMode.Viewer;

import android.view.View;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.impl.CEditImageContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CEditPathContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CEditTextContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CScreenShotContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSearchReplaceContextMenuView;
import com.compdfkit.tools.common.contextmenu.impl.CSignatureContextMenuView;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.viewer.contextmenu.CopyContextMenuView;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;
import com.compdfkit.ui.reader.CPDFReaderView;

/**
 * Manages preview mode switching (Viewer/Annotation/Edit/Form/Signature),
 * context menu reset, and the saved edit-mode state used to restore editing
 * after temporary exits (e.g. dialogs, search, BOTA).
 */
public class CPreviewModeController {

    private final CPDFDocumentController ctx;

    private int curEditMode = CPDFEditPage.LoadNone;

    public CPreviewModeController(CPDFDocumentController ctx) {
        this.ctx = ctx;
    }

    public void setPreviewMode(CPreviewMode mode) {
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (pdfView.getCPdfReaderView() == null) {
            return;
        }
        CPDFReaderView readerView = pdfView.getCPdfReaderView();
        readerView.getInkDrawHelper().onSave();
        readerView.pauseAllRenderProcess();
        readerView.removeAllAnnotFocus();
        IContextMenuShowListener contextMenuShowListener = readerView.getContextMenuShowListener();
        if (contextMenuShowListener != null) {
            contextMenuShowListener.dismissContextMenu();
        }
        ctx.getScreenManager().changeWindowStatus(mode);
        ctx.getFragment().pdfToolBar.selectMode(mode);
        ctx.getFragment().formToolBar.reset();
        ctx.getFragment().signatureToolBar.reset();
        resetContextMenu(pdfView, mode);
        CPDFEditManager editManager = readerView.getEditManager();
        if (mode == Edit) {
            readerView.setViewMode(CPDFReaderView.ViewMode.PDFEDIT);
            ctx.getFragment().editToolBar.updateUndoRedo();
            if (editManager != null && !editManager.isEditMode()) {
                editManager.enable();
                editManager.beginEdit(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
            }
        } else {
            if (readerView.getTouchMode() == CPDFReaderView.TouchMode.SCREENSHOT
                    && readerView.getViewMode() == CPDFReaderView.ViewMode.PDFEDIT) {
                readerView.setTouchMode(CPDFReaderView.TouchMode.EDIT);
            }
            pdfView.exitEditMode();
            switch (mode) {
                case Viewer:
                case Signature:
                    pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.VIEW);
                    break;
                case Annotation:
                    pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.ANNOT);
                    break;
                case Form:
                    pdfView.getCPdfReaderView().setViewMode(CPDFReaderView.ViewMode.FORM);
                    break;
                default:
                    break;
            }
        }
    }

    public void resetContextMenu(CPDFViewCtrl pdfView, CPreviewMode mode) {
        switch (mode) {
            case Viewer:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .setSelectContentMenu(new CopyContextMenuView())
                                .setScreenShotContextMenu(new CScreenShotContextMenuView())
                                .create(pdfView));
                break;
            case Annotation:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder().defaultHelper()
                                .create(pdfView));
                break;
            case Edit:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .setEditTextContentMenu(new CEditTextContextMenuView())
                                .setEditImageContentMenu(new CEditImageContextMenuView())
                                .setSearchReplaceContextMenu(new CSearchReplaceContextMenuView())
                                .setScreenShotContextMenu(new CScreenShotContextMenuView())
                                .setEditPathContentMenu(new CEditPathContextMenuView())
                                .create(pdfView));
                break;
            case Form:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .defaultFormHelper()
                                .create(pdfView));
                break;
            case Signature:
                pdfView.getCPdfReaderView().setContextMenuShowListener(
                        new CPDFContextMenuHelper.Builder()
                                .setSignatureContextMenu(new CSignatureContextMenuView())
                                .setScreenShotContextMenu(new CScreenShotContextMenuView())
                                .create(pdfView));
                break;
            default:
                break;
        }
    }

    /**
     * Save the current edit load type so it can be restored later.
     */
    public void saveCurrentEditMode() {
        CPDFReaderView readerView = ctx.getPdfView().getCPdfReaderView();
        if (readerView != null && readerView.getEditManager() != null && readerView.getEditManager().isEditMode()) {
            curEditMode = readerView.getLoadType();
        } else {
            curEditMode = CPDFEditPage.LoadNone;
        }
    }

    /**
     * Save the current edit load type from the reader view's load type directly.
     */
    public void saveCurrentEditModeFromLoadType() {
        curEditMode = ctx.getPdfView().getCPdfReaderView().getLoadType();
    }

    public int getCurEditMode() {
        return curEditMode;
    }

    public void setCurEditMode(int mode) {
        curEditMode = mode;
    }

    /**
     * Restore edit mode using the current pdfToolBar mode to determine isEditMode.
     */
    public void restoreEdit() {
        restoreEdit(ctx.getPdfView(),
                ctx.getFragment().pdfToolBar.getMode() == Edit);
    }

    /**
     * Restore edit mode if one was previously active.
     */
    public void restoreEdit(CPDFViewCtrl pdfView, boolean isEditMode) {
        if (curEditMode > CPDFEditPage.LoadNone && isEditMode) {
            CPDFEditManager editManager = pdfView.getCPdfReaderView().getEditManager();
            if (!editManager.isEditMode()) {
                editManager.beginEdit(curEditMode);
            }
        }
    }
}
