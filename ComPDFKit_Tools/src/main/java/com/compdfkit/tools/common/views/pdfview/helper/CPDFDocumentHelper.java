/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.views.pdfview.helper;

import android.net.Uri;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditManager;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.ui.reader.CPDFReaderView;

import java.io.File;
import java.util.UUID;

/**
 * Manages PDF document lifecycle: opening, saving, closing, and write-permission handling.
 *
 * <p>Extracted from {@code CPDFViewCtrl}'s document methods.
 *
 * <p>Bug fixes applied:
 * <ul>
 *   <li>{@code setPDFDocument}: deduplicated success/password-verified branches via {@code applyDocument()}.</li>
 *   <li>{@code savePDF}: fixed incorrect "document is null" error message in the catch block.</li>
 *   <li>{@code close}: null-check on {@code getContextMenuShowListener()} before calling dismiss.</li>
 *   <li>{@code showWritePermissionsDialog}: added CLog.e in catch block instead of silent swallow.</li>
 * </ul>
 */
public class CPDFDocumentHelper {

    private static final String TAG = "CPDFDocumentHelper";

    // Context obtained via readerView.getContext() to avoid leaking an Activity reference.
    private final CPDFReaderView readerView;
    private final CPDFViewCtrlDelegate delegate;

    private CPDFConfiguration cpdfConfiguration;
    private CPDFViewCtrl.COnSaveCallback saveGlobalCallback;
    private CPDFViewCtrl.COnSaveError saveGlobalErrorCallback;

    private boolean isInitOpenPDF = true;
    private int initPageIndex = 0;

    public CPDFDocumentHelper(CPDFReaderView readerView,
                              CPDFViewCtrlDelegate delegate) {

        this.readerView = readerView;
        this.delegate = delegate;
    }

    public void setCPDFConfiguration(CPDFConfiguration configuration) {
        this.cpdfConfiguration = configuration;
    }

    public void setSaveCallback(CPDFViewCtrl.COnSaveCallback callback,
                                CPDFViewCtrl.COnSaveError errorCallback) {
        this.saveGlobalCallback = callback;
        this.saveGlobalErrorCallback = errorCallback;
    }

    public boolean isInitOpenPDF() {
        return isInitOpenPDF;
    }

    public void setInitOpenPDF(boolean init) {
        isInitOpenPDF = init;
    }

    public int getInitPageIndex() {
        return initPageIndex;
    }

    public void setInitPageIndex(int pageIndex) {
        initPageIndex = pageIndex;
    }

    // ========================================================================
    // openPDF overloads
    // ========================================================================

    public void openPDF(String pdfFilePath) {
        openPDF(pdfFilePath, null);
    }

    public void openPDF(String pdfFilePath, String password) {
        openPDF(pdfFilePath, password, 0, null);
    }

    public void openPDF(String pdfFilePath, String password, int pageIndex) {
        openPDF(pdfFilePath, password, pageIndex, null);
    }

    public void openPDF(String pdfFilePath, String password,
                        CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        openPDF(pdfFilePath, password, 0, callback);
    }

