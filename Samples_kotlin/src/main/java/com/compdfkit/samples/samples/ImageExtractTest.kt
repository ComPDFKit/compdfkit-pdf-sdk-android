/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
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

class ImageExtractTest : PDFSamples() {
    init {
        setTitle(R.string.image_extract_test_title)
        setDescription(R.string.image_extract_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        printDividingLine()
        outputListener?.println("Samples 1: Extract all images in the document")
        outputListener?.println("Opening the Samples PDF File")
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "ImageExtractTest.pdf"))

        val extractDir = File(outputDir(), "imageExtract/ImageExtractTest")
        extractDir.delete()
        extractDir.mkdirs()
        document.extractImage(extractDir.absolutePath) { pageIndex, imageIndex, _ ->
            //Customize the returned file name
            return@extractImage document.fileName + "_" + pageIndex + "_" + imageIndex
        }
        val images = extractDir.listFiles()
        if (images != null && images.isNotEmpty()) {
            for (image in images) {
                getOutputFileList().add(image.absolutePath)
                outputListener?.println(image.name)
            }
        }
        document.close()
        printFooter()
    }
}