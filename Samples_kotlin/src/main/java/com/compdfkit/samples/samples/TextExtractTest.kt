/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import android.graphics.RectF
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.page.CPDFTextRange
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener

class TextExtractTest : PDFSamples() {
    init {
        setTitle(R.string.text_extract_test_title)
        setDescription(R.string.text_extract_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        extractPageText()
        extractAllPageText()
        extractRectRangeText()
        printFooter()
    }

    /**
     * Samples 1: Extract all text content in the specified page
     */
    private fun extractPageText() {
        outputListener?.println("Samples 1: Extract all text content in the specified page")
        outputListener?.println("Opening the Samples PDF File")
        outputListener?.println("The text content of the first page of the document:")
        printDividingLine()
        outputListener?.println("Text : ")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "text.pdf"))
        val page = document.pageAtIndex(0)
        val textPage = page.textPage
        val pageText = textPage.getText(CPDFTextRange(0, textPage.countChars - 1))
        outputListener?.print(pageText)
        outputListener?.println("\nDone!")
        printDividingLine()
    }

    /**
     * Samples 2: Extract all text content of the document
     */
    private fun extractAllPageText() {
        outputListener?.println("Samples 2: Extract all text content of the document")
        outputListener?.println("Opening the Samples PDF File")
        printDividingLine()
        outputListener?.println("Text : ")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "text.pdf"))
        for (i in 0 until document.pageCount) {
            val page = document.pageAtIndex(i)
            val textPage = page.textPage
            val pageText = textPage.getText(CPDFTextRange(0, textPage.countChars - 1))
            outputListener?.print(pageText)
        }
        outputListener?.println("Done!")
        printDividingLine()
    }

    private fun extractRectRangeText() {
        outputListener?.println("Samples 3: Extract Select Rect Range Text")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "text.pdf"))
        val page = document.pageAtIndex(0)
        val textPage = page.textPage
        // Extract the text within the range of （0, 0, 500, 500) on the first page
        var selectRect: RectF? = RectF(0f, 0f, 300f, 300f)
        val size = page.size
        selectRect = page.convertRectFromPage(false, size.width(), size.height(), selectRect)
        val selections = textPage.getSelectionsByLineForRect(selectRect)
        outputListener?.println("Range : (0, 0, 300, 300)")
        outputListener?.println("Text : ")
        for (i in selections.indices) {
            val textSelection = selections[i] ?: continue
            val text = textPage.getText(textSelection.textRange)
            outputListener?.print(text)
        }
        outputListener?.println("\nDone!")
        printDividingLine()
    }
}