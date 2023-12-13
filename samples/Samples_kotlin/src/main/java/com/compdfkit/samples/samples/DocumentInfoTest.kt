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
import com.compdfkit.samples.util.DateUtil.transformPDFDate
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener

class DocumentInfoTest : PDFSamples() {
    init {
        setTitle(R.string.document_test_title)
        setDescription(R.string.document_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        outputListener?.println()
        // Obtain basic information of pdf documents
        val cpdfInfo = document.info
        outputListener?.println("FileName : ${document.fileName}")
        outputListener?.println("FileSize : ${getDocumentSize(document)}")
        outputListener?.println("Title : ${cpdfInfo.title}")
        outputListener?.println("Author : ${cpdfInfo.author}")
        outputListener?.println("Subject : ${cpdfInfo.subject}")
        outputListener?.println("Keywords : ${cpdfInfo.keywords}")
        outputListener?.println("Version : ${document.majorVersion}")
        outputListener?.println("PageCount : ${document.pageCount}")
        outputListener?.println("Creator : ${cpdfInfo.creator}")
        outputListener?.println("CreationDate : ${transformPDFDate(cpdfInfo.creationDate)}")
        outputListener?.println("ModificationDate : ${transformPDFDate(cpdfInfo.modificationDate)}")

        // Get pdf document permission information
        val permissionInfo = document.permissionsInfo
        outputListener?.println("Printing : ${permissionInfo.isAllowsPrinting}")
        outputListener?.println("Content Copying : ${permissionInfo.isAllowsCopying}")
        outputListener?.println("Document Change : ${permissionInfo.isAllowsDocumentChanges}")
        outputListener?.println("Document Assembly : ${permissionInfo.isAllowsDocumentAssembly}")
        outputListener?.println("Document Commenting : ${permissionInfo.isAllowsCommenting}")
        outputListener?.println("Document Filling of form field : ${permissionInfo.isAllowsFormFieldEntry}")
        document.close()
        printFooter()
    }

    private fun getDocumentSize(document: CPDFDocument): String {
        val MB = (1024 * 1024).toLong()
        val KB = 1024
        var fileSize = 0L
        try {
            val p = document.context.contentResolver.openFileDescriptor(document.uri, "r")
            fileSize = p?.statSize ?:0L
            p?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val size: Float
        var unit = " M"
        if (fileSize > MB) {
            size = fileSize.toFloat() / (1024 * 1024)
        } else if (fileSize > KB) {
            size = fileSize.toFloat() / 1024
            unit = " KB"
        } else {
            size = fileSize.toFloat()
            unit = " B"
        }
        return String.format("%.2f", size) + unit
    }
}