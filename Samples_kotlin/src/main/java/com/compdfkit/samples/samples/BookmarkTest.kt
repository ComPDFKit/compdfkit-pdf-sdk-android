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

import com.compdfkit.core.common.CPDFDate
import com.compdfkit.core.document.CPDFBookmark
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.utils.TTimeUtil
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class BookmarkTest : PDFSamples() {
    init {
        setTitle(R.string.bookmark_test_title)
        setDescription(R.string.bookmark_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        outputListener?.println()
        // Open test pdf document
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        // Add a bookmark that jumps to the second page of the document
        document.addBookmark(CPDFBookmark(
                1, "my bookmark", CPDFDate.toStandardDate(TTimeUtil.getCurrentDate())
        ))
        val list = document.bookmarks
        if (list != null && list.size > 0) {
            for (cpdfBookmark in list) {
                outputListener?.println("Go to page " + (cpdfBookmark.pageIndex + 1))
            }
        }
        val file = File(outputDir(), "BookmarkTest/CreateBookmarkTest.pdf")
        outputListener?.println("Done.")
        outputListener?.println("Done. Result saved in CreateBookmarkTest.pdf")
        saveSamplePDF(document, file, true)
        printFooter()
    }
}