/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;

public class CVerifyPasswordDialogFragment extends CBasicBottomSheetDialogFragment {

    private AppCompatImageView ivClose;

    private AppCompatImageView ivClean;

    private AppCompatEditText etPassword;

    private AppCompatButton btnConfirm;

    private AppCompatTextView tvErrorMsg;

    private CVerifyCompleteListener completeListener;

    private COnDialogDismissListener dismissListener;

    private CPDFDocument document;

    private Uri uri;

    private String pdfFilePath;

    public static CVerifyPasswordDialogFragment newInstance(CPDFDocument document, Uri uri) {
        Bundle args = new Bundle();
        CVerifyPasswordDialogFragment fragment = new CVerifyPasswordDialogFragment();
        fragment.setPDFDocument(document, uri);
        fragment.setArguments(args);
        return fragment;
    }

    public static CVerifyPasswordDialogFragment newInstance(CPDFDocument document, String pdfFilePath) {
        Bundle args = new Bundle();
        CVerifyPasswordDialogFragment fragment = new CVerifyPasswordDialogFragment();
        fragment.setPDFDocument(document, pdfFilePath);
        fragment.setArguments(args);
        return fragment;
    }

    public static CVerifyPasswordDialogFragment newInstance(CPDFDocument document, String pdfFilePath, Uri uri) {
        Bundle args = new Bundle();
        CVerifyPasswordDialogFragment fragment = new CVerifyPasswordDialogFragment();
        fragment.setPDFDocument(document, pdfFilePath);
        fragment.setPDFDocument(document, uri);
        fragment.setArguments(args);
        return fragment;
    }

    public void setPDFDocument(CPDFDocument document, String pdfFilePath) {
        this.document = document;
        this.pdfFilePath = pdfFilePath;
    }

    public void setPDFDocument(CPDFDocument document, Uri uri) {
        this.document = document;
        this.uri = uri;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected float dimAmount() {
        return CViewUtils.isLandScape(getContext()) ? 0.2F : 0F;
    }

    @Override
    protected boolean draggable() {
        return false;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_verify_password_dialog_fragment;
    }

    @Override
    protected void onCreateView(View rootView) {
        ivClose = rootView.findViewById(R.id.iv_close);
        ivClean = rootView.findViewById(R.id.iv_clean);
        etPassword = rootView.findViewById(R.id.et_password);
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
        tvErrorMsg = rootView.findViewById(R.id.tv_error_msg);
    }

    @Override
    protected void onViewCreate() {
        ivClose.setOnClickListener(v -> dismiss());
        ivClean.setOnClickListener(v -> etPassword.setText(""));
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnConfirm.setEnabled(!TextUtils.isEmpty(s) && s.length() > 0);
                if (tvErrorMsg.getVisibility() == View.VISIBLE) {
                    tvErrorMsg.setVisibility(View.GONE);
                }
                ivClean.setVisibility(!TextUtils.isEmpty(s) && s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnConfirm.setOnClickListener(v -> {
            verifyPassword();
        });
    }

    private void verifyPassword() {
        if (!TextUtils.isEmpty(etPassword.getText())) {
            String password = etPassword.getText().toString();
            if (document != null) {
                CPDFDocument.PDFDocumentError error = null;
                if (uri != null) {
                    try {
                        getContext().grantUriPermission(getContext().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception e) {
                    }
                    error = document.open(uri, password);
                } else if (!TextUtils.isEmpty(pdfFilePath)) {
                    error = document.open(pdfFilePath, password);
                }
                if (error != null) {
                    switch (error) {
                        case PDFDocumentErrorSuccess:
                            if (completeListener != null) {
                                completeListener.complete(document);
                                dismiss();
                            }
                            break;
                        case PDFDocumentErrorPassword:
                            tvErrorMsg.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public String getPassword(){
        return etPassword.getText().toString();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.dismiss();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dismissListener != null) {
            dismissListener.dismiss();
        }
    }

    public void setVerifyCompleteListener(CVerifyCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void setDismissListener(COnDialogDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface CVerifyCompleteListener {

        void complete(CPDFDocument document);
    }
}
