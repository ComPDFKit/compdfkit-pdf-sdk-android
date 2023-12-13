/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples.samples

import android.graphics.BitmapFactory
import android.graphics.Color
import com.compdfkit.core.annotation.CPDFImageScaleType
import com.compdfkit.core.document.CPDFBackground
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class BackgroundTest : PDFSamples() {
    init {
        setTitle(R.string.background_test_title)
        setDescription(R.string.background_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        addColorBackground()
        addImageBackground()
        deleteBackground()
        printFooter()
    }

    private fun addColorBackground() {
        outputListener?.println()
        outputListener?.println("Samples 1 : Set the document background color")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.background.apply {
            type = CPDFBackground.Type.Color
            color = Color.RED
            opacity = 1f
            pages = "0,1,2,3,4"
            printBackgroundInfo(this)
            update()
            release()
        }
        val file = File(outputDir(), "backgroundTest/AddColorBackgroundTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in AddColorBackgroundTest.pdf")
        printDividingLine()
    }

    private fun addImageBackground() {
        outputListener!!.println("Samples 2 : Set the document background image")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.background.apply {
            type = CPDFBackground.Type.Image
            val backgroundImage = BitmapFactory.decodeResource(context().resources, R.mipmap.ic_launcher_foreground)
            setImage(backgroundImage, CPDFImageScaleType.SCALETYPE_center)
            opacity = 1F
            pages = "0,1,2,3,4"
            printBackgroundInfo(this)
            update()
            release()
        }
        val file = File(outputDir(), "backgroundTest/AddImageBackgroundTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in AddImageBackgroundTest.pdf")
        printDividingLine()
    }

    /**
     * Sample 3 : Delete document background
     */
    private fun deleteBackground() {
        outputListener?.println("Samples 3 : Delete document background")
        val document = CPDFDocument(context())
        val sampleFile = File(outputDir(),
                "backgroundTest/AddColorBackgroundTest.pdf")
        document.open(sampleFile.absolutePath)
        //remove all pages background
        document.background.clear()
        //update pages
        //background.setPages("0,2,3,4");
        //background.update();
        //background.release();
        val file = File(outputDir(), "backgroundTest/DeleteBackgroundTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in DeleteBackgroundTest.pdf")
    }

    private fun printBackgroundInfo(background: CPDFBackground) {
        outputListener?.println("Type : ${background.type.name}")
        if (background.type == CPDFBackground.Type.Color) {
            outputListener?.println(String.format("Color : red:%d, green:%d, blue:%d, alpha:%d",
                    Color.red(background.color),
                    Color.green(background.color),
                    Color.blue(background.color),
                    Color.alpha(background.color)))
        }
        outputListener?.println("Opacity : ${background.opacity}")
        outputListener?.println("Rotation : ${background.rotation}")
        outputListener?.println("Vertalign : ${background.vertalign.name}")
        outputListener?.println("Horizalign : ${background.horizalign.name}")
        outputListener?.println("VertOffset : ${background.xOffset}")
        outputListener?.println("HorizOffset : ${background.yOffset}")
        outputListener?.println("Pages : ${background.pages}")
    }
}