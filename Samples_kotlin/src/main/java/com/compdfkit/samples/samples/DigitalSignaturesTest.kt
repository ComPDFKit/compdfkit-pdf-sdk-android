/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RectF
import com.compdfkit.core.annotation.form.CPDFSignatureWidget
import com.compdfkit.core.annotation.form.CPDFWidget
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.signature.CPDFDigitalSigConfig
import com.compdfkit.core.signature.CPDFOwnerInfo
import com.compdfkit.core.signature.CPDFSignature
import com.compdfkit.core.signature.CPDFSignature.CertUsage
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.DateUtil
import com.compdfkit.samples.util.DateUtil.transformPDFDate
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.FileUtils.getNameWithoutExtension
import com.compdfkit.samples.util.OutputListener
import java.io.File

class DigitalSignaturesTest : PDFSamples() {

    init {
        setTitle(R.string.digital_signature_title)
        setDescription(R.string.digital_signature_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        printDividingLine()

        //---------
        //Samples 0: Create a pfx format certificate for digital signature use
        generateCertificate()

        //--------
        //Samples 1: Create form fields for digital signature and digital signature using created pfx format certificate
        createSignature()

        //--------
        //Samples 2: Verify signature
        verifySignatureInfo()

        //--------
        //Samples 3: Verify certificate
        verifyCertificate()

        //--------
        //Samples 4: Print digital signature info
        printDigitalSignatureInfo()

        //--------
        //Samples 5: Trust certificate
        trustCertificate()

        //--------
        //Samples 6:Remove digital signature from PDF document
        removeDigitalSignature()
        printFooter()
    }

    /**
     * **Create pfx format certificate for digital signature use** <br></br>
     * <br></br><br></br>
     * **Password:** ComPDFKit <br></br>
     * **C=SG:** This represents the country code "SG," which typically stands for Singapore.<br></br><br></br>
     * **O=ComPDFKit:** This is the Organization (O) field, indicating the name of the organization or entity,
     * in this case, "ComPDFKit." <br></br><br></br>
     * **D=R&D Department:** This is the Department (D) field,
     * indicating the specific department within the organization,
     * in this case, "R&D Department."<br></br><br></br>
     * **CN=Alan:** This is the Common Name (CN) field, which usually represents the name
     * of the individual or entity. In this case, it is "Alan."<br></br><br></br>
     * **emailAddress=xxxx@example.com:** Email is xxxx@example.com <br></br><br></br>
     * **CPDFSignature.CertUsage.PDFAll: **Used for both digital signing and data validation simultaneously.<br></br><br></br>
     * **is_2048: ** Enhanced security encryption.<br></br><br></br>
     */
    private fun generateCertificate() {
        outputListener?.println("generate certificate")
        val password = "ComPDFKit"
        val ownerInfo = CPDFOwnerInfo().apply {
            commonName = "Alan"
            orgnize = "ComPDFKit"
            email = "xxxx@example.com"
            country = "SG"
            orgnizeUnit = "R&D Department"
        }
        val flag = CertUsage.PDFAll
        val certFile = File(outputDir(), "certificate/Certificate.pfx")
        certFile.parentFile?.mkdirs()
        val result = CPDFSignature.generatePKCS12Cert(ownerInfo, password, certFile.absolutePath, flag)
        if (result) {
            outputListener?.println("Done. File saved in Certificate.pfx")
            outputListener?.println("Generate PKCS12 certificate done")
        }
        addFileList(certFile.absolutePath)
        printDividingLine()
    }

    private fun createSignature() {
        outputListener?.println("Create digital signature.")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        //Insert a Form Signature Widget (unsigned)
        val cpdfPage = document.pageAtIndex(0)
        val pageSize = cpdfPage.size
        val signatureWidget = cpdfPage.addFormWidget(CPDFWidget.WidgetType.Widget_SignatureFields) as CPDFSignatureWidget
        signatureWidget.fieldName = "Signature1"
        signatureWidget.fillColor = Color.parseColor("#FF96B4D2")
        signatureWidget.rect = kotlin.run {
            cpdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), RectF(28f, 420f, 150f, 370f))
        }
        signatureWidget.updateAp()


        // Make a digital signature
        val fileName = getNameWithoutExtension(document.fileName) + "_Signed.pdf"
        val file = File(outputDir(), "digitalSignature/$fileName")
        file.parentFile?.mkdirs()
        val password = "ComPDFKit"
        val certPath = getAssetsTempFile(context(), "Certificate.pfx")
        val cpdfx509 = CPDFSignature.getX509ByPKCS12Cert(certPath, password)
        val location = cpdfx509.certInfo.subject.country
        val reason = "I am the owner of the document."
        val bitmap = BitmapFactory.decodeFile(getAssetsTempFile(context(), "ComPDFKit.png"))
        // Describe signature appearance information
        val sigConfig = CPDFDigitalSigConfig().apply {
            text = cpdfx509.certInfo.subject.commonName
            textColor = Color.BLACK
            contentColor = Color.BLACK
            logo = bitmap
            val builder = StringBuilder()
            builder.append("Name: ").append(cpdfx509.certInfo.subject.commonName).append("\n")
                    .append("Date: ").append(DateUtil.getDataTime("yyyy.MM.dd HH:mm:ss")).append("\n")
                    .append("Reason: ").append(reason).append("\n")
                    .append("Location: ").append(location).append("\n")
                    .append("DN: ").append(cpdfx509.certInfo.subject)
            content = builder.toString()
            isContentAlginLeft = false
            isDrawLogo = true
        }

