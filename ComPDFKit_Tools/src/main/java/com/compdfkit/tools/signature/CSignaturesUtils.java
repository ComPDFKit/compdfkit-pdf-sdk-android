/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.CPDFImageScaleType;
import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.signature.CPDFDigitalSigConfig;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.utils.CToastUtil;
import com.compdfkit.tools.common.utils.image.CBitmapUtil;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.common.views.directory.CFileDirectoryDialog;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleUIParams;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.tools.common.views.pdfview.CPDFViewCtrl;
import com.compdfkit.tools.signature.importcert.create.CPDFSelectDigitalSignatureDialog;
import com.compdfkit.tools.signature.importcert.create.CSelectSignTypeDialog;
import com.compdfkit.tools.signature.preview.CDigitalSignStylePreviewDialog;
import com.compdfkit.ui.proxy.form.CPDFSignatureWidgetImpl;
import com.compdfkit.ui.reader.PageView;
import com.compdfkit.ui.reader.ReaderView;

import java.io.File;
import java.util.UUID;

public class CSignaturesUtils {

    public static void electronicSignature(CPDFSignatureWidgetImpl signatureWidgetImpl, PageView pageView){
        CStyleManager styleManager = new CStyleManager(signatureWidgetImpl, pageView);
        CPDFSignatureWidget signatureWidget = signatureWidgetImpl.onGetAnnotation();
        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(styleManager.getStyle(CStyleType.FORM_SIGNATURE_FIELDS));
        CStyleUIParams styleUiParams = CStyleUIParams.defaultStyle(pageView.getContext(), CStyleType.FORM_SIGNATURE_FIELDS);
        styleDialogFragment.setStyleUiConfig(styleUiParams);
        styleDialogFragment.setStyleDialogDismissListener(() -> {
            CAnnotStyle annotStyle = styleDialogFragment.getAnnotStyle();
            if (!TextUtils.isEmpty(annotStyle.getImagePath())) {
                try {
                    Bitmap bitmap = CBitmapUtil.decodeBitmap(annotStyle.getImagePath());
                    signatureWidget.updateApWithBitmap(bitmap, CPDFImageScaleType.SCALETYPE_fitCenter);
                    signatureWidgetImpl.refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(pageView.getContext());
        if (fragmentActivity != null) {
            styleDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "styleDialog");
        }
    }

    public static void digitalSign(CPDFSignatureWidgetImpl signatureWidgetImpl, ReaderView readerView, PageView pageView){
        CPDFSelectDigitalSignatureDialog dialog = CPDFSelectDigitalSignatureDialog.newInstance();
        CPDFSignatureWidget signatureWidget = signatureWidgetImpl.onGetAnnotation();
        dialog.setCertDigitalSignListener((certFilePath, certPassword, signImagePath) -> {
            CPDFX509 cpdfx509 = CertificateDigitalDatas.getCertInfo(certFilePath, certPassword);
            if (cpdfx509 == null) {
                return;
            }
            String dn = CertificateDigitalDatas.getOwnerContent(cpdfx509.getCertInfo().getSubject(), ",");
            CDigitalSignStylePreviewDialog previewDialog = CDigitalSignStylePreviewDialog.newInstance(signImagePath, cpdfx509.getCertInfo().getSubject().getCommonName(), dn);
            previewDialog.setResultDigitalSignListener((imagePath, config, location, reason) -> {
                try {
                    Context context = pageView.getContext();
                    CFileDirectoryDialog directoryDialog = CFileDirectoryDialog.newInstance(
                            Environment.getExternalStorageDirectory().getAbsolutePath(),
                            context.getString(R.string.tools_select_folder),
                            context.getString(R.string.tools_save_to_this_directory)
                    );
                    directoryDialog.setSelectFolderListener(dir -> {
                        sigitalSignatureDocument(signatureWidget,readerView, config, location, reason, certFilePath, certPassword, dir);
                    });
                    FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(pageView.getContext());
                    if (fragmentActivity != null) {
                        directoryDialog.show(fragmentActivity.getSupportFragmentManager(), "selectFolderDialog");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            });
            FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(pageView.getContext());
            if (fragmentActivity != null) {
                previewDialog.show(fragmentActivity.getSupportFragmentManager(), "digitalSignPreviewDialog");
            }
        });
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(pageView.getContext());
        if (fragmentActivity != null) {
            dialog.show(fragmentActivity.getSupportFragmentManager(), "styleDialog");
        }
    }

    private static void sigitalSignatureDocument(CPDFSignatureWidget cpdfSignatureWidget,
                                          ReaderView readerView,
                                          CPDFDigitalSigConfig config,
                                          String location,
                                          String reason,
                                          String certFilePath,
                                          String certPassword,
                                          String saveDir) {

        String uuid = UUID.randomUUID().toString().substring(0, 4);
        Context context = readerView.getContext();
        String fileName = readerView.getPDFDocument().getFileName();
        File saveFile = new File(saveDir + File.separator + fileName + "_" + uuid, readerView.getPDFDocument().getFileName());
        saveFile.getParentFile().mkdirs();

        boolean updateSignApResult = cpdfSignatureWidget.updateApWithDigitalSigConfig(config);
        if (!updateSignApResult){
            return;
        }
        CPDFX509 cpdfx509 = CertificateDigitalDatas.getCertInfo(certFilePath, certPassword);
        if (cpdfx509 != null) {
            if (!cpdfx509.checkCertificateIsTrusted(context)) {
                cpdfx509.addToTrustedCertificates(context);
            }
        }

        // sign this pdf document
        boolean result = CertificateDigitalDatas.writeSignature(readerView.getPDFDocument(),
                cpdfSignatureWidget, location, reason,
                certFilePath, certPassword, saveFile.getAbsolutePath());
        if (result) {
            CToastUtil.showToast(context, context.getString(R.string.tools_digital_sign_success));
            if (readerView.getParent() instanceof CPDFViewCtrl) {
                // open signed pdf document
                CPDFViewCtrl pdfView = (CPDFViewCtrl) readerView.getParent();
                pdfView.openPDF(saveFile.getAbsolutePath());
            }
        } else {
            cpdfSignatureWidget.resetForm();
            CToastUtil.showToast(context, context.getString(R.string.tools_digital_sign_failures));
        }
    }

    public static void manualSelectSignature(CPDFSignatureWidgetImpl signatureWidgetImpl, ReaderView readerView, PageView pageView) {
        // select signature type
        CSelectSignTypeDialog signTypeDialog = CSelectSignTypeDialog.newInstance();
        signTypeDialog.setSelectSignaturesTypeListener(type -> {
            signTypeDialog.dismiss();
            if (type == CSelectSignTypeDialog.SignatureType.ElectronicSignature){
                electronicSignature(signatureWidgetImpl, pageView);
            }else {
                digitalSign(signatureWidgetImpl, readerView, pageView);
            }
        });

        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(pageView.getContext());
        if (fragmentActivity != null) {
            signTypeDialog.show(fragmentActivity.getSupportFragmentManager(), "selectSignType");
        }
    }

}
