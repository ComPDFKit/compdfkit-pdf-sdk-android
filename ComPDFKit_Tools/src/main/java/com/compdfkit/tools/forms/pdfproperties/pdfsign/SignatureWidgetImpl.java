/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfsign;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.GlobalConfig;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.signature.CSignaturesUtils;
import com.compdfkit.tools.signature.info.CertDigitalSignInfoDialog;
import com.compdfkit.ui.proxy.form.CPDFSignatureWidgetImpl;


/**
 * Form signature form click interaction is implemented. This object contains two signature interactions: electronic signature and digital signature.
 * If you only need electronic signature interaction, please check
 * @see CustomSignatureWidgetImpl
 * @see com.compdfkit.tools.signature.pdfproperties.pdfsign.CDigitalSignatureWidgetImpl
 *
 * <br>
 * <blockquote><pre>
 * cpdfReaderView.getAnnotImplRegistry().registImpl(CPDFSignatureWidget.class, SignatureWidgetImpl.class);
 * </pre></blockquote>
 */
public class SignatureWidgetImpl extends CPDFSignatureWidgetImpl {

    private boolean requesting = false;

    @Override
    public void onSignatureWidgetFocused(CPDFSignatureWidget cpdfSignatureWidget) {
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (!cpdfSignatureWidget.isSigned()) {

            CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
            if (configuration == null){
                CSignaturesUtils.manualSelectSignature(this,readerView, pageView);
            }else {
                GlobalConfig globalConfig = configuration.globalConfig;
                switch (globalConfig.signatureType) {
                    case Manual:
                        CSignaturesUtils.manualSelectSignature(this,readerView, pageView);
                        break;
                    case Digital:
                        CSignaturesUtils.digitalSign(this,readerView, pageView);
                        break;
                    case Electronic:
                        CSignaturesUtils.electronicSignature(this, pageView);
                        break;
                }
            }
            return;
        }

        if (!requesting){
            requesting = true;
            CThreadPoolUtils.getInstance().executeIO(()->{
                // The form has been signed
                CPDFSignature signature = readerView.getPDFDocument().getPdfSignature(cpdfSignatureWidget);
                requesting = false;
                CThreadPoolUtils.getInstance().executeMain(()->{
                    // Determine whether it is a digital signature
                    if (signature != null && signature.isDigitalSigned()){
                        // digital signature
                        showDigitalSignInfo(signature);
                    } else {
                        // electronic signature
                        CSignaturesUtils.electronicSignature(this, pageView);
                    }
                });
            });
        }
    }

    /**
     * View digital signature and signature certificate related information
     * @param signature
     */
    private void showDigitalSignInfo(CPDFSignature signature){
        CertDigitalSignInfoDialog signInfoDialog = CertDigitalSignInfoDialog.newInstance();
        signInfoDialog.setDocument(readerView.getPDFDocument());
        signInfoDialog.setPDFSignature(signature);
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity != null) {
            signInfoDialog.show(fragmentActivity.getSupportFragmentManager(), "signInfoDialog");
        }
    }
}
