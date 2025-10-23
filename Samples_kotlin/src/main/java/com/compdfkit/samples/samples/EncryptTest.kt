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

import com.compdfkit.core.common.CPDFDocumentException
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.document.CPDFDocument.PDFDocumentEncryptAlgo
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class EncryptTest : PDFSamples() {
    init {
        setTitle(R.string.encrypt_test_title)
        setDescription(R.string.encrypt_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        printDividingLine()
        outputListener?.println("Samples 1 : RC4 encrypt")
        encryptDocument(PDFDocumentEncryptAlgo.PDFDocumentRC4, "EncryptUseRC4Test.pdf")
        outputListener?.println("Samples 2 : AES128 encrypt")
        encryptDocument(PDFDocumentEncryptAlgo.PDFDocumentAES128, "EncryptUseAES128Test.pdf")
        outputListener?.println("Samples 3 : AES236 encrypt")
        encryptDocument(PDFDocumentEncryptAlgo.PDFDocumentAES128, "EncryptUseAES256Test.pdf")
        outputListener?.println("Samples 4 : NoEncryptAlgo encrypt")
        encryptDocument(PDFDocumentEncryptAlgo.PDFDocumentAES128, "EncryptUseNoEncryptAlgoTest.pdf")
        outputListener?.println("Encrypt by user password done.")
        encryptOwnerPassword()
        encryptAllPassword()
        decryptDocument()
        printFooter()
    }

    /**
     * Encrypt document
     */
    private fun encryptDocument(documentEncryptAlgo: PDFDocumentEncryptAlgo, fileName: String) {
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.setUserPassword("User")
        document.encryptAlgorithm = documentEncryptAlgo
        val resultsFile = File(outputDir(), "EncryptTest/$fileName")
        saveSamplePDF(document, resultsFile, true)
        outputListener?.println("User password is : User")
        outputListener?.println("Done. Results saved in $fileName")
        outputListener?.println()
    }

    private fun encryptOwnerPassword() {
        printDividingLine()
        outputListener?.println("Samples 5 : Encrypt by owner passwords")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.encryptAlgorithm = PDFDocumentEncryptAlgo.PDFDocumentAES256
        document.setOwnerPassword("owner")
        val resultsFile = File(outputDir(), "EncryptTest/EncryptByOwnerPasswordsTest.pdf")
        saveSamplePDF(document, resultsFile, true)
        outputListener?.println("Owner password is : owner")
        outputListener?.println("Done. Results saved in EncryptByOwnerPasswordsTest.pdf")
        outputListener?.println()
    }

    private fun encryptAllPassword() {
        printDividingLine()
        outputListener?.println("Samples 6 : Encrypt by Both user and owner passwords")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.setUserPassword("User")
        document.encryptAlgorithm = PDFDocumentEncryptAlgo.PDFDocumentAES256
        document.setOwnerPassword("owner")
        val resultsFile = File(outputDir(), "EncryptTest/EncryptByAllPasswordsTest.pdf")
        saveSamplePDF(document, resultsFile, true)
        outputListener?.println("User password is : User")
        outputListener?.println("Owner password is : owner")
        outputListener?.println("Done. Results saved in EncryptByAllPasswordsTest.pdf")
        outputListener?.println()
    }

    private fun decryptDocument() {
        outputListener?.println("Samples 7 : encrypt document")
        val document = CPDFDocument(context())
        val file = File(outputDir(), "EncryptTest/EncryptByAllPasswordsTest.pdf")
        outputListener?.println("Unlock with user password")
        document.open(file.absolutePath, "User")
        outputListener?.println("Document is ${if (document.isEncrypted) "locked" else "unlocked"}")
        val info = document.permissionsInfo
        outputListener?.println("AllowsPrinting : ${info.isAllowsPrinting}")
        outputListener?.println("AllowsCopy : ${info.isAllowsCopying}")
        document.close()
        val newDocument = CPDFDocument(context())
        outputListener?.println("Unlock with owner password")
        newDocument.open(file.absolutePath, "owner")
        val info1 = newDocument.permissionsInfo
        outputListener?.println("AllowsPrinting : ${info1.isAllowsPrinting}")
        outputListener?.println("AllowsCopy : ${info1.isAllowsCopying}")
        outputListener?.println("Unlock done.")
        printDividingLine()
        try {
            val decryptTestFile = File(outputDir(), "EncryptTest/DecryptTest.pdf")
            decryptTestFile.parentFile?.mkdirs()
            newDocument.saveAs(decryptTestFile.absolutePath, true)
            if (decryptTestFile.exists()) {
                getOutputFileList().add(decryptTestFile.absolutePath)
            }
            newDocument.close()
            outputListener?.println("Document decrypt done.")
            outputListener?.println("Decrypt done.")
        } catch (_: CPDFDocumentException) {
        }
    }

    override fun saveSamplePDF(document: CPDFDocument, file: File, close: Boolean) {
        try {
            file.parentFile?.mkdirs()
            document.saveAs(file.absolutePath, false)
            if (file.exists()) {
                getOutputFileList().add(file.absolutePath)
            }
            if (close) {
                document.close()
            }
        } catch (e: CPDFDocumentException) {
            e.printStackTrace()
        }
    }
}