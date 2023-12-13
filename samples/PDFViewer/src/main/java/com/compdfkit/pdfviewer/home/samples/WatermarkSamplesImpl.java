/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.samples;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.watermark.CPDFWatermark;
import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.home.HomeFunBean;
import com.compdfkit.pdfviewer.home.SelectWatermarkFunFragment;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.security.watermark.CWatermarkEditDialog;

import java.io.File;


public class WatermarkSamplesImpl extends OpenPDFSamplesImpl {

    public WatermarkSamplesImpl(Fragment fragment, HomeFunBean.FunType funType) {
        super(fragment, funType);
    }

    @Override
    public void run() {
        watermark(filePath, uri);
    }

    private void verifyDocument(String filePath, Uri uri, CVerifyPasswordDialogFragment.CVerifyCompleteListener listener) {
        CPDFDocument document = new CPDFDocument(fragment.getContext());
        CPDFDocument.PDFDocumentError error;
        if (!TextUtils.isEmpty(filePath)) {
            error = document.open(filePath);
        } else {
            error = document.open(uri);
        }
        if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
            if (listener != null) {
                listener.complete(document);
            }
        } else if (error == CPDFDocument.PDFDocumentError.PDFDocumentErrorPassword) {
            showVerifyPasswordDialog(document, listener);
        }
    }

    /**
     * Demonstrate watermark function
     *
     * @param filePath
     * @param uri
     */
    private void watermark(String filePath, Uri uri) {
        SelectWatermarkFunFragment watermarkFunFragment = SelectWatermarkFunFragment.newInstance();
        watermarkFunFragment.setAddWatermarkClickListener(v -> {
            verifyDocument(filePath, uri, document -> {
                CWatermarkEditDialog watermarkEditDialog = CWatermarkEditDialog.newInstance();
                watermarkEditDialog.setDocument(document);
                watermarkEditDialog.setPageIndex(0);
                watermarkEditDialog.setCompleteListener(pdfFile -> {
                    watermarkEditDialog.dismiss();
                    if (TextUtils.isEmpty(pdfFile)){
                        CToastUtil.showLongToast(fragment.getContext(), R.string.tools_watermark_add_failed);
                    } else {
                        CToastUtil.showLongToast(fragment.getContext(), R.string.tools_watermark_add_success);
                        startPDFActivity(pdfFile, null, password);
                    }
                });
                watermarkEditDialog.show(fragment.getChildFragmentManager(), "watermarkEditDialog");
                watermarkFunFragment.dismiss();
            });

        });
        watermarkFunFragment.setRemoveWatermarkClickListener(v -> {
            watermarkFunFragment.dismiss();
            verifyDocument(filePath, uri, document -> {
                if (document != null) {
                    // Display the secondary confirmation pop-up window to remove the watermark
                    CAlertDialog alertDialog = CAlertDialog.newInstance(fragment.getString(R.string.tools_remove_watermark),
                            fragment.getString(R.string.tools_remove_watermark_info));
                    alertDialog.setCancelClickListener(v1 -> {
                        alertDialog.dismiss();
                    });
                    alertDialog.setConfirmClickListener(v1 -> {
                        alertDialog.dismiss();
                        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
                        CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(dir,
                                fragment.getString(R.string.tools_save_location), fragment.getString(R.string.tools_save_to_this_directory));
                        directoryDialog.setSelectFolderListener(dir1 -> {
                            try {
                                // remove all watermark
                                boolean result = true;
                                for (int i = 0; i < document.getWatermarkCount(); i++) {
                                    CPDFWatermark watermark = document.getWatermark(i);
                                    if (watermark != null){
                                        boolean success = watermark.clear();
                                        if (!success){
                                            result = false;
                                        }
                                    }else {
                                        result = false;
                                    }
                                }
                                if (result){
                                    String fileName = CFileUtils.getFileNameNoExtension(document.getFileName());
                                    File file = new File(dir1, fileName +fragment.getContext().getString(R.string.tools_remove_watermark_suffix));
                                    file = CFileUtils.renameNameSuffix(file);
                                    document.saveAs(file.getAbsolutePath(), false);
                                    document.close();
                                    CToastUtil.showLongToast(fragment.getContext(), R.string.tools_watermark_removed_success);
                                    startPDFActivity(file.getAbsolutePath(), null, password);
                                }else {
                                    CToastUtil.showLongToast(fragment.getContext(), R.string.tools_watermark_removed_failed);

                                }

                            } catch (CPDFDocumentException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        directoryDialog.show(fragment.getChildFragmentManager(), "dirDialog");
                    });
                    alertDialog.show(fragment.getChildFragmentManager(), "removeWatermarkAlertDialog");
                }
            });
        });
        watermarkFunFragment.show(fragment.getChildFragmentManager(), "selectFunDialog");
    }

    private void showVerifyPasswordDialog(CPDFDocument document, CVerifyPasswordDialogFragment.CVerifyCompleteListener documentListener) {
        CVerifyPasswordDialogFragment verifyPasswordDialogFragment;
        verifyPasswordDialogFragment = CVerifyPasswordDialogFragment.newInstance(document, filePath, uri);
        verifyPasswordDialogFragment.setVerifyCompleteListener(document1 -> {
            if (documentListener != null) {
                documentListener.complete(document1);
            }
        });
        verifyPasswordDialogFragment.show(fragment.getChildFragmentManager(), "verifyPasswordDialog");
    }

}
