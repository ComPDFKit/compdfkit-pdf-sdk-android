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
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.pdfviewer.R;
import com.compdfkit.pdfviewer.home.HomeFunBean;
import com.compdfkit.pdfviewer.home.datas.FunDatas;
import com.compdfkit.tools.common.pdf.CPDFDocumentActivity;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.compress.CPDFCompressDialog;


public class CompressSamplesImpl extends OpenPDFSamplesImpl {

    public CompressSamplesImpl(Fragment fragment, HomeFunBean.FunType funType) {
        super(fragment, funType);
    }

    @Override
    public void run() {
        verifyDocument(filePath, uri, document -> {
            CPDFCompressDialog compressDialog = new CPDFCompressDialog();
            compressDialog.setDocument(document);
            compressDialog.setCompressDocumentListener((result, path) -> {
                compressDialog.dismiss();
                if (result && !TextUtils.isEmpty(path)){
                    CPDFDocumentActivity.startActivity(fragment.getContext(), path, "",
                            FunDatas.getConfiguration(fragment.getContext(), CPreviewMode.Viewer));
                    CToastUtil.showLongToast(fragment.getContext(), R.string.tools_compressed_successfully);
                }
            });
            compressDialog.show(fragment.getChildFragmentManager(), "compressedDialog");
        });
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
