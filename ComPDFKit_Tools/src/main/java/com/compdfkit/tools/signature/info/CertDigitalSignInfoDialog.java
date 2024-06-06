/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.info;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.signature.CPDFCertInfo;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.core.signature.CPDFSigner;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.basic.fragment.CBasicBottomSheetDialogFragment;
import com.compdfkit.tools.common.interfaces.COnDialogDismissListener;
import com.compdfkit.tools.common.utils.date.CDateUtil;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.bean.CPDFSignatureStatusInfo;

public class CertDigitalSignInfoDialog extends CBasicBottomSheetDialogFragment implements View.OnClickListener {

    private AppCompatTextView tvTitle;

    private AppCompatImageView ivClose;

    private AppCompatTextView tvFounder;

    private AppCompatTextView tvDateOfSignature;

    private AppCompatButton btnSignDetails;

    private CPDFSignature cpdfSignature;

    private AppCompatTextView tvSignStatement;

    private CPDFDocument document;

    private COnDialogDismissListener dialogDismissListener;

    private boolean refresh = false;

    public static CertDigitalSignInfoDialog newInstance() {
        Bundle args = new Bundle();
        CertDigitalSignInfoDialog fragment = new CertDigitalSignInfoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void setDocument(CPDFDocument document) {
        this.document = document;
    }

    public void setPDFSignature(CPDFSignature signature) {
        this.cpdfSignature = signature;
    }

    @Override
    protected int getStyle() {
        return R.style.Tools_Base_Theme_BasicBottomSheetDialogStyle_TopCorners;
    }

    @Override
    protected boolean draggable() {
        return false;
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.tools_sign_certificate_digital_sign_info_fragment;
    }

    @Override
    protected void onCreateView(View view) {
        tvTitle = view.findViewById(R.id.tv_tool_bar_title);
        ivClose = view.findViewById(R.id.iv_tool_bar_close);
        tvFounder = view.findViewById(R.id.tv_sign_founder);
        btnSignDetails = view.findViewById(R.id.btn_cert_detail);
        tvDateOfSignature = view.findViewById(R.id.tv_date_of_sign);
        tvSignStatement = view.findViewById(R.id.tv_sign_statement_info);
        ivClose.setOnClickListener(this);
        btnSignDetails.setOnClickListener(this);
    }

    @Override
    protected void onViewCreate() {
        tvTitle.setText(R.string.tools_digital_signature_detail);
        refreshInfo();
    }

    private void refreshInfo() {
        CThreadPoolUtils.getInstance().executeIO(()->{
            try {
                if (cpdfSignature != null) {
                    CPDFSigner[] signers = cpdfSignature.getSignerArr();
                    if (signers != null && signers.length > 0) {
                        CPDFX509 cert = signers[0].getCert();
                        CPDFCertInfo certInfo = cert.getCertInfo();
                        String commonName = certInfo.getSubject().getCommonName();
                        CThreadPoolUtils.getInstance().executeMain(()->{
                            tvFounder.setText(commonName);
                        });
                    }
                    String date = CDateUtil.transformPDFDate(cpdfSignature.getDate());
                    CPDFSignatureStatusInfo signatureStatusInfo = CertificateDigitalDatas.verifyGetSignatureStatusInfo(document, cpdfSignature);
                    StringBuilder builder = new StringBuilder();
                    for (String certAuthorityStatement : signatureStatusInfo.getCertAuthorityStatements()) {
                        builder.append(certAuthorityStatement).append("\n\n");
                    }
                    CThreadPoolUtils.getInstance().executeMain(()->{
                        try {
                            tvDateOfSignature.setText(CDateUtil.formatDate(date, getString(R.string.tools_signature_date_pattern)));
                            tvSignStatement.setText(builder);
                        }catch (Exception e){

                        }
                    });

                }
            }catch (Exception e){

            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_tool_bar_close) {
            dismiss();
        } else if (v.getId() == R.id.btn_cert_detail) {
            CertDetailsDialog detailsDialog = CertDetailsDialog.newInstance(cpdfSignature);
            detailsDialog.setTrustedCertRefreshListener(() -> {
                refresh = true;
                refreshInfo();
            });
            detailsDialog.show(getChildFragmentManager(), "certDetailDialog");
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dialogDismissListener != null && refresh) {
            dialogDismissListener.dismiss();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dialogDismissListener != null&& refresh) {
            dialogDismissListener.dismiss();
        }
    }

    public void setTrustedCertRefreshListener(COnDialogDismissListener dialogDismissListener) {
        this.dialogDismissListener = dialogDismissListener;
    }
}
