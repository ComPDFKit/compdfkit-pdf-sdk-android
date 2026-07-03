/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;

/**
 * Manages screenshot/snippet mode: entering and exiting.
 *
 * Note: exitScreenShot and exitSnipMode had identical logic in the original
 * fragment, so they are merged into a single exitSnippetMode() here.
 */
public class CSnippetController {

    private final CPDFDocumentController ctx;

    public CSnippetController(CPDFDocumentController ctx) {
        this.ctx = ctx;
    }

    /**
     * Enter screenshot/snippet mode. The user selects a screenshot area in the reader view.
     */
    public void enterSnippetMode() {
        CPDFReaderView readerView = ctx.getPdfView().getCPdfReaderView();
        readerView.removeAllAnnotFocus();
        if (readerView.getContextMenuShowListener() != null) {
            readerView.getContextMenuShowListener().dismissContextMenu();
        }
        for (int i = 0; i < readerView.getChildCount(); i++) {
            CPDFPageView view = (CPDFPageView) readerView.getChildAt(i);
            view.clearScreenShotRect();
        }
        ctx.getFragment().onBackPressedCallback.setEnabled(true);
        ctx.getScreenManager().fillScreenChange();
        readerView.setTouchMode(CPDFReaderView.TouchMode.SCREENSHOT);
    }

    /**
     * Exit screenshot/snippet mode and restore the previous touch mode.
     * This is the shared implementation for both exitScreenShot() and exitSnipMode().
     */
    public void exitSnippetMode() {
        CPDFReaderView readerView = ctx.getPdfView().getCPdfReaderView();
        if (readerView.getContextMenuShowListener() != null) {
            readerView.getContextMenuShowListener().dismissContextMenu();
        }
        for (int i = 0; i < readerView.getChildCount(); i++) {
            CPDFPageView view = (CPDFPageView) readerView.getChildAt(i);
            view.clearScreenShotRect();
        }
        CPDFReaderView.ViewMode viewMode = readerView.getViewMode();
        if (viewMode == CPDFReaderView.ViewMode.PDFEDIT) {
            readerView.setTouchMode(CPDFReaderView.TouchMode.EDIT);
            CPDFEditManager editManager = readerView.getEditManager();
            if (editManager != null && !editManager.isEditMode()) {
                editManager.enable();
                editManager.beginEdit(CPDFEditPage.LoadTextImage | CPDFEditPage.LoadPath);
            }
        } else {
            readerView.setTouchMode(CPDFReaderView.TouchMode.BROWSE);
        }
        ctx.getScreenManager().fillScreenChange();
    }
}
