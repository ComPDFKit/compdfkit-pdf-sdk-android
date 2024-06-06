/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import com.compdfkit.core.document.CPDFDestination
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.document.CPDFOutline
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class OutlineTest : PDFSamples() {
    init {
        setTitle(R.string.outline_test_title)
        setDescription(R.string.outline_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        createOutline()
        printOutline()
        printFooter()
    }

    /**
     * Sample 1 : create outline test
     */
    private fun createOutline() {
        outputListener?.println()
        // Open test pdf document
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        val rootOutline: CPDFOutline = if (document.outlineRoot == null) {
            document.newOutlineRoot()
        } else {
            document.outlineRoot
        }
        // add outline data
        val child = insertOutline(rootOutline, "1. page1", 0)
        // Add sub-outline data for first page outline
        insertOutline(child, "1.1 page1_1", 0)
        insertOutline(rootOutline, "2. page2", 1)
        insertOutline(rootOutline, "3. page3", 2)
        insertOutline(rootOutline, "4. page4", 3)
        insertOutline(rootOutline, "5. page5", 4)
        val file = File(outputDir(), "OutlineTest/CreateOutlineTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done.")
        outputListener?.println("Done. Results saved in CreateOutlineTest.pdf")
    }

    /**
     * insert outline data
     * @param rootOutline parent Outline
     * @param title outline title
     * @param pageIndex jump document page index
     * @return CPDFOutline
     */
    private fun insertOutline(rootOutline: CPDFOutline, title: String, pageIndex: Int): CPDFOutline {
        return rootOutline.insertChildAtIndex(pageIndex).apply {
            this.title = title;
            destination = CPDFDestination(pageIndex, 0F, 0F, 1F)
        }
    }

    private fun printOutline() {
        outputListener?.println()
        val document = CPDFDocument(context())
        val sampleFile = File(outputDir(), "OutlineTest/CreateOutlineTest.pdf")
        document.open(sampleFile.absolutePath)
        val outlineRoot = document.outlineRoot
        if (outlineRoot != null) {
            printChildOutline(outlineRoot.childList)
        }
    }

    private fun printChildOutline(outlines: Array<CPDFOutline>) {
        for (outline in outlines) {
            val tab = StringBuilder()
            if (outline.level > 1) {
                for (i in 0 until outline.level) {
                    tab.append("\t")
                }
            }
            outputListener?.println(tab.toString() + "->" + outline.title)
            if (outline.childList != null && outline.childList.size > 0) {
                printChildOutline(outline.childList)
            }
        }
    }
}