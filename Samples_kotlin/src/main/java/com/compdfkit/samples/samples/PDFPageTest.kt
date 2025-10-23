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

import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class PDFPageTest : PDFSamples() {
    init {
        setTitle(R.string.pdf_page_test_title)
        setDescription(R.string.pdf_page_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        insertBlankPage()
        insertPdfPage()
        splitPages()
        mergePages()
        deletePages()
        rotatePage()
        replacePages()
        extractPages()
        printFooter()
    }

    /**
     * Samples - 1: Insert a blank A4-sized page into the sample document
     */
    private fun insertBlankPage() {
        printDividingLine()
        outputListener?.println("Samples 1: Insert a blank A4-sized page into the sample document")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.insertBlankPage(1, 595F, 842F)
        outputListener?.println("Insert PageIndex : 1")
        outputListener?.println("Size : 595*842")
        val file = File(outputDir(), "pdfPage/Insert_Blank_Page.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in Insert_Blank_Page.pdf")
        printDividingLine()
    }

    /**
     * Samples - 2: Import pages from another document into the example document
     */
    private fun insertPdfPage() {
        outputListener?.println("Samples 2: Import pages from another document into the example document")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        outputListener?.println("Open the document to be imported")
        //Open the document to be imported
        val document2 = CPDFDocument(context())
        document2.open(getAssetsTempFile(context(), "text.pdf"))
        document.importPages(document2, intArrayOf(0), 1)
        val file = File(outputDir(), "pdfPage/Import_Document_Page.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in Import_Document_Page.pdf")
        printDividingLine()
    }

    /**
     * Samples - 3: Split a PDF document into multiple pages
     */
    private fun splitPages() {
        outputListener?.println("Samples 3: Split a PDF document into multiple pages")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        for (i in 0 until document.pageCount) {
            val newDocument = CPDFDocument.createDocument(context())
            newDocument.importPages(document, intArrayOf(i), 0)
            val file = File(outputDir(), "pdfPage/CommonFivePage_Split_Page_" + (i + 1) + ".pdf")
            outputListener?.println("Done. Result saved in \nCommonFivePage_Split_Page_" + (i + 1) + ".pdf")
            saveSamplePDF(newDocument, file, true)
        }
        document.close()
        outputListener?.println("Done!")
        printDividingLine()
    }

    /**
     * Samples - 4: Merge split documents
     */
    private fun mergePages() {
        outputListener?.println("Samples 4: Merge split documents")
        val pageNum = 5
        val document = CPDFDocument.createDocument(context())
        for (i in 0 until pageNum) {
            val file = File(outputDir(), "pdfPage/CommonFivePage_Split_Page_" + (i + 1) + ".pdf")
            if (file.exists()) {
                outputListener?.println("Opening " + file.name)
                val newDocument = CPDFDocument(context())
                newDocument.open(file.absolutePath)
                document.importPages(newDocument, intArrayOf(0), i)
            }
        }
        val file = File(outputDir(), "pdfPage/Merge_Pages.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in\nMerge_Pages.pdf")
        printDividingLine()
    }

    /**
     * Samples - 5: Delete the specified page of the document
     */
    private fun deletePages() {
        outputListener?.println("Samples 5: Delete the specified page of the document")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        val evenNumbers = getEvenNumbers(1, document.pageCount - 1)
        document.removePages(evenNumbers)
        val file = File(outputDir(), "pdfPage/Remove_Page.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in\nRemove_Page.pdf")
        printDividingLine()
    }

    /**
     * Samples - 6: Rotate document pages
     */
    private fun rotatePage() {
        outputListener?.println("Samples 6: Rotate document pages")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.pageAtIndex(0).rotation = 90
        val file = File(outputDir(), "pdfPage/Rotate_Pages.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in\nRotate_Pages.pdf")
        printDividingLine()
    }

    /**
     * Samples - 7: Replace specified pages of example documentation with other documentation specified pages
     */
    private fun replacePages() {
        outputListener?.println("Samples 7: Replace specified pages of example documentation with other documentation specified pages")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.removePages(intArrayOf(1))
        //open second pdf Document
        val document2 = CPDFDocument(context())
        document2.open(getAssetsTempFile(context(), "text.pdf"))
        document.importPages(document2, intArrayOf(0), 1)
        val file = File(outputDir(), "pdfPage/Replace_Pages.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in\nReplace_Pages.pdf")
        printDividingLine()
    }

    /**
     * Samples - 8: Extract specific pages of a document
     */
    private fun extractPages() {
        outputListener?.println("Samples 8: Extract specific pages of a document")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        val newDocument = CPDFDocument.createDocument(context())
        newDocument.importPages(document, intArrayOf(1), 0)
        val file = File(outputDir(), "pdfPage/ExtractPages.pdf")
        outputListener?.println("Done. Result saved in \nCommonFivePage_Extract_Page_1.pdf")
        saveSamplePDF(newDocument, file, true)
        document.close()
        printDividingLine()
    }

    companion object {
        fun getEvenNumbers(start: Int, end: Int): IntArray {
            val size = (end - start) / 2 + 1
            val evenNumbers = IntArray(size)
            var index = 0
            for (i in start..end) {
                if (i % 2 != 0) {
                    evenNumbers[index] = i
                    index++
                }
            }
            return evenNumbers
        }
    }
}