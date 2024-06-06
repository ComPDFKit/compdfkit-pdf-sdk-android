/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfsign;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFImageScaleType;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.signature.CPDFDigitalSigConfig;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.image.CBitmapUtil;
import com.compdfkit.tools.common.utils.threadpools.CThreadPoolUtils;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleUIParams;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;
import com.compdfkit.tools.signature.CertificateDigitalDatas;
import com.compdfkit.tools.signature.importcert.create.CPDFSelectDigitalSignatureDialog;
import com.compdfkit.tools.signature.importcert.create.CSelectSignTypeDialog;
import com.compdfkit.tools.signature.info.CertDigitalSignInfoDialog;
import com.compdfkit.tools.signature.preview.CDigitalSignStylePreviewDialog;
import com.compdfkit.ui.proxy.form.CPDFSignatureWidgetImpl;

import java.io.File;
import java.util.UUID;


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
        if (!cpdfSignatureWidget.isSigned()) {

            if (getCurrentMode() != CPreviewMode.Signature) {
                fillElectronicSignature();
                return;
            }

            // unsigned
            // select signature type
            CSelectSignTypeDialog signTypeDialog = CSelectSignTypeDialog.newInstance();
            signTypeDialog.setSelectSignaturesTypeListener(type -> {
                signTypeDialog.dismiss();
                if (type == CSelectSignTypeDialog.SignatureType.ElectronicSignature){
                    fillElectronicSignature();
                }else {
                    fillDigitalSign(cpdfSignatureWidget);
                }
            });
            if (readerView.getContext() instanceof FragmentActivity) {
                signTypeDialog.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "selectSignType");
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
                        fillElectronicSignature();
                    }
                });
            });
        }
    }

    /**
     * A list of created electronic signature styles pops up,
     * and you can also choose to create a new signature style.
     */
    private void fillElectronicSignature(){
        CStyleManager styleManager = new CStyleManager(this, pageView);
        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(styleManager.getStyle(CStyleType.FORM_SIGNATURE_FIELDS));
        CStyleUIParams styleUiParams = CStyleUIParams.defaultStyle(CStyleType.FORM_SIGNATURE_FIELDS);
        styleDialogFragment.setStyleUiConfig(styleUiParams);
        styleDialogFragment.setStyleDialogDismissListener(() -> {
            CAnnotStyle annotStyle = styleDialogFragment.getAnnotStyle();
            if (!TextUtils.isEmpty(annotStyle.getImagePath())) {
                try {
                    Bitmap bitmap = CBitmapUtil.decodeBitmap(annotStyle.getImagePath());
                    ((CPDFSignatureWidget) pdfAnnotation).updateApWithBitmap(bitmap, CPDFImageScaleType.SCALETYPE_fitCenter);
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (readerView.getContext() instanceof FragmentActivity) {
            styleDialogFragment.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "styleDialog");
        }
    }

    private void fillDigitalSign(CPDFSignatureWidget cpdfSignatureWidget){
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
                            Environment.getExternalStorageDirectory().getAbsolutePath(),
                            context.getString(R.string.tools_select_folder),
                            context.getString(R.string.tools_save_to_this_directory)
                    );
                    directoryDialog.setSelectFolderListener(dir -> {
                        sigitalSignatureDocument(cpdfSignatureWidget, config, location, reason, certFilePath, certPassword, dir);
                    });
                    if (readerView.getContext() instanceof FragmentActivity) {
                        directoryDialog.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "selectFolderDialog");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            });
            if (readerView.getContext() instanceof FragmentActivity) {
                previewDialog.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "digitalSignPreviewDialog");
            }
        });
        if (readerView.getContext() instanceof FragmentActivity) {
            dialog.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "styleDialog");
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
        if (readerView.getContext() instanceof FragmentActivity) {
            signInfoDialog.show(((FragmentActivity) readerView.getContext()).getSupportFragmentManager(), "signInfoDialog");
        }
    }

    private void sigitalSignatureDocument(CPDFSignatureWidget cpdfSignatureWidget,
                                          CPDFDigitalSigConfig config,
                                          String location,
                                          String reason,
                                          String certFilePath,
                                          String certPassword,
                                          String saveDir) {

        String uuid = UUID.randomUUID().toString().substring(0, 4);
        String fileName = readerView.getPDFDocument().getFileName();
        File saveFile = new File(saveDir + File.separator + fileName + "_" + uuid, readerView.getPDFDocument().getFileName());
        saveFile.getParentFile().mkdirs();

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

        // sign this pdf document
        boolean result = CertificateDigitalDatas.writeSignature(readerView.getPDFDocument(),
                cpdfSignatureWidget, location, reason,
                certFilePath, certPassword, saveFile.getAbsolutePath());
        if (result) {
            CToastUtil.showToast(readerView.getContext(), readerView.getContext().getString(R.string.tools_digital_sign_success));
            if (readerView.getParent() instanceof CPDFViewCtrl) {
                // open signed pdf document
                CPDFViewCtrl pdfView = (CPDFViewCtrl) readerView.getParent();
                pdfView.openPDF(saveFile.getAbsolutePath());
            }
        } else {
            CToastUtil.showToast(readerView.getContext(), "Signature Fail");
        }
    }

    protected CPreviewMode getCurrentMode(){
        return CPreviewMode.Viewer;
    }

}
