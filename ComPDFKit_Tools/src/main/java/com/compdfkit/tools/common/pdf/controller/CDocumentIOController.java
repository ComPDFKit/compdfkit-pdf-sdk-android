/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import android.net.Uri;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultLauncher;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CPDFMediaScannerUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.docseditor.pdfpageedit.CPDFPageEditDialogFragment;

import java.io.File;

/**
 * Manages document I/O: opening, saving, sharing, and selecting a new document.
 *
 * The sharePDF logic previously duplicated between success and error callbacks
 * in CBasicPDFFragment is deduplicated here via sharePdfUriOrFile().
 */
public class CDocumentIOController {

    private static final String TAG = "CDocumentIOController";

    private final CPDFDocumentController ctx;
    private final CPreviewModeController previewModeController;
    private final ActivityResultLauncher<Void> selectDocumentLauncher;

    public CDocumentIOController(CPDFDocumentController ctx,
                                 CPreviewModeController previewModeController,
                                 ActivityResultLauncher<Void> selectDocumentLauncher) {
        this.ctx = ctx;
        this.previewModeController = previewModeController;
        this.selectDocumentLauncher = selectDocumentLauncher;
    }

    /**
     * Open the PDF document from the fragment's arguments.
     */
    public void initDocument(CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (fragment.getArguments() != null) {
            int pageIndex = fragment.getArguments().getInt(CPDFDocumentFragment.EXTRA_PAGE_INDEX, 0);
            String password = fragment.getArguments().getString(CPDFDocumentFragment.EXTRA_FILE_PASSWORD);
            if (!TextUtils.isEmpty(fragment.getArguments().getString(CPDFDocumentFragment.EXTRA_FILE_PATH))) {
                String path = fragment.getArguments().getString(CPDFDocumentFragment.EXTRA_FILE_PATH);
                pdfView.openPDF(path, password, pageIndex, callback);
            } else if (fragment.getArguments().getParcelable(CPDFDocumentFragment.EXTRA_FILE_URI) != null) {
                Uri uri = fragment.getArguments().getParcelable(CPDFDocumentFragment.EXTRA_FILE_URI);
                CFileUtils.takeUriPermission(fragment.getContext(), uri);
                pdfView.openPDF(uri, password, pageIndex, callback);
            }
        }
    }

