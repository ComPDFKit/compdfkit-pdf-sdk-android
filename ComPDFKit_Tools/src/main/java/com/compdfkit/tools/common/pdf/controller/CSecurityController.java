/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import static com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType.ADD_WATERMARK_DIALOG_DISMISSED;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.pdf.CPDFDocumentFragment;
import com.compdfkit.tools.common.pdf.config.CPDFWatermarkConfig;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.storage.CPDFPublicFileSaver;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.security.encryption.CDocumentEncryptionDialog;
import com.compdfkit.tools.security.encryption.CInputOwnerPwdDialog;
import com.compdfkit.tools.security.watermark.CWatermarkEditDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages security-related dialogs: document encryption, watermark, and flatten.
 */
public class CSecurityController {

    private static final String TAG = "CSecurityController";

    private final CPDFDocumentController ctx;
    private final CDocumentIOController documentIOController;

    public CSecurityController(CPDFDocumentController ctx, CDocumentIOController documentIOController) {
        this.ctx = ctx;
        this.documentIOController = documentIOController;
    }

    /**
     * Show the security/encryption dialog. If the user only has user-level permissions,
     * prompt for the owner password first.
     */
    public void showSecurityDialog() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
        if (document == null) {
            return;
        }
        if (document.getPermissions() == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsUser) {
            CInputOwnerPwdDialog inputOwnerPwdDialog = CInputOwnerPwdDialog.newInstance();
            inputOwnerPwdDialog.setDocument(document);
            inputOwnerPwdDialog.setCancelClickListener(v2 -> inputOwnerPwdDialog.dismiss());
            inputOwnerPwdDialog.setConfirmClickListener(ownerPassword -> {
                document.reload(ownerPassword);
                showSettingEncryptionDialog();
                inputOwnerPwdDialog.dismiss();
            });
            inputOwnerPwdDialog.show(fragment.getChildFragmentManager(), "inputPasswordDialog");
            return;
        }
        showSettingEncryptionDialog();
    }

    private void showSettingEncryptionDialog() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        CDocumentEncryptionDialog documentEncryptionDialog = CDocumentEncryptionDialog.newInstance();
        documentEncryptionDialog.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
        documentEncryptionDialog.setSaveFileExtraFontSubset(pdfView.isSaveFileExtraFontSubset());
        documentEncryptionDialog.setEncryptionResultListener((isRemoveSecurity, result, filePath, uri, password) -> {
            pdfView.getCPdfReaderView().reloadPages();
            if (result) {
                if (uri != null) {
                    documentIOController.refreshSavedPdfUri(uri);
                    pdfView.openPDF(uri);
                } else {
                    documentIOController.refreshSavedPdf(filePath);
                    pdfView.openPDF(filePath);
                }
            }
            documentEncryptionDialog.dismiss();
            int msgResId;
            if (isRemoveSecurity) {
                msgResId = result ? R.string.tools_password_remove_success : R.string.tools_password_remove_fail;
            } else {
                msgResId = result ? R.string.tools_set_password_successfully : R.string.tools_set_password_failures;
            }
            CToastUtil.showLongToast(fragment.getContext(), msgResId);
        });
        documentEncryptionDialog.show(fragment.getChildFragmentManager(), "documentEncryption");
    }

    /**
     * Show the add watermark dialog using the default watermark config.
     */
    public void showAddWatermarkDialog() {
        showAddWatermarkDialog(ctx.getConfiguration().globalConfig.watermark);
    }

    /**
     * Show the add watermark dialog with a custom watermark config.
     */
    public void showAddWatermarkDialog(CPDFWatermarkConfig watermarkConfig) {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        CWatermarkEditDialog watermarkEditDialog = CWatermarkEditDialog.newInstance();
        watermarkEditDialog.setDocument(pdfView.getCPdfReaderView().getPDFDocument());
        watermarkEditDialog.setSaveFileExtraFontSubset(pdfView.isSaveFileExtraFontSubset());
        watermarkEditDialog.setPageIndex(pdfView.currentPageIndex);
        watermarkEditDialog.setWatermarkConfig(watermarkConfig);
        watermarkEditDialog.setCompleteListener((success, saveAsNewFile1, pdfFile, pdfUri) -> {
            watermarkEditDialog.dismiss();
            pdfView.getCPdfReaderView().reloadPages();
            if (!success) {
                CToastUtil.showLongToast(fragment.getContext(), R.string.tools_watermark_add_failed);
                return;
            }
            CToastUtil.showLongToast(fragment.getContext(), R.string.tools_watermark_add_success);
            if (saveAsNewFile1) {
                if (pdfUri != null) {
                    documentIOController.refreshSavedPdfUri(pdfUri);
                    pdfView.openPDF(pdfUri);
                } else {
                    documentIOController.refreshSavedPdf(pdfFile);
                    pdfView.openPDF(pdfFile);
                }
            }
        });
        watermarkEditDialog.setDismissListener(() -> {
            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, ADD_WATERMARK_DIALOG_DISMISSED);
            CPDFCustomEventCallbackHelper.getInstance().notifyClick("", extraMap);
        });
        watermarkEditDialog.show(fragment.getChildFragmentManager(), "watermarkEditDialog");
    }

    /**
     * Show the flatten dialog. Directory access is handled inside CFileDirectoryDialog.
     */
    public void showFlattenedDialog() {
        CPDFViewCtrl pdfView = ctx.getPdfView();
        pdfView.savePDF((filePath, pdfUri) -> flattenedPdf(), e -> flattenedPdf());
    }

    private void flattenedPdf() {
        CPDFDocumentFragment fragment = ctx.getFragment();
        CPDFViewCtrl pdfView = ctx.getPdfView();
        CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(
                CPDFStorageManager.getDefaultDirectoryDialogPath(),
                fragment.getString(R.string.tools_select_folder),
                fragment.getString(R.string.tools_save_to_this_directory)
        );
        directoryDialog.setSelectFolderListener(dir -> {
            CPDFDocument document = pdfView.getCPdfReaderView().getPDFDocument();
            if (document == null) {
                CToastUtil.showLongToast(fragment.getContext(), R.string.tools_save_failed);
                return;
            }
            ctx.getBasicFragment().showLoadingDialog();
            String fileName = CFileUtils.getFileNameNoExtension(document.getFileName()) + fragment.getString(R.string.tools_flattened_suffix);
            CThreadPoolUtils.getInstance().executeIO(() -> {
                boolean result = document.flattenAllPages(CPDFPage.PDFFlattenOption.FLAT_NORMALDISPLAY);
                if (result) {
                    try {
                       CPDFPublicFileSaver.SaveResult saveResult = CPDFPublicFileSaver.savePdfToSelectedDirectory(
                               fragment.getContext(),
                               dir,
                               fileName,
                                false,
                               tempPath -> document.saveAs(tempPath, false, ctx.getConfiguration().globalConfig.fileSaveExtraFontSubset));
                        CThreadPoolUtils.getInstance().executeMain(() -> {
                            ctx.getBasicFragment().dismissLoadingDialog();
                            if (document.shouleReloadDocument()) {
                                document.reload();
                            }
                            if (saveResult.isSuccess()) {
                                if (saveResult.getPublicUri() != null) {
                                    documentIOController.refreshSavedPdfUri(saveResult.getPublicUri());
                                    pdfView.openPDF(saveResult.getPublicUri());
                                } else {
                                    documentIOController.refreshSavedPdf(saveResult.getOpenPath());
                                    pdfView.openPDF(saveResult.getOpenPath());
                                }
                                CToastUtil.showLongToast(fragment.getContext(), R.string.tools_save_success);
                            } else {
                                CToastUtil.showLongToast(fragment.getContext(), R.string.tools_save_failed);
                            }
                        });
                    } catch (Exception e) {
                        CLog.e(TAG, "flattenedPdf save failed: " + e.getMessage());
                        CThreadPoolUtils.getInstance().executeMain(() -> {
                            ctx.getBasicFragment().dismissLoadingDialog();
                            CToastUtil.showLongToast(fragment.getContext(), R.string.tools_save_failed);
                        });
                    }
                } else {
                    ctx.getBasicFragment().dismissLoadingDialog();
                    CToastUtil.showLongToast(fragment.getContext(), R.string.tools_save_failed);
                }
            });
        });
        directoryDialog.show(fragment.getChildFragmentManager(), "dirDialog");
    }
}
