/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.signature;


import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.signature.CPDFOwnerInfo;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.core.signature.CPDFSigner;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.tools.R;
import com.compdfkit.tools.signature.bean.CPDFDocumentSignInfo;
import com.compdfkit.tools.signature.bean.CPDFSignatureStatusInfo;
import com.compdfkit.ui.reader.CPDFReaderView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CertificateDigitalDatas {

    /**
     * Generate a digital signature certificate based on relevant information
     *
     * @param ownerInfo Certificate information
     * @param password
     * @param saveDir   Local storage save path
     * @param fileName  Certificate file name
     * @return Whether a certificate was generated
     */
    public static boolean generatePKCS12Cert(CPDFOwnerInfo ownerInfo, String password, String saveDir, String fileName, CPDFSignature.CertUsage certUsage) {
        String pfxPath = new File(saveDir, fileName).getAbsolutePath();
        return CPDFSignature.generatePKCS12Cert(ownerInfo, password, pfxPath, certUsage);
    }

    /**
     * Get certificate information
     *
     * @param certFilePath Certificate local file path
     * @param password
     * @return
     */
    @Nullable
    public static CPDFX509 getCertInfo(String certFilePath, String password) {
        if (CPDFSignature.checkPKCS12Password(certFilePath, password)) {
            CPDFX509 x509 = CPDFSignature.getX509ByPKCS12Cert(certFilePath, password);
            return x509;
        } else {
            return null;
        }
    }

    /**
     * Get a collection of digital signatures in a pdf document
     *
     * @param document
     * @return
     */
    @NotNull
    public static List<CPDFSignature> getDigitalSignList(CPDFDocument document) {
        List<CPDFSignature> list = new ArrayList<>();
        if (document == null) {
            return list;
        }
        for (int i = 0; i < document.getSignatureCount(); i++) {
            CPDFSignature signature = document.getPdfSignature(i);
            if (signature == null) {
                continue;
            }
            CPDFSigner[] signers = signature.getSignerArr();
            if (signers != null && signers.length > 0) {
                list.add(signature);
            }
        }
        return list;
    }

    public static boolean hasDigitalSignature(CPDFDocument document) {
        return getDigitalSignList(document).size() > 0;
    }

    /**
     * Write digital signature into pdf document
     *
     * @param document
     * @param signatureWidget
     * @param certPath        Signed certificate local file path
     * @param password        certificate password
     * @param savePath        Document save path
     * @return
     */
    public static boolean writeSignature(CPDFDocument document,
                                         CPDFSignatureWidget signatureWidget,
                                         String location,
                                         String reason,
                                         String certPath,
                                         String password,
                                         String savePath
    ) {
        return document.writeSignature(signatureWidget,
                savePath,
                certPath,
                password,
                location,
                reason,
                CPDFDocument.PDFDocMdpP.PDFDocMdpPForbidAllModify
        );
    }

    /**
     * Verify that the specified digital signature is valid
     *
     * @param signature
     * @param document
     * @return
     */
    public static CPDFSignatureStatusInfo verifyGetSignatureStatusInfo(CPDFDocument document, CPDFSignature signature) {
        CPDFSigner signer = signature.getSignerArr()[0];
        boolean verifyValid = signature.verify(document);
        boolean notChanged = signature.verifyDocument(document);
        // The signature is valid and the signature has not been modified to be valid
        boolean isSignVerified = verifyValid && notChanged;
        // Check if the certificate is trusted
        boolean certChainTrusted = signer.getCert().verifyGetChain(document.getContext(), signature);
        boolean certificateIsTrusted = signer.getCert().checkCertificateIsTrusted(document.getContext());
        boolean isTrusted = certChainTrusted || certificateIsTrusted;
        boolean isExpired = signer.getCert().isExpired();
        SignatureStatus status;
        if (isSignVerified && isTrusted) {
            status = SignatureStatus.SUCCESS;
        } else if (isSignVerified && !isTrusted) {
            status = SignatureStatus.UNKNOWN;
        } else {
            status = SignatureStatus.FAILURE;
        }
        return new CPDFSignatureStatusInfo(document.getContext(), signature, status, isSignVerified, isExpired, notChanged, isTrusted);
    }

    public static boolean certIsTrusted(Context context, CPDFSignature signature, CPDFX509 cpdfx509){
        boolean certChainTrusted = cpdfx509.verifyGetChain(context, signature);
        boolean certificateIsTrusted = cpdfx509.checkCertificateIsTrusted(context);
        return  certChainTrusted || certificateIsTrusted;
    }

    /**
     * Digital Signature Timestamp Authentication
     *
     * @param context
     * @param signature
     * @return
     */
    public static boolean verifyTimeStamp(Context context, CPDFSignature signature) {
        return signature.veryfyTimestamp(context);
    }

    public static CPDFDocumentSignInfo verifyDocumentSignStatus(CPDFDocument document) {
        List<CPDFSignature> signatures = getDigitalSignList(document);
        if (signatures != null && signatures.size() > 0) {
            boolean hasInValid = false;
            boolean hasUnknownStatusSign = false;
            for (CPDFSignature signature : signatures) {
                CPDFSignatureStatusInfo statusInfo = verifyGetSignatureStatusInfo(document, signature);
                if (statusInfo.getStatus() == SignatureStatus.FAILURE) {
                    // Invalid digital signature
                    hasInValid = true;
                }
                if (statusInfo.getStatus() == SignatureStatus.UNKNOWN) {
                    hasUnknownStatusSign = true;
                }
            }
            // Invalid signature in digital signature collection
            if (hasInValid) {
                String info = document.getContext().getString(signatures.size() == 1 ? R.string.tools_sign_the_sign_is_in_valid : R.string.tools_sign_at_least_one_signature_is_invalid);
                return new CPDFDocumentSignInfo(SignatureStatus.FAILURE, info);
            }
            String info;
            if (hasUnknownStatusSign) {
                info = document.getContext().getString(R.string.tools_sign_validity_is_unknown);
                return new CPDFDocumentSignInfo(SignatureStatus.UNKNOWN, info);
            } else {
                info = document.getContext().getString(R.string.tools_sign_the_sign_is_valid);
                return new CPDFDocumentSignInfo(SignatureStatus.SUCCESS, info);
            }
        } else {
            return new CPDFDocumentSignInfo(SignatureStatus.FAILURE, "");
        }
    }

    public static boolean removeDigitalSign(CPDFReaderView readerView, CPDFDocument document, CPDFSignature signature) {
        try {
            boolean result = document.removeSignature(signature, true, cpdfSignatureWidget ->{
                readerView.refreshSignatureWidget(cpdfSignatureWidget);
            });
            document.save();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<CPDFX509> getCertDetails(Context context, CPDFSignature signature) {
        CPDFSigner[] signers = signature.getSignerArr();
        List<CPDFX509> list = new ArrayList<>();
        if (signers != null && signers.length > 0) {
            CPDFSigner signer = signers[0];
            CPDFX509 cert = signer.getCert();
            list.add(cert);
            loopVerifyGetChain(context, list, signature, cert);
        }
        Collections.reverse(list);
        return list;
    }

    private static void loopVerifyGetChain(Context context, List<CPDFX509> list, CPDFSignature signature, CPDFX509 cert) {
        cert.verifyGetChain(context, signature);
        CPDFX509[] cpdfx509s = cert.getChain();
        if (cpdfx509s != null && cpdfx509s.length > 0) {
            CPDFX509 a = cpdfx509s[0];
            list.add(a);
            loopVerifyGetChain(context, list, signature, a);
        }
    }


    public static String getOwnerContent(CPDFOwnerInfo ownerInfo, String separator){
        StringBuilder builder = new StringBuilder();
        List<String> infos = new ArrayList<>();
        if (!TextUtils.isEmpty(ownerInfo.getCountry())){
            infos.add("C="+ownerInfo.getCountry());
        }
        if (!TextUtils.isEmpty(ownerInfo.getOrgnize())){
            infos.add("O="+ownerInfo.getOrgnize());
        }
        if (!TextUtils.isEmpty(ownerInfo.getOrgnizeUnit())){
            infos.add("OU="+ownerInfo.getOrgnizeUnit());
        }
        if (!TextUtils.isEmpty(ownerInfo.getCommonName())){
            infos.add("CN="+ownerInfo.getCommonName());
        }
        for (int i = 0; i < infos.size(); i++) {
            String item = infos.get(i);
            builder.append(item);
            if (i != infos.size() -1){
                builder.append(separator);
            }
        }
        return builder.toString();
    }
}
