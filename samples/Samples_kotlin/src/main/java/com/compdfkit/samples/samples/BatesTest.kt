/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
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

class BatesTest : PDFSamples() {
    init {
        setTitle(R.string.bates_test_title)
        setDescription(R.string.bates_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        addCommonBates()
        editDateBates()
        clearBates()
        printFooter()
    }

    /**
     * Samples 1 : Add bates
     */
    private fun addCommonBates() {
        printDividingLine()
        outputListener?.println("Samples 1 : Add Bates")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        val bates = document.bates
        val num = 6
        for (i in 0 until num) {
            outputListener?.println("\nText : <<#3#5#Prefix-#-Suffix>>")
            bates.setText(i, "<<#3#1#ab#cd>>")
            bates.setTextColor(i, Color.RED)
            bates.setFontSize(i, 14F)
            when (i) {
                0 -> outputListener?.println("Location: Top Left")
                1 -> outputListener?.println("Location: Top Middle")
                2 -> outputListener?.println("Location: Top Right")
                3 -> outputListener?.println("Location: Botton Left")
                4 -> outputListener?.println("Location: Botton Middle")
                else -> outputListener?.println("Location: Botton Right")
            }
        }
        bates.pages = "0-${document.pageCount - 1}"
        bates.update()
        val file = File(outputDir(), "BatesTest/AddBatesTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Results saved in 1 AddBatesTest.pdf")
        printDividingLine()
    }

    /**
     * Samples 2: edit bates
     */
    private fun editDateBates() {
        outputListener?.println("Samples 2 : edit bates")
        val document = CPDFDocument(context())
        val file = File(outputDir(), "BatesTest/AddBatesTest.pdf")
        document.open(file.absolutePath)
        val bates = document.bates
        outputListener?.println("Get old bates 0 succeeded, text is ${bates.getText(0)}")
        outputListener?.println("Change bates 0 succeeded, new text is <<#3#1#ComPDFKit-#-ComPDFKit>>")
        bates.setText(0, "<<#3#1#ComPDFKit-#-ComPDFKit>>")
        bates.update()
        val resultsFile = File(outputDir(), "BatesTest/EditBatesTest.pdf")
        saveSamplePDF(document, resultsFile, false)
        outputListener?.println("Done. Results saved in EditBatesTest.pdf")
        printDividingLine()
    }

    /**
     * samples 3 : clear bates
     */
    private fun clearBates() {
        outputListener?.println("Samples 3 : clear bates")
        val document = CPDFDocument(context())
        val file = File(outputDir(), "BatesTest/AddBatesTest.pdf")
        document.open(file.absolutePath)
        val bates = document.bates.clear()
        val resultsFile = File(outputDir(), "BatesTest/ClearBatesTest.pdf")
        saveSamplePDF(document, resultsFile, false)
        outputListener?.println("Done. Results saved in ClearBatesTest.pdf")
        printDividingLine()
    }
}