        val updateSignAp = signatureWidget.updateApWithDigitalSigConfig(sigConfig)
        if (updateSignAp) {
            val writeSignResult = document.writeSignature(signatureWidget,
                    file.absolutePath,
                    certPath,
                    password,
                    location,
                    reason,
                    CPDFDocument.PDFDocMdpP.PDFDocMdpPForbidAllModify
            )
            if (writeSignResult) {
                addFileList(file.absolutePath)
                outputListener?.println("Done. File saved in $fileName")
                outputListener?.println("Create digital signature done")
            }
        }
        printDividingLine()
    }

    private fun verifySignatureInfo() {
        outputListener?.println("Verify digital signature")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "Signed.pdf"))
        // Iterate through all digital signatures
        for (i in 0 until document.signatureCount) {
            val signature = document.getPdfSignature(i)
            // Check if the signer array exists and is not empty
            if (signature.signerArr != null && signature.signerArr.isNotEmpty()) {
                val signer = signature.signerArr[0]

                // Verify the validity of the signature
                val verifyValid = signature.verify(document)

                // Verify if the document has not been modified
                val unmodified = signature.verifyDocument(document)

                // Determine if the signature is valid and the document is unmodified
                val isSignVerified = verifyValid && unmodified

                // Check if the certificate is trusted
                val certChainTrusted = signer.cert.verifyGetChain(document.context, signature)
                val certificateIsTrusted = signer.cert.checkCertificateIsTrusted(document.context)
                val certIsTrusted = certChainTrusted || certificateIsTrusted

                // Check if the certificate has expired
                signer.cert.isExpired

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
                outputListener?.println("Is the certificate trusted: $certIsTrusted")
                outputListener?.println("Is the signature verified: $isSignVerified")
            }
        }
        outputListener?.println("Verify digital signature done.")
        printDividingLine()
    }

    private fun verifyCertificate() {
        outputListener?.println("verify certificate")
        // Verify whether the specified certificate file is trusted
        val certFilePath = getAssetsTempFile(context(), "Certificate.pfx")
        val password = "ComPDFKit"
        if (CPDFSignature.checkPKCS12Password(certFilePath, password)) {
            val x509 = CPDFSignature.getX509ByPKCS12Cert(certFilePath, password)
            x509.addToTrustedCertificates(context())
            val isTrusted = x509.checkCertificateIsTrusted(context())
            if (isTrusted) {
                outputListener?.println("Certificate is trusted")
            } else {
                outputListener?.println("Certificate is not trusted")
            }
            outputListener?.println("Verify certificate done.")
        }
        printDividingLine()
    }

    private fun printDigitalSignatureInfo() {
        outputListener?.println("Print digital signature info.")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "Signed.pdf"))
        for (i in 0 until document.signatureCount) {
            val signature = document.getPdfSignature(i)
            // Check if the signer array exists and is not empty
            if (signature.signerArr != null && signature.signerArr.isNotEmpty()) {
                val signer = signature.signerArr[0]
                val subject = signer.cert.certInfo.subject
                outputListener?.println("Name: ${signature.name}")
                outputListener?.println("Location: ${signature.location}")
                outputListener?.println("Reason: ${signature.reason}")
                outputListener?.println("Date: ${transformPDFDate(signature.date)}")
                outputListener?.println("Subject: $subject")
            }
        }
        outputListener?.println("Print digital signature info done.")
        printDividingLine()
    }

    private fun trustCertificate() {
        outputListener?.println("Trust certificate")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "Signed.pdf"))
        val signature = document.getPdfSignature(0)
        val signer = signature.signerArr[0]
        val cpdfx509 = signer.cert
        outputListener?.println("Certificate trusted status: ${cpdfx509.checkCertificateIsTrusted(context())}")
        outputListener?.println("---Begin trusted---")
        cpdfx509.addToTrustedCertificates(context())
        outputListener?.println("Certificate trusted status: ${cpdfx509.checkCertificateIsTrusted(context())}")
        outputListener?.println("Trust certificate done.")
        printDividingLine()
    }

    private fun removeDigitalSignature() {
        outputListener?.println("Remove digital signature")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "Signed.pdf"))
        for (i in 0 until document.signatureCount) {
            val signature = document.getPdfSignature(i)
            if (i == 0) {
                document.removeSignature(signature, true, null)
            }
        }
        val fileName = getNameWithoutExtension(document.fileName) + "_RemoveSign.pdf"
        val file = File(outputDir(), "digitalSignature/$fileName")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. File saved in $fileName")
        printDividingLine()
    }
}