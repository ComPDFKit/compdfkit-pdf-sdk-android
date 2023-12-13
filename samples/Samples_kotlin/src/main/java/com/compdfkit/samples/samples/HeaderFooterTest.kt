/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import android.graphics.Color
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class HeaderFooterTest : PDFSamples() {
    init {
        setTitle(R.string.header_footer_test_title)
        setDescription(R.string.header_footer_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        addCommonHeaderFooter()
        addPageHeaderFooter()
        editHeaderFooter()
        clearHeaderFooterTest()
        printFooter()
    }

    /**
     * samples 1 : add header
     */
    private fun addCommonHeaderFooter() {
        printDividingLine()
        outputListener?.println("Samples 1 : Insert common header footer")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        val headerFooter = document.headerFooter
        val headerStr = "ComPDFKit"
        // index 0 : top left
        // index 1 : top middle
        // index 2 : top right
        val index = 3
        for (i in 0 until index) {
            outputListener!!.println("Text: $headerStr")
            headerFooter.setText(i, headerStr)
            headerFooter.setTextColor(i, Color.RED)
            headerFooter.setFontSize(i, 14F)
            when (i) {
                0 -> outputListener?.println("Location: Top Left")
                1 -> outputListener?.println("Location: Top Middle")
                else -> outputListener?.println("Location: Top Right")
            }
            outputListener?.println()
        }
        headerFooter.pages = "0,1,2,3,4"
        headerFooter.update()
        val file = File(outputDir(), "HeaderFooterTest/AddCommonHeaderFooter.pdf")
        saveSamplePDF(document, file, false)
        outputListener?.println("Done. Results saved in AddCommonHeaderFooter.pdf")
        printDividingLine()
    }

    private fun addPageHeaderFooter() {
        outputListener?.println("Samples 2 : Insert page header footer")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        val headerFooter = document.headerFooter
        val headerStr = "ComPDFKit"
        for (i in 0 until document.pageCount) {
            val pageNumber = i + 1
            val index = 6
            // index 0 : top left
            // index 1 : top middle
            // index 2 : top right
            // index 3 : bottom left
            // index 4 : bottom middle
            // index 5 : bottom right
            for (i1 in 0 until index) {
                if (i1 < 3) {
                    outputListener?.println("Text: $headerStr")
                    headerFooter.setText(i1, headerStr)
                    headerFooter.setTextColor(i1, Color.BLACK)
                } else {
                    outputListener?.println("Text: 0$pageNumber")
                    headerFooter.setText(i1, "0$pageNumber")
                    headerFooter.setTextColor(i1, Color.RED)
                }
                when (i1) {
                    0 -> outputListener?.println("Location: Top Left")
                    1 -> outputListener?.println("Location: Top Middle")
                    2 -> outputListener?.println("Location: Top Right")
                    3 -> outputListener?.println("Location: Bottom Left")
                    4 -> outputListener?.println("Location: Bottom Middle")
                    else -> outputListener?.println("Location: Bottom Right")
                }
                headerFooter.setFontSize(i, 14f)
            }
            headerFooter.pages = "" + i
            headerFooter.update()
        }
        val file = File(outputDir(), "HeaderFooterTest/AddPageHeaderFooter.pdf")
        saveSamplePDF(document, file, false)
        outputListener?.println("Done. Results saved in AddPageHeaderFooter.pdf")
        printDividingLine()
    }

    /**
     * samples 3 : edit top left header
     */
    private fun editHeaderFooter() {
        outputListener?.println("Samples 3 : Edit top left header")
        val document = CPDFDocument(context())
        val file = File(outputDir(), "HeaderFooterTest/AddCommonHeaderFooter.pdf")
        document.open(file.absolutePath)
        val headerFooter = document.headerFooter
        outputListener?.println("Get old head and footer 0 succeeded, text is ${headerFooter.getText(0)}")
        outputListener?.println("Change head and footer 0 succeeded, new text is ComPDFKit Samples")
        // change top left text
        headerFooter.setText(0, "ComPDFKit Samples")
        headerFooter.update()
        val resultsFile = File(outputDir(), "HeaderFooterTest/EditHeaderFooterTest.pdf")
        saveSamplePDF(document, resultsFile, false)
        outputListener?.println("Done. Results saved in EditHeaderFooterTest.pdf")
        printDividingLine()
    }

    private fun clearHeaderFooterTest() {
        outputListener?.println("Samples 4 : Clean all header and footer")
        val document = CPDFDocument(context())
        val file = File(outputDir(), "HeaderFooterTest/AddCommonHeaderFooter.pdf")
        document.open(file.absolutePath)
        document.headerFooter.clear()
        outputListener?.println("")
        val resultsFile = File(outputDir(), "HeaderFooterTest/ClearHeaderFooterTest.pdf")
        saveSamplePDF(document, resultsFile, false)
        outputListener?.println("Done. Results saved in ClearHeaderFooterTest.pdf")
        printDividingLine()
    }
}