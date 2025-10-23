/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import com.compdfkit.core.annotation.form.CPDFSignatureWidget;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.signature.CPDFDigitalSigConfig;
import com.compdfkit.core.signature.CPDFOwnerInfo;
import com.compdfkit.core.signature.CPDFSignature;
import com.compdfkit.core.signature.CPDFSigner;
import com.compdfkit.core.signature.CPDFX509;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.DateUtil;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class DigitalSignaturesTest extends PDFSamples {

    public DigitalSignaturesTest() {
        setTitle(R.string.digital_signature_title);
        setDescription(R.string.digital_signature_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        printDividingLine();

        //---------
        //Samples 0: Create a pfx format certificate for digital signature use
        generateCertificate();

        //--------
        //Samples 1: Create form fields for digital signature and digital signature using created pfx format certificate
        createSignature();

        //--------
        //Samples 2: Verify signature
        verifySignatureInfo();

        //--------
        //Samples 3: Verify certificate
        verifyCertificate();

        //--------
        //Samples 4: Print digital signature info
        printDigitalSignatureInfo();

        //--------
        //Samples 5: Trust certificate
        trustCertificate();

        //--------
        //Samples 6:Remove digital signature from PDF document
        removeDigitalSignature();

        printFooter();
    }

    /**
     * <b>Create pfx format certificate for digital signature use</b> <br>
     * <br><br>
     * <b>Password:</b> ComPDFKit <br>
     * <b>C=SG:</b> This represents the country code "SG," which typically stands for Singapore.<br><br>
     * <b>O=ComPDFKit:</b> This is the Organization (O) field, indicating the name of the organization or entity,
     *      in this case, "ComPDFKit." <br><br>
     * <b>D=R&D Department:</b> This is the Department (D) field,
     *      indicating the specific department within the organization,
     *      in this case, "R&D Department."<br><br>
     * <b>CN=Alan:</b> This is the Common Name (CN) field, which usually represents the name
     *      of the individual or entity. In this case, it is "Alan."<br><br>
     * <b>emailAddress=xxxx@example.com:</b> Email is xxxx@example.com <br><br>
     * <b>CPDFSignature.CertUsage.PDFAll: </b>Used for both digital signing and data validation simultaneously.<br><br>
     * <b>is_2048: </b> Enhanced security encryption.<br><br>
     */
    private void generateCertificate() {
        outputListener.println("generate certificate");
        String password = "ComPDFKit";
        CPDFOwnerInfo ownerInfo = new CPDFOwnerInfo();
        ownerInfo.setCommonName("Alan");
        ownerInfo.setOrgnize("ComPDFKit");
        ownerInfo.setEmail("xxxx@example.com");
        ownerInfo.setCountry("SG");
        ownerInfo.setOrgnizeUnit("R&D Department");
        CPDFSignature.CertUsage flag = CPDFSignature.CertUsage.PDFAll;
        File certFile = new File(outputDir(), "certificate/Certificate_2048.pfx");
        certFile.getParentFile().mkdirs();
        boolean result = CPDFSignature.generatePKCS12Cert(ownerInfo, password, certFile.getAbsolutePath(), flag);

        if (result) {
            outputListener.println("Done. File saved in Certificate.pfx");
            outputListener.println("Generate PKCS12 certificate done");
        }
        addFileList(certFile.getAbsolutePath());
        printDividingLine();
    }


    private void createSignature() {
        outputListener.println("Create digital signature.");

        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        //Insert a Form Signature Widget (unsigned)
        CPDFPage cpdfPage = document.pageAtIndex(0);
        RectF pageSize = cpdfPage.getSize();
        RectF signatureRect = new RectF(28, 420, 150, 370);
        signatureRect = cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), signatureRect);
        CPDFSignatureWidget signatureWidget = (CPDFSignatureWidget) cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_SignatureFields);
        signatureWidget.setFieldName("Signature1");
        int fillColor = 0xFF96B4D2;
        signatureWidget.setFillColor(fillColor);
        signatureWidget.setRect(signatureRect);
        signatureWidget.updateAp();


        // Make a digital signature
        String fileName = FileUtils.getNameWithoutExtension(document.getFileName()) + "_Signed.pdf";
        File file = new File(outputDir(), "digitalSignature/" + fileName);
        file.getParentFile().mkdirs();
        String password = "ComPDFKit";

        String certPath = FileUtils.getAssetsTempFile(context, "Certificate.pfx");
        CPDFX509 cpdfx509 = CPDFSignature.getX509ByPKCS12Cert(certPath, password);
        String location = cpdfx509.getCertInfo().getSubject().getCountry();
        String reason = "I am the owner of the document.";

        Bitmap bitmap = BitmapFactory.decodeFile(FileUtils.getAssetsTempFile(context, "ComPDFKit.png"));
        // Describe signature appearance information
        CPDFDigitalSigConfig sigConfig = new CPDFDigitalSigConfig();
        sigConfig.setText(cpdfx509.getCertInfo().getSubject().getCommonName());
        sigConfig.setTextColor(0xFF000000);
        sigConfig.setContentColor(0xFF000000);
        sigConfig.setLogo(bitmap);
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ").append(cpdfx509.getCertInfo().getSubject().getCommonName()).append("\n")
                        .append("Date: ").append(DateUtil.getDataTime("yyyy.MM.dd HH:mm:ss")).append("\n")
                        .append("Reason: ").append(reason).append("\n")
                        .append("Location: ").append(location).append("\n")
                        .append("DN: ").append(cpdfx509.getCertInfo().getSubject());
        sigConfig.setContent(builder.toString());
        sigConfig.setContentAlginLeft(false);
        sigConfig.setDrawLogo(true);

        boolean updateSignAp = signatureWidget.updateApWithDigitalSigConfig(sigConfig);
        if (updateSignAp){
            boolean writeSignResult = document.writeSignature(signatureWidget,
                    file.getAbsolutePath(),
                    certPath,
                    password,
                    location,
                    reason,
                    CPDFDocument.PDFDocMdpP.PDFDocMdpPForbidAllModify
            );
            if (writeSignResult) {
                addFileList(file.getAbsolutePath());
                outputListener.println("Done. File saved in " + fileName);
                outputListener.println("Create digital signature done");
            }
        }

        printDividingLine();
    }

    private void verifySignatureInfo() {
        outputListener.println("Verify digital signature");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "Signed.pdf"));
        // Iterate through all digital signatures
        for (int i = 0; i < document.getSignatureCount(); i++) {
            CPDFSignature signature = document.getPdfSignature(i);
            // Check if the signer array exists and is not empty
            if (signature.getSignerArr() != null && signature.getSignerArr().length > 0) {
                CPDFSigner signer = signature.getSignerArr()[0];

                // Verify the validity of the signature
                boolean verifyValid = signature.verify(document);

                // Verify if the document has not been modified
                boolean unmodified = signature.verifyDocument(document);

                // Determine if the signature is valid and the document is unmodified
                boolean isSignVerified = verifyValid && unmodified;

                // Check if the certificate is trusted
                boolean certChainTrusted = signer.getCert().verifyGetChain(document.getContext(), signature);
                boolean certificateIsTrusted = signer.getCert().checkCertificateIsTrusted(document.getContext());
                boolean certIsTrusted = certChainTrusted || certificateIsTrusted;

                // Check if the certificate has expired
                boolean isExpired = signer.getCert().isExpired();

                // Take appropriate actions based on the verification results
                if (isSignVerified && certIsTrusted) {
                    // Signature is valid and the certificate is trusted
                    // Perform corresponding actions

                } else if (isSignVerified && !certIsTrusted) {
                    // Signature is valid but the certificate is not trusted
                    // Perform corresponding actions
                } else {
                    // Signature is invalid
                    // Perform corresponding actions
                }
                outputListener.println("Is the certificate trusted: " + certIsTrusted);
                outputListener.println("Is the signature verified: " + isSignVerified);
            }
        }
        outputListener.println("Verify digital signature done.");
        printDividingLine();
    }

    private void verifyCertificate() {
        outputListener.println("verify certificate");
        // Verify whether the specified certificate file is trusted
        String certFilePath = FileUtils.getAssetsTempFile(context, "Certificate.pfx");
        String password = "ComPDFKit";
        if (CPDFSignature.checkPKCS12Password(certFilePath, password)) {
            CPDFX509 x509 = CPDFSignature.getX509ByPKCS12Cert(certFilePath, password);
            boolean result = x509.addToTrustedCertificates(context);
            boolean isTrusted = x509.checkCertificateIsTrusted(context);
            if (isTrusted) {
                outputListener.println("Certificate is trusted");
            } else {
                outputListener.println("Certificate is not trusted");
            }
            outputListener.println("Verify certificate done.");
        }
        printDividingLine();
    }

    private void printDigitalSignatureInfo() {
        outputListener.println("Print digital signature info.");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "Signed.pdf"));
        for (int i = 0; i < document.getSignatureCount(); i++) {
            CPDFSignature signature = document.getPdfSignature(i);
            // Check if the signer array exists and is not empty
            if (signature.getSignerArr() != null && signature.getSignerArr().length > 0) {
                CPDFSigner signer = signature.getSignerArr()[0];
                CPDFOwnerInfo subject = signer.getCert().getCertInfo().getSubject();
                outputListener.println("Name: " + signature.getName());
                outputListener.println("Location: " + signature.getLocation());
                outputListener.println("Reason: " + signature.getReason());
                outputListener.println("Date: " + DateUtil.transformPDFDate(signature.getDate()));
                outputListener.println("Subject: " + subject.toString());
            }
        }
        outputListener.println("Print digital signature info done.");
        printDividingLine();
    }

    private void trustCertificate(){
        outputListener.println("Trust certificate");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "Signed.pdf"));
        CPDFSignature signature = document.getPdfSignature(0);
        CPDFSigner signer = signature.getSignerArr()[0];
        CPDFX509 cpdfx509 = signer.getCert();
        outputListener.println("Certificate trusted status: " + cpdfx509.checkCertificateIsTrusted(context));
        outputListener.println("---Begin trusted---");
        cpdfx509.addToTrustedCertificates(context);
        outputListener.println("Certificate trusted status: " + cpdfx509.checkCertificateIsTrusted(context));
        outputListener.println("Trust certificate done.");
        printDividingLine();
    }

    private void removeDigitalSignature() {
        outputListener.println("Remove digital signature");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "Signed.pdf"));
        for (int i = 0; i < document.getSignatureCount(); i++) {
            CPDFSignature signature = document.getPdfSignature(i);
            if (i == 0) {
                document.removeSignature(signature, true, null);
            }
        }
        String fileName = FileUtils.getNameWithoutExtension(document.getFileName()) + "_RemoveSign.pdf";
        File file = new File(outputDir(), "digitalSignature/" + fileName);
        saveSamplePDF(document, file, true);
        outputListener.println("Done. File saved in " + fileName);
        printDividingLine();
    }
}