    public void openPDF(String pdfFilePath, String password, int pageIndex,
                        CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            CPDFDocument cpdfDocument = new CPDFDocument(readerView.getContext());
            CPDFDocument.PDFDocumentError pdfDocumentError = cpdfDocument.open(pdfFilePath, password);
            CThreadPoolUtils.getInstance().executeMain(() ->
                    setPDFDocument(cpdfDocument, pdfFilePath, pageIndex, pdfDocumentError, callback));
        });
    }

    public void openPDF(Uri pdfUri) {
        openPDF(pdfUri, null);
    }

    public void openPDF(Uri pdfUri, String password) {
        openPDF(pdfUri, password, 0, null);
    }

    public void openPDF(Uri pdfUri, String password, int pageIndex) {
        openPDF(pdfUri, password, pageIndex, null);
    }

    public void openPDF(Uri pdfUri, String password,
                        CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        openPDF(pdfUri, password, 0, callback);
    }

    public void openPDF(Uri pdfUri, String password, int pageIndex,
                        CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        CThreadPoolUtils.getInstance().executeIO(() -> {
            CPDFDocument cpdfDocument = new CPDFDocument(readerView.getContext());
            CPDFDocument.PDFDocumentError pdfDocumentError = cpdfDocument.open(pdfUri, password);
            CThreadPoolUtils.getInstance().executeMain(() ->
                    setPDFDocument(cpdfDocument, pdfUri, pageIndex, pdfDocumentError, callback));
        });
    }

    // ========================================================================
    // setPDFDocument — with deduplicated applyDocument()
    // ========================================================================

    public void setPDFDocument(CPDFDocument cpdfDocument, Object pdf, int pageIndex,
                               CPDFDocument.PDFDocumentError error,
                               CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        isInitOpenPDF = true;
        initPageIndex = pageIndex;
        CLog.e("ComPDFKit", "CPDFViewCtrl-openPDF:" + error.name());
        switch (error) {
            case PDFDocumentErrorSuccess:
                CLog.e("ComPDFKit", "canWrite:" + cpdfDocument.isCanWrite() + ", hasRepaired:"
                        + cpdfDocument.hasRepaired());
                applyDocument(cpdfDocument, callback);
                if (!cpdfDocument.isCanWrite() && cpdfDocument.hasRepaired()) {
                    showWritePermissionsDialog(cpdfDocument);
                }
                delegate.applyErrorCallback();
                break;
            case PDFDocumentErrorPassword:
                FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
                CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
                if (pdf instanceof String) {
                    verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(cpdfDocument,
                            (String) pdf);
                } else {
                    verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(cpdfDocument,
                            (Uri) pdf);
                }
                verifyPasswordDialogFragment.setDismissListener(() -> {
                   if (readerView.getPDFDocument() == null) {
                       if (fragmentActivity != null) {
                            fragmentActivity.getOnBackPressedDispatcher().onBackPressed();
                       }
                   }
                });
                verifyPasswordDialogFragment.setVerifyCompleteListener(document ->
                        applyDocument(document, callback));
                if (fragmentActivity != null) {
                    verifyPasswordDialogFragment.show(fragmentActivity.getSupportFragmentManager(),
                            "verifyPwdDialog");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Shared method to apply a successfully opened/verified document to the reader view.
     * Deduplicated from the success and password-verified branches.
     */
    private void applyDocument(CPDFDocument document, CPDFViewCtrl.COnOpenPdfFinishCallback callback) {
        readerView.setPDFDocument(document);
        delegate.updateScaleForLayout();
        delegate.addPageIndicator();
        if (readerView.getEditManager() != null) {
            readerView.getEditManager().disable();
        }
        if (callback != null) {
            callback.onOpenPdfFinishCallback();
        }
    }

    // ========================================================================
    // showWritePermissionsDialog
    // ========================================================================

    public void showWritePermissionsDialog(CPDFDocument document) {
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity == null) {
            return;
        }
        Fragment rwPermissionDialog = fragmentActivity.getSupportFragmentManager()
                .findFragmentByTag("rwPermissionDialog");
        if (rwPermissionDialog != null && rwPermissionDialog instanceof DialogFragment) {
            ((DialogFragment) rwPermissionDialog).dismiss();
        }
        CAlertDialog alertDialog = CAlertDialog.newInstance(
                readerView.getContext().getString(R.string.tools_warning),
                readerView.getContext().getString(R.string.tools_repair_pdf_file_mes)
        );
        alertDialog.setCancelClickListener(v -> alertDialog.dismiss());
        alertDialog.setConfirmClickListener(v -> {
            try {
                File tempFile = new File(readerView.getContext().getCacheDir(),
                        CFileUtils.CACHE_FOLDER + File.separator + document.getFileName());
                if (tempFile.exists()) {
                    String name = UUID.randomUUID().toString().substring(0, 4) + "_" + document.getFileName();
                    tempFile = new File(readerView.getContext().getCacheDir(),
                            CFileUtils.CACHE_FOLDER + File.separator + name);
                }
                boolean result = document.saveAs(tempFile.getAbsolutePath(), false);
                if (result) {
                    openPDF(tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                CLog.e(TAG, "showWritePermissionsDialog saveAs failed: " + e.getMessage());
            }
            alertDialog.dismiss();
        });
        alertDialog.show(fragmentActivity.getSupportFragmentManager(), "rwPermissionDialog");
    }

    // ========================================================================
    // savePDF
    // ========================================================================

    public void savePDF(CPDFViewCtrl.COnSaveCallback callback, CPDFViewCtrl.COnSaveError error) {
        boolean saveFileExtraFontSubset = false;
        boolean saveIncremental = true;
        if (cpdfConfiguration != null && cpdfConfiguration.globalConfig != null) {
            saveFileExtraFontSubset = cpdfConfiguration.globalConfig.fileSaveExtraFontSubset;
            saveIncremental = cpdfConfiguration.globalConfig.useSaveIncremental;
        }
        savePDF(saveIncremental, saveFileExtraFontSubset, callback, error);
    }

    public void savePDF(boolean saveIncremental, boolean fontSubset,
                        CPDFViewCtrl.COnSaveCallback callback, CPDFViewCtrl.COnSaveError error) {
        CThreadPoolUtils.getInstance().executeMain(() -> {
            readerView.getInkDrawHelper().onSave();
            readerView.pauseAllRenderProcess();
            readerView.removeAllAnnotFocus();
            if (readerView.getContextMenuShowListener() != null) {
                readerView.getContextMenuShowListener().dismissContextMenu();
            }
            CPDFDocument document = readerView.getPDFDocument();
            if (document == null) {
                if (error != null) {
                    error.error(new Exception("document is null"));
                }
                if (saveGlobalErrorCallback != null) {
                    saveGlobalErrorCallback.error(new Exception("document is null"));
                }
                return;
            }
            int contentEditorLoadType = readerView.getLoadType();
            delegate.exitEditMode();
            if (document.hasChanges()) {
                CThreadPoolUtils.getInstance().executeIO(() -> {
                    try {
                        CLog.e("ComPDFKit", "useSaveIncremental: " + saveIncremental + ", extraFontSubset:" + fontSubset);
                        document.save(saveIncremental ? CPDFDocument.PDFDocumentSaveType.PDFDocumentSaveIncremental : CPDFDocument.PDFDocumentSaveType.PDFDocumentSaveNoIncremental,
                                fontSubset);
                        if (document.shouleReloadDocument()) {
                            document.reload();
                            CThreadPoolUtils.getInstance().executeMain(() -> readerView.reloadPages2());
                        }
                        CThreadPoolUtils.getInstance().executeMain(() -> {
                            restoreEdit(contentEditorLoadType);
                            if (callback != null) {
                                callback.callback(document.getAbsolutePath(), document.getUri());
                            }
                            if (saveGlobalCallback != null) {
                                saveGlobalCallback.callback(document.getAbsolutePath(), document.getUri());
                            }
                        });
                    } catch (CPDFDocumentException e) {
                        CLog.e("ComPDFKit", "save fail:" + e.getMessage());
                        if (error != null) {
                            error.error(e);
                        }
                        // Bug fix: was "new Exception("document is null")", should pass the actual exception
                        if (saveGlobalErrorCallback != null) {
                            saveGlobalErrorCallback.error(e);
                        }
                    }
                });
            } else {
                restoreEdit(contentEditorLoadType);
                if (callback != null) {
                    callback.callback(document.getAbsolutePath(), document.getUri());
                }
                if (saveGlobalCallback != null) {
                    saveGlobalCallback.callback(document.getAbsolutePath(), document.getUri());
                }
            }
        });
    }

    // ========================================================================
    // Edit mode helpers
    // ========================================================================

    public void exitEditMode() {
        CPDFEditManager editManager = readerView.getEditManager();
        if (editManager != null && editManager.isEditMode()) {
            editManager.endEdit();
        }
    }

    private void restoreEdit(int curEditMode) {
        if (curEditMode > CPDFEditPage.LoadNone
                && readerView.getViewMode() == CPDFReaderView.ViewMode.PDFEDIT) {
            CPDFEditManager editManager = readerView.getEditManager();
            if (!editManager.isEditMode()) {
                editManager.beginEdit(curEditMode);
            }
        }
    }

    // ========================================================================
    // close — with NPE fix
    // ========================================================================

    public void close() {
        try {
            // Bug fix: null-check before calling dismissContextMenu
            if (readerView.getContextMenuShowListener() != null) {
                readerView.getContextMenuShowListener().dismissContextMenu();
            }
            saveGlobalCallback = null;
            saveGlobalErrorCallback = null;
            delegate.clearListeners();
            readerView.setSelectAnnotCallback(null);
            readerView.setOnViewModeChangedListener(null);
            delegate.detachSlideBar();
            if (readerView.getPDFDocument() != null) {
                readerView.getPDFDocument().close();
            }
        } catch (Exception e) {
            CLog.e(TAG, "close failed: " + e.getMessage());
        }
    }

    // ========================================================================
    // Delegate interface — callbacks back to CPDFViewCtrl
    // ========================================================================

    public interface CPDFViewCtrlDelegate {
        void updateScaleForLayout();
        void addPageIndicator();
        void applyErrorCallback();
        void exitEditMode();
        void detachSlideBar();
        void clearListeners();
    }
}