    /**
     * Prompt the user to select a new PDF document, saving the current one if it has changes.
     */
    public void selectDocument() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (fragment.pdfToolBar.getMode() == CPreviewMode.Edit) {
            pdfView.exitEditMode();
        }
        if (pdfView.getCPdfReaderView().getPDFDocument() == null) {
            selectDocumentLauncher.launch(null);
            return;
        }
        if (!pdfView.getCPdfReaderView().getPDFDocument().hasChanges()) {
            selectDocumentLauncher.launch(null);
            return;
        }
        CAlertDialog alertDialog = CAlertDialog.newInstance(
                fragment.getString(R.string.tools_save_title),
                fragment.getString(R.string.tools_save_message));
        alertDialog.setConfirmClickListener(v -> {
            alertDialog.dismiss();
            if (ctx.getConfiguration() != null && ctx.getConfiguration().globalConfig.fileSaveExtraFontSubset) {
                ctx.getBasicFragment().showLoadingDialog(fragment.getString(R.string.tools_saveing));
            }
            pdfView.savePDF((filePath, pdfUri) -> {
                ctx.getBasicFragment().dismissLoadingDialog();
                selectDocumentLauncher.launch(null);
            }, e -> {
                ctx.getBasicFragment().dismissLoadingDialog();
                selectDocumentLauncher.launch(null);
            });
        });
        alertDialog.setCancelClickListener(v -> {
            alertDialog.dismiss();
            selectDocumentLauncher.launch(null);
        });
        alertDialog.show(fragment.getChildFragmentManager(), "alertDialog");
    }

    /**
     * Save the current PDF and exit the activity.
     */
    public void saveAndExit() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (pdfView != null) {
            ctx.getBasicFragment().showLoadingDialog(fragment.getString(R.string.tools_saveing));
            pdfView.savePDF((filePath, pdfUri) -> {
                try {
                   ctx.getBasicFragment().dismissLoadingDialog();
                   fragment.onBackPressedCallback.setEnabled(false);
                    fragment.requireActivity().getOnBackPressedDispatcher().onBackPressed();
               } catch (Exception e) {
                   CLog.e(TAG, "saveAndExit success callback failed: " + e.getMessage());
               }
           }, e -> {
               try {
                   ctx.getBasicFragment().dismissLoadingDialog();
                   fragment.onBackPressedCallback.setEnabled(false);
                    fragment.requireActivity().getOnBackPressedDispatcher().onBackPressed();
               } catch (Exception ex) {
                    CLog.e(TAG, "saveAndExit error callback failed: " + ex.getMessage());
                }
            });
        }
    }

    /**
     * Refresh the media scanner so the saved PDF appears in file managers.
     */
    public void refreshSavedPdf(String filePath) {
        CPDFMediaScannerUtil.scanPdfFile(ctx.getFragment().getContext(), filePath);
    }

    /**
     * MediaStore-published files are already indexed; this is a no-op kept for API symmetry.
     */
    public void refreshSavedPdfUri(Uri uri) {
        // MediaStore files are automatically visible to the system media scanner.
    }

    /**
     * Share the current PDF. Saves first, then shares the resulting file/URI.
     *
     * The original implementation in CBasicPDFFragment duplicated the share
     * path-resolution logic between the success and error callbacks.
     * Here it is extracted into sharePdfUriOrFile() for reuse.
     */
    public void sharePDF(CPDFViewCtrl pdfView) {
        previewModeController.saveCurrentEditModeFromLoadType();
        pdfView.savePDF((filePath, pdfUri) -> {
            previewModeController.restoreEdit(pdfView, true);
            sharePdfUriOrFile(filePath, pdfUri);
        }, e -> {
            previewModeController.restoreEdit(pdfView, true);
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (document == null) {
                return;
            }
            Uri pdfUri = document.getUri();
            String filePath = document.getAbsolutePath();
            sharePdfUriOrFile(filePath, pdfUri);
        });
    }

    /**
     * Shared helper: resolve the file/URI to a shareable form and invoke the system share sheet.
     * Handles content:// URIs, cache/files dir paths, and external storage paths.
     */
    private void sharePdfUriOrFile(String filePath, Uri pdfUri) {
        CPDFDocumentFragment fragment = ctx.getFragment();
        if (pdfUri != null && pdfUri.toString().startsWith("content://")) {
            CFileUtils.shareFile(fragment.getContext(), fragment.getString(R.string.tools_share_to), "application/pdf", pdfUri);
            return;
        }
        if (!TextUtils.isEmpty(filePath)) {
            if (filePath.startsWith(fragment.getContext().getCacheDir().getAbsolutePath()) ||
                    filePath.startsWith(fragment.getContext().getFilesDir().getAbsolutePath())) {
                CFileUtils.shareFile(fragment.getContext(), fragment.getString(R.string.tools_share_to), "application/pdf", new File(filePath));
            } else if (new File(filePath).exists()) {
                Uri uri = CFileUtils.getUriBySystem(fragment.getContext(), new File(filePath));
                CFileUtils.shareFile(fragment.getContext(), fragment.getString(R.string.tools_share_to), "application/pdf", uri);
            }
        }
    }

    /**
     * Callback invoked when a new document is selected via the launcher.
     * Resets all toolbar states and opens the new document.
     */
    public void onDocumentSelected(Uri uri) {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        if (uri == null) {
            return;
        }
        com.compdfkit.ui.reader.CPDFReaderView readerView = pdfView.getCPdfReaderView();
        if (readerView != null && readerView.getEditManager() != null) {
            readerView.getEditManager().endEdit();
        }
        if (readerView != null && readerView.getContextMenuShowListener() != null) {
            readerView.getContextMenuShowListener().dismissContextMenu();
        }
        CFileUtils.takeUriPermission(fragment.getContext(), uri);
        pdfView.getCPdfReaderView().getUndoManager().clearHistory();
        fragment.setPreviewMode(ctx.getConfiguration().modeConfig.initialViewMode);
        pdfView.resetAnnotationType();
        fragment.formToolBar.reset();
        fragment.editToolBar.resetStatus();
        fragment.signatureToolBar.reset();
        fragment.annotationToolbar.reset();
        ctx.getScreenManager().changeWindowStatus(ctx.getConfiguration().modeConfig.initialViewMode);
        ctx.getScreenManager().constraintHide(fragment.signStatusView);

        int pageIndex = 0;
        if (fragment.getArguments() != null) {
            pageIndex = fragment.getArguments().getInt(CPDFDocumentFragment.EXTRA_PAGE_INDEX, 0);
        }
        pdfView.openPDF(uri, null, pageIndex, () -> {
            fragment.editToolBar.setEditMode(false);
            boolean enableSliderBar = pdfView.isEnableSliderBar();
            if (enableSliderBar && !ctx.getScreenManager().isFillScreen) {
                ctx.getScreenManager().fillScreenManager.showFromRight(pdfView.getSlideBarView(),
                        com.compdfkit.tools.common.utils.animation.CFillScreenManager.CONFIG_SHORT_ANIM_TIME);
            }
        });
    }
}
