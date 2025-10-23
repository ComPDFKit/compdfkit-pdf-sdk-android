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
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.SampleApplication
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class AnnotationImportExportTest : PDFSamples() {

    init {
        setTitle(R.string.annotation_import_export_test_title)
        setDescription(R.string.annotation_import_export_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        exportAnnotation()
        importAnnotation()
        printFooter()
    }

    /**
     * Samples 1 : export pdf document annotations
     */
    private fun exportAnnotation() {
        printDividingLine()
        // Open the pdf document containing comments that needs to be exported
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "Annotations.pdf"))
        // Set annotations export path, cache directory
        val exportFile = File(outputDir(), "AnnotationImportExportTest/ExportAnnotationTest.xfdf")
        val cacheDir = File(context().cacheDir, "AnnotationImportExportTest/")
        // To create a directory, please ensure that the path exists.
        exportFile.parentFile?.mkdirs()
        cacheDir.mkdirs()
        document.exportAnnotations(exportFile.absolutePath, cacheDir.absolutePath)
        document.close()
        if (exportFile.exists()) {
            getOutputFileList().add(exportFile.absolutePath)
        }
        outputListener?.println("Done.")
        outputListener?.println("Done. Results saved in ExportAnnotationTest.xfdf")
    }

    /**
     * Samples 2 : Import a previously exported comment file into a blank document
     */
    private fun importAnnotation() {
        // Open the pdf document containing comments that needs to be exported
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        // Get imported annotation files
        val importFile = File(outputDir(), "AnnotationImportExportTest/ExportAnnotationTest.xfdf")
        val cacheDir = File(SampleApplication.instance.cacheDir, "AnnotationImportExportTest/")
        cacheDir.mkdirs()
        document.importAnnotations(importFile.absolutePath, cacheDir.absolutePath)
        val file = File(outputDir(), "AnnotationImportExportTest/ImportAnnotationTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done.")
        outputListener?.println("Done. Results saved in ImportAnnotationTest.pdf")
    }
}