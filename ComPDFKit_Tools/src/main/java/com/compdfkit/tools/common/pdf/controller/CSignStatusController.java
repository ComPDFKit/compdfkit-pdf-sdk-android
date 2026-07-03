/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.tools.common.pdf.controller;

import android.view.View;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.bean.CPDFDocumentSignInfo;
import com.compdfkit.tools.signature.info.signlist.CPDFCertDigitalSignListDialog;
import com.compdfkit.tools.signature.verify.CVerifySignStatusView;

/**
 * Manages digital signature verification status display and the sign status view.
 */
public class CSignStatusController {

    private final CPDFDocumentController ctx;

    public CSignStatusController(CPDFDocumentController ctx) {
        this.ctx = ctx;
    }

    /**
     * Initialize the sign status view and wire up its detail button.
     */
    public void init() {
        CVerifySignStatusView signStatusView = ctx.getFragment().signStatusView;
        signStatusView.initWithPDFView(ctx.getPdfView());
        signStatusView.getBtnDetails().setOnClickListener(v -> {
            CPDFCertDigitalSignListDialog signListDialog = CPDFCertDigitalSignListDialog.newInstance();
            signListDialog.initWithPDFView(ctx.getPdfView());
            signListDialog.setDialogDismissListener(this::verify);
            signListDialog.show(ctx.getFragment().getChildFragmentManager(), "signListDialog");
        });
    }

    /**
     * Verify the document's digital signature status and show/hide the status view accordingly.
     */
    public void verify() {
        CPDFDocument document = ctx.getPdfView().getCPdfReaderView().getPDFDocument();
        CVerifySignStatusView signStatusView = ctx.getFragment().signStatusView;
        if (CertificateDigitalDatas.hasDigitalSignature(document)) {
            CThreadPoolUtils.getInstance().executeIO(() -> {
                CPDFDocumentSignInfo status = CertificateDigitalDatas.verifyDocumentSignStatus(document);
                CThreadPoolUtils.getInstance().executeMain(() -> {
                    signStatusView.setStatus(status);
                    ctx.getScreenManager().fillScreenManager.bindTopToolView(signStatusView);
                    ctx.getScreenManager().constraintShow(signStatusView);
                });
            });
        } else {
            hide();
        }
    }

    /**
     * Hide the digital signature status view if it is currently visible.
     */
    public void hide() {
        CVerifySignStatusView signStatusView = ctx.getFragment().signStatusView;
        if (signStatusView.getVisibility() == View.VISIBLE) {
            ctx.getScreenManager().fillScreenManager.removeAndHideToolView(signStatusView);
            ctx.getScreenManager().constraintHide(signStatusView);
        }
    }
}
