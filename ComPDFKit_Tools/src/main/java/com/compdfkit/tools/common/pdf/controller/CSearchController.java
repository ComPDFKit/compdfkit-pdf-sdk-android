/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;

import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.viewer.pdfsearch.CSearchReplaceToolbar;
import com.compdfkit.ui.reader.CPDFReaderView;

/**
 * Manages the text search toolbar: initialization, showing, and hiding.
 */
public class CSearchController {

    private final CPDFDocumentController ctx;
    private final CPreviewModeController previewModeController;

    public CSearchController(CPDFDocumentController ctx, CPreviewModeController previewModeController) {
        this.ctx = ctx;
        this.previewModeController = previewModeController;
    }

    /**
     * Initialize the search toolbar and wire up the exit-search callback.
     */
    public void init() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        fragment.pdfSearchToolBarView.initWithPDFView(ctx.getPdfView());
        fragment.pdfSearchToolBarView.setExitSearchListener(this::hideTextSearchView);
    }

    /**
     * Show the text search view, saving the current edit mode for later restoration.
     */
    public void showTextSearchView() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (pdfView.getCPdfReaderView().getEditManager().isEditMode()) {
            previewModeController.saveCurrentEditModeFromLoadType();
        } else {
            previewModeController.setCurEditMode(CPDFEditPage.LoadNone);
        }
        pdfView.exitEditMode();
        if (fragment.flTool.getVisibility() == GONE && !ctx.getConfiguration().toolbarConfig.mainToolbarVisible) {
            ctx.getScreenManager().fillScreenManager.showFromTop(fragment.flTool, 200);
        }
        fragment.pdfToolBar.setVisibility(View.GONE);
        CSearchReplaceToolbar.ViewType viewType = pdfView.getCPdfReaderView().getViewMode() == CPDFReaderView.ViewMode.PDFEDIT
                ? CSearchReplaceToolbar.ViewType.SearchReplace : CSearchReplaceToolbar.ViewType.Search;
        fragment.onBackPressedCallback.setEnabled(true);
        fragment.pdfSearchToolBarView.setViewType(viewType);
        fragment.pdfSearchToolBarView.setVisibility(VISIBLE);
        fragment.pdfSearchToolBarView.showKeyboard();
        if (viewType == CSearchReplaceToolbar.ViewType.SearchReplace) {
            ctx.getScreenManager().fillScreenManager.removeToolView(fragment.flBottomToolBar);
            ctx.getScreenManager().fillScreenManager.hideFromBottom(fragment.flBottomToolBar, 200);
        }
    }

    /**
     * Hide the text search view and restore the previous edit mode if any.
     */
    public void hideTextSearchView() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (previewModeController.getCurEditMode() > CPDFEditPage.LoadNone) {
            previewModeController.restoreEdit();
            fragment.editToolBar.updateUndoRedo();
        }
        if (!ctx.getConfiguration().toolbarConfig.mainToolbarVisible) {
            fragment.flTool.setVisibility(GONE);
        }
        fragment.pdfToolBar.setVisibility(VISIBLE);
        fragment.pdfSearchToolBarView.hideKeyboard();
        fragment.pdfSearchToolBarView.setVisibility(GONE);
        if (pdfView.getCPdfReaderView().getViewMode() == CPDFReaderView.ViewMode.PDFEDIT) {
            ctx.getScreenManager().fillScreenManager.bindBottomToolViewList(fragment.flBottomToolBar);
            ctx.getScreenManager().constraintShow(fragment.flBottomToolBar);
        }
        fragment.blockView.setVisibility(GONE);
    }
}
