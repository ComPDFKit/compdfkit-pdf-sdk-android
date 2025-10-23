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

import android.graphics.BitmapFactory
import android.graphics.Color
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.watermark.CPDFWatermark
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class WatermarkTest : PDFSamples() {
    init {
        setTitle(R.string.watermark_test_title)
        setDescription(R.string.watermark_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        addTextWatermark()
        addImageWatermark()
        addTilesWatermark()
        deleteWatermark()
        printFooter()
    }

    /**
     * Samples 1 : Insert a text watermark in the center of all pages of the document
     */
    private fun addTextWatermark() {
        printDividingLine()
        outputListener?.println("Sample 1 : Insert Text Watermark")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_TEXT).apply {
            text = "ComPDFKit"
            textRGBColor = Color.RED
            fontSize = 30F
            opacity = 0.5F
            rotation = 45F
            vertalign = CPDFWatermark.Vertalign.WATERMARK_VERTALIGN_CENTER
            horizalign = CPDFWatermark.Horizalign.WATERMARK_HORIZALIGN_CENTER
            vertOffset = 0F
            horizOffset = 0F
            pages = "0,1,2,3,4"
            printWatermarkInfo(this)
            update()
        }.release()
        val file = File(outputDir(), "watermarkTest/AddTextWatermarkTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in AddTextWatermarkTest.pdf")
        printDividingLine()
    }

    /**
     * Samples 2 : Insert a picture watermark in the center of all pages of the document
     */
    private fun addImageWatermark() {
        outputListener?.println("Sample 2 : Insert Image Watermark")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_IMG).apply {
            val watermarkImage = BitmapFactory.decodeResource(context().resources, R.mipmap.ic_launcher_foreground)
            setImage(watermarkImage, 100, 100)
            opacity = 1F
            rotation = 20F
            vertalign = CPDFWatermark.Vertalign.WATERMARK_VERTALIGN_CENTER
            horizalign = CPDFWatermark.Horizalign.WATERMARK_HORIZALIGN_CENTER
            vertOffset = 0F
            horizOffset = 0F
            pages = "0,1,2,3,4"
            printWatermarkInfo(this)
            update()
        }.release()
        val file = File(outputDir(), "watermarkTest/AddImageWatermarkTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in AddImageWatermarkTest.pdf")
        printDividingLine()
    }

    /**
     * Samples 3 : Insert a tiled text watermark on all pages of the document
     */
    private fun addTilesWatermark() {
        outputListener?.println("Sample 3 : Insert Text Tiles Watermark")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_TEXT).apply {
            text = "ComPDFKit"
            textRGBColor = Color.RED
            fontSize = 30F
            opacity = 0.5F
            rotation = 45F
            vertalign = CPDFWatermark.Vertalign.WATERMARK_VERTALIGN_CENTER
            horizalign = CPDFWatermark.Horizalign.WATERMARK_HORIZALIGN_CENTER
            vertOffset = 0F
            horizOffset = 0F
            pages = "0,1,2,3,4"
            isFullScreen = true
            horizontalSpacing = 100F
            verticalSpacing = 100F
            printWatermarkInfo(this)
            update()
        }.release()
        val file = File(outputDir(), "watermarkTest/AddTilesWatermarkTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in AddTilesWatermarkTest.pdf")
        printDividingLine()
    }

    private fun deleteWatermark() {
        outputListener?.println("Sample 4 : Delete Watermark")
        val document = CPDFDocument(context())
        val sampleFile = File(outputDir(), "watermarkTest/AddTextWatermarkTest.pdf")
        document.open(sampleFile.absolutePath)
        document.getWatermark(0)?.clear()
        val file = File(outputDir(), "watermarkTest/DeleteWatermarkTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done. Result saved in DeleteWatermarkTest.pdf")
        printDividingLine()
    }

    private fun printWatermarkInfo(watermark: CPDFWatermark) {
        if (watermark.type == CPDFWatermark.Type.WATERMARK_TYPE_TEXT) {
            outputListener?.println("Text : " + watermark.text)
            outputListener?.println(String.format("Color : red:%d, green:%d, blue:%d, alpha:%d",
                    Color.red(watermark.textRGBColor),
                    Color.green(watermark.textRGBColor),
                    Color.blue(watermark.textRGBColor),
                    Color.alpha(watermark.textRGBColor)))
            outputListener?.println("FontSize : ${watermark.fontSize}")
        }
        outputListener?.println("Opacity : ${watermark.opacity}")
        outputListener?.println("Rotation : ${watermark.rotation}")
        outputListener?.println("Vertalign : ${watermark.vertalign.name}")
        outputListener?.println("Horizalign : ${watermark.horizalign.name}")
        outputListener?.println("VertOffset : ${watermark.vertOffset}")
        outputListener?.println("HorizOffset : ${watermark.horizOffset}")
        outputListener?.println("Pages : ${watermark.pages}")
        outputListener?.println("VerticalSpacing : ${watermark.verticalSpacing}")
        outputListener?.println("HorizontalSpacing : ${watermark.horizontalSpacing}")
    }
}