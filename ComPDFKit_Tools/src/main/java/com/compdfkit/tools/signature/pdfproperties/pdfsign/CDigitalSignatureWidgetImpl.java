/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature.pdfproperties.pdfsign;

import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.signature.CPDFDigitalSigConfig;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.storage.CPDFPublicFileSaver;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.common.utils.storage.CPDFStorageManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.importcert.create.CPDFSelectDigitalSignatureDialog;
import com.compdfkit.tools.signature.info.CertDigitalSignInfoDialog;
import com.compdfkit.tools.signature.preview.CDigitalSignStylePreviewDialog;
import com.compdfkit.ui.proxy.form.CPDFSignatureWidgetImpl;

import java.util.UUID;


public class CDigitalSignatureWidgetImpl extends CPDFSignatureWidgetImpl {

    @Override
    public void onSignatureWidgetFocused(CPDFSignatureWidget cpdfSignatureWidget) {
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        CPDFSignature signature = readerView.getPDFDocument().getPdfSignature(cpdfSignatureWidget);
        if (signature != null && signature.isDigitalSigned()) {
            CertDigitalSignInfoDialog signInfoDialog = CertDigitalSignInfoDialog.newInstance();
            signInfoDialog.setDocument(readerView.getPDFDocument());
            signInfoDialog.setPDFSignature(signature);
            if (fragmentActivity != null) {
                signInfoDialog.show(fragmentActivity.getSupportFragmentManager(), "signInfoDialog");
            }
            return;
        }
        CPDFSelectDigitalSignatureDialog dialog = CPDFSelectDigitalSignatureDialog.newInstance();
        dialog.setCertDigitalSignListener((certFilePath, certPassword, signImagePath) -> {
            CPDFX509 cpdfx509 = CertificateDigitalDatas.getCertInfo(certFilePath, certPassword);
            if (cpdfx509 == null) {
                return;
            }
            String dn = CertificateDigitalDatas.getOwnerContent(cpdfx509.getCertInfo().getSubject(), ",");
            CDigitalSignStylePreviewDialog previewDialog = CDigitalSignStylePreviewDialog.newInstance(signImagePath, cpdfx509.getCertInfo().getSubject().getCommonName(), dn);
            previewDialog.setResultDigitalSignListener((imagePath, config, location, reason) -> {
                try {
                    Context context = readerView.getContext();
                    CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(
                            CPDFStorageManager.getDefaultDirectoryDialogPath(),
                            context.getString(R.string.tools_select_folder),
                            context.getString(R.string.tools_save_to_this_directory)
                    );
                    directoryDialog.setSelectFolderListener(dir -> signDocument(cpdfSignatureWidget, config, location, reason, certFilePath, certPassword, dir));
                    if (fragmentActivity != null) {
                        directoryDialog.show(fragmentActivity.getSupportFragmentManager(), "selectFolderDialog");
                    }
                } catch (Exception ignored) {
                }
                dialog.dismiss();
            });
            if (fragmentActivity != null) {
                previewDialog.show(fragmentActivity.getSupportFragmentManager(), "digitalSignPreviewDialog");
            }
        });
        if (fragmentActivity != null) {
            dialog.show(fragmentActivity.getSupportFragmentManager(), "styleDialog");
        }
    }


    private void signDocument(CPDFSignatureWidget cpdfSignatureWidget,
                              CPDFDigitalSigConfig config,
                              String location,
                              String reason,
                              String certFilePath,
                              String certPassword,
                              String saveDir) {

        String uuid = UUID.randomUUID().toString().substring(0, 4);
        String fileName = readerView.getPDFDocument().getFileName();
        String subDir = fileName + "_" + uuid;
        boolean updateSignApResult = cpdfSignatureWidget.updateApWithDigitalSigConfig(config);
        if (!updateSignApResult){
            return;
        }
        CPDFX509 cpdfx509 = CertificateDigitalDatas.getCertInfo(certFilePath, certPassword);
        if (cpdfx509 != null) {
            if (!cpdfx509.checkCertificateIsTrusted(readerView.getContext())) {
                cpdfx509.addToTrustedCertificates(readerView.getContext());
            }
        }

        CPDFPublicFileSaver.SaveResult saveResult = CPDFPublicFileSaver.savePdfToSelectedDirectory(
                readerView.getContext(),
                saveDir,
                subDir,
                fileName,
                false,
                tempPath -> CertificateDigitalDatas.writeSignature(readerView.getPDFDocument(),
                        cpdfSignatureWidget, location, reason,
                        certFilePath, certPassword, tempPath));
        if (saveResult.isSuccess()) {
            CToastUtil.showToast(readerView.getContext(), readerView.getContext().getString(R.string.tools_digital_sign_success));
            if (readerView.getParent() instanceof CPDFViewCtrl) {
                CPDFViewCtrl pdfView = (CPDFViewCtrl) readerView.getParent();
                if (saveResult.getPublicUri() != null) {
                    pdfView.openPDF(saveResult.getPublicUri());
                } else if (!TextUtils.isEmpty(saveResult.getOpenPath())) {
                    pdfView.openPDF(saveResult.getOpenPath());
                }
            }
        } else {
            CToastUtil.showToast(readerView.getContext(), readerView.getContext().getString(R.string.tools_digital_sign_failures));
        }
    }
}
