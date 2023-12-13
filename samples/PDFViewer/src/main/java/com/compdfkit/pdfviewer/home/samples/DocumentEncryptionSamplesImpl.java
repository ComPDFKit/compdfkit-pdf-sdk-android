/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
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
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.dialog.CAlertDialog;
import com.compdfkit.tools.common.views.CVerifyPasswordDialogFragment;
import com.compdfkit.tools.security.encryption.CDocumentEncryptionDialog;
import com.compdfkit.tools.security.encryption.CInputOwnerPwdDialog;


public class DocumentEncryptionSamplesImpl extends OpenPDFSamplesImpl {

    public DocumentEncryptionSamplesImpl(Fragment fragment, HomeFunBean.FunType funType) {
        super(fragment, funType);
    }

    @Override
    public void run() {
        security(filePath, uri);
    }

    /**
     * Examples of PDF security functions, which can set passwords and remove passwords
     * @param filePath
     * @param uri
     */
    private void security(String filePath, Uri uri){
        verifyDocument(filePath, uri, document -> {
            if (document.getPermissions() == CPDFDocument.PDFDocumentPermissions.PDFDocumentPermissionsUser) {
                showInputOwnerPasswordDialog(document);
                return;
            }
            // Display the dialog for setting the document encryption function
            showDocumentEncryptionDialog(document);
        });
    }

    /**
     * Display the dialog for setting the document encryption function
     *
     * @param document
     */
    private void showDocumentEncryptionDialog(CPDFDocument document){
        CDocumentEncryptionDialog encryptionDialog = CDocumentEncryptionDialog.newInstance();
        encryptionDialog.setDocument(document);
        encryptionDialog.setEncryptionResultListener((isRemoveSecurity, result, file, password) -> {
            encryptionDialog.dismiss();
            int tipsResId;
            if (isRemoveSecurity){
                tipsResId = result ? R.string.tools_password_remove_success : R.string.tools_password_remove_fail;
            }else {
                tipsResId = result ? R.string.tools_set_password_successfully : R.string.tools_set_password_failures;
            }
            if (!result){
                showResultDialog(fragment.getActivity().getString(tipsResId));
            }else {
                startPDFActivity(file, null, null);
                CToastUtil.showLongToast(fragment.getContext(), tipsResId);
            }
        });
        encryptionDialog.show(fragment.getChildFragmentManager(), "documentEncryptionDialog");
    }

    private void showInputOwnerPasswordDialog(CPDFDocument document){
        CInputOwnerPwdDialog ownerPwdDialog = CInputOwnerPwdDialog.newInstance();
        ownerPwdDialog.setDocument(document);
        ownerPwdDialog.setConfirmClickListener(ownerPassword -> {
            ownerPwdDialog.dismiss();
            document.reload(ownerPassword);
            showDocumentEncryptionDialog(document);
        });
        ownerPwdDialog.setCancelClickListener(v -> {
            ownerPwdDialog.dismiss();
        });
        ownerPwdDialog.show(fragment.getChildFragmentManager(), "inputOwnerPwdDialog");
    }

    private void showResultDialog(String title){
        CAlertDialog alertDialog = CAlertDialog.newInstance("", title);
        alertDialog.setCancelClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.setConfirmClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show(fragment.getParentFragmentManager(), "tips");
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
