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
import com.compdfkit.core.page.CPDFPage
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class FlattenTest : PDFSamples() {
    init {
        setTitle(R.string.flatten_test_title)
        setDescription(R.string.flatten_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        outputListener?.println()
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "Annotations.pdf"))
        // Get the total number of comments in the pdf document
        var annotationCount = 0
        for (i in 0 until document.pageCount) {
            annotationCount += document.pageAtIndex(i).annotCount
        }
        outputListener?.println("$annotationCount annotations in the file.")
        // Flatten processing of pdf pages
        document.flattenAllPages(CPDFPage.PDFFlattenOption.FLAT_NORMALDISPLAY)
        // save document
        val file = File(outputDir(), "Flatten/FlattenTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in FlattenTest.pdf")
        val newFileDocument = CPDFDocument(context())
        newFileDocument.open(file.absolutePath)
        //Open the document again and get the number of annotations in the document
        annotationCount = 0
        for (i in 0 until newFileDocument.pageCount) {
            annotationCount += newFileDocument.pageAtIndex(i).annotCount
        }
        newFileDocument.close()
        outputListener?.println("$annotationCount annotations in the new file.")
        printDividingLine()
        printFooter()
    }
}