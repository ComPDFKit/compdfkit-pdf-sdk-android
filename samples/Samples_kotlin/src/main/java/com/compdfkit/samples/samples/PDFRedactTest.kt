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
import android.graphics.RectF
import com.compdfkit.core.annotation.CPDFAnnotation
import com.compdfkit.core.annotation.CPDFRedactAnnotation
import com.compdfkit.core.annotation.CPDFTextAttribute
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class PDFRedactTest : PDFSamples() {
    init {
        setTitle(R.string.redact_test_title)
        setDescription(R.string.redact_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        outputListener?.println()
        outputListener?.println("The text need to be redact is : Page1")
        outputListener?.println("Text in the redacted area is :")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        val pdfPage = document.pageAtIndex(0)
        val redactAnnotation = pdfPage.addAnnot(CPDFAnnotation.Type.REDACT) as CPDFRedactAnnotation
        val pageSize = pdfPage.size
        redactAnnotation.rect = kotlin.run {
            val insertRect = RectF(300F, 240F, 400F, 320F)
            //coordinate conversion
            pdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect)
        }
        val textAttribute = CPDFTextAttribute(
                CPDFTextAttribute.FontNameHelper.obtainFontName(CPDFTextAttribute.FontNameHelper.FontType.Helvetica, false, false), 14f, Color.YELLOW)
        redactAnnotation.textDa = textAttribute
        redactAnnotation.fillColor = Color.RED
        redactAnnotation.overLayText = "REDACTED"
        redactAnnotation.updateAp()
        redactAnnotation.applyRedaction()
        val resultsFile = File(outputDir(), "RedactTest/RedactTest.pdf")
        saveSamplePDF(document, resultsFile, false)
        outputListener?.println("Done. Results saved in RedactTest.pdf")
        printDividingLine()
        printFooter()
    }
}