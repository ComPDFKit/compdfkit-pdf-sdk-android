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

import android.graphics.Bitmap
import android.graphics.Color
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.FileUtils.getNameWithoutExtension
import com.compdfkit.samples.util.OutputListener
import com.compdfkit.ui.utils.CPDFBitmapUtils
import java.io.File

class PDFToImageTest : PDFSamples() {
    init {
        setTitle(R.string.pdf_to_image_test_title)
        setDescription(R.string.pdf_to_image_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        outputListener?.println()
        val document = CPDFDocument(context())
        val assetsName = "CommonFivePage.pdf"
        document.open(getAssetsTempFile(context(), assetsName))
        // Render each page of the document as an image
        for (i in 0 until document.pageCount) {
            // page size
            val size = document.getPageSize(i)
            val bitmap = Bitmap.createBitmap(size.width().toInt(), size.height().toInt(), Bitmap.Config.RGB_565)
            val success = document.renderPageAtIndex(
                    bitmap,
                    i, size.width().toInt(), size.height().toInt(),
                    0,
                    0, size.width().toInt(), size.height().toInt(),
                    Color.WHITE,
                    255,
                    0,
                    true,
                    true
            )
            if (success) {
                val file = File(outputDir(), "PDFToImageTest/" + getNameWithoutExtension(assetsName) + "/PDFToImageTest" + i + ".png")
                file.parentFile?.mkdirs()
                // save bitmap
                CPDFBitmapUtils.saveBitmap(bitmap, file.absolutePath)
                outputListener?.println("Done.")
                if (file.exists()) {
                    outputListener?.println("Done. Results saved in " + file.name)
                    addFileList(file.absolutePath)
                }
            }
        }
        document.close()
        printFooter()
    }
}