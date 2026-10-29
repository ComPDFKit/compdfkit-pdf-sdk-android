/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

/**
 * This sample shows how to convert a common PDF document to the PDF standards
 * supported by ComPDFKit : PDF/A, PDF/X, PDF/E, PDF/VT and PDF/UA.
 */
class PDFATest : PDFSamples() {

    private data class ConvertItem(
        val type: CPDFDocument.PDFDocumentType,
        val label: String,
        val fileName: String
    )

    init {
        setTitle(R.string.pdf_a_test_title)
        setDescription(R.string.pdf_a_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        convertItems.forEachIndexed { index, item -> convertTo(index + 1, item) }
        printFooter()
    }

    /**
     * Convert the sample document to the given PDF standard and save it as a new file.
     * Not every standard accepts every source document, so the result of
     * [CPDFDocument.convertType] is checked before saving.
     */
    private fun convertTo(index: Int, item: ConvertItem) {
        printDividingLine()
        outputListener?.println("Samples $index : convert to ${item.label}")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        if (!document.convertType(item.type)) {
            outputListener?.println("Convert to ${item.label} failed")
            document.close()
            return
        }
        val file = File(outputDir(), "PDFATest/${item.fileName}")
        saveSamplePDF(document, file, true)
        outputListener?.println("Convert to ${item.label} done")
        outputListener?.println("Done. Result saved in ${item.fileName}")

        val savedDocument = CPDFDocument(context())
        val openResult = savedDocument.open(file.absolutePath)
        if (openResult != CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
            outputListener?.println("Failed to reopen the saved file: $openResult")
            savedDocument.close()
            return
        }
        val savedType = savedDocument.getType()
        outputListener?.println("Saved document type: ${savedType.name}")
        outputListener?.println("Matches selected type: ${savedType == item.type}")
        savedDocument.close()
    }

    companion object {

        /**
         * All the document types [CPDFDocument.convertType] can convert to.
         * [CPDFDocument.PDFDocumentType.PDFType] is the plain PDF type and is not a conversion
         * target, so it is not listed here.
         */
        private val convertItems = listOf(
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA1a, "PDF/A-1a", "PDFA1aTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA1b, "PDF/A-1b", "PDFA1bTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA2a, "PDF/A-2a", "PDFA2aTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA2u, "PDF/A-2u", "PDFA2uTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA2b, "PDF/A-2b", "PDFA2bTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA3a, "PDF/A-3a", "PDFA3aTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA3u, "PDF/A-3u", "PDFA3uTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA3b, "PDF/A-3b", "PDFA3bTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeX1a, "PDF/X-1a", "PDFX1aTest.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeX3, "PDF/X-3", "PDFX3Test.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeX4, "PDF/X-4", "PDFX4Test.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeE1, "PDF/E-1", "PDFE1Test.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeE2, "PDF/E-2", "PDFE2Test.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeVT1, "PDF/VT-1", "PDFVT1Test.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeVT2, "PDF/VT-2", "PDFVT2Test.pdf"),
            ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeUA1, "PDF/UA-1", "PDFUA1Test.pdf"),
        )
    }
}
