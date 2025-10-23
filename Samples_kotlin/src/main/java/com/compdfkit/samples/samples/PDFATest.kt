/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
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

class PDFATest : PDFSamples() {
    init {
        setTitle(R.string.pdf_a_test_title)
        setDescription(R.string.pdf_a_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        convertToPDFA1a()
        convertToPDFA1b()
        printFooter()
    }

    /**
     * Samples 1 : convert pdf document to pdfA1a
     */
    private fun convertToPDFA1a() {
        printDividingLine()
        outputListener?.println("Samples 1 : convert to pdf A1a")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.convertType(CPDFDocument.PDFDocumentType.PDFTypeA1a)
        val file = File(outputDir(),
                "PDFATest/PDFA1aTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Convert to PDF/A-1a done")
        outputListener?.println("Done. Result saved in PDFA1aTest.pdf")
        printDividingLine()
    }

    /**
     * Samples 2 : convert pdf document to pdfA1b
     */
    private fun convertToPDFA1b() {
        outputListener?.println("Samples 1 : convert to pdf A1b")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.convertType(CPDFDocument.PDFDocumentType.PDFTypeA1b)
        val file = File(outputDir(), "PDFATest/PDFA1bTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Convert to PDF/A-1b done")
        outputListener?.println("Done. Result saved in PDFA1bTest.pdf")
        printDividingLine()
    }
}