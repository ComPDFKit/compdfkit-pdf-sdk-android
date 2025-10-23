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

import android.graphics.Color
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.compdfkit.core.annotation.CPDFAnnotation
import com.compdfkit.core.annotation.CPDFHighlightAnnotation
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.page.CPDFTextRange
import com.compdfkit.core.page.CPDFTextSearcher
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File

class TextSearchTest : PDFSamples() {
    private val handler = Handler(Looper.getMainLooper())

    init {
        setTitle(R.string.text_search_test_title)
        setDescription(R.string.text_search_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
            printHead()
            outputListener?.println()
            val document = CPDFDocument(context())
            document.open(getAssetsTempFile(context(), "text.pdf"))
            val keywords = "PDF"
            // Search for all relevant text in a document based on a keyword
            val list = startSearch(document, keywords)
            if (list.isNotEmpty()) {
                val textRange = list[0]
                val pdfPage = document.pageAtIndex(0)
                val pdfTextPage = pdfPage.textPage
                val textSelectionArr = pdfTextPage.getSelectionsByTextForRange(textRange)
                //Then, add a highlight annotation for the specific area.
                val annotRect = RectF()
                val highlightAnnotation = pdfPage.addAnnot(CPDFAnnotation.Type.HIGHLIGHT) as CPDFHighlightAnnotation
                highlightAnnotation.color = Color.YELLOW
                highlightAnnotation.alpha = 255 / 2
                val quadRects = arrayOfNulls<RectF>(textSelectionArr.size)
                val markedTextSb = StringBuilder()
                val len = textSelectionArr.size
                for (i in 0 until len) {
                    val textSelection = textSelectionArr[i] ?: continue
                    val rect = RectF(textSelection.rectF)
                    if (annotRect.isEmpty) {
                        annotRect.set(rect)
                    } else {
                        annotRect.union(rect)
                    }
                    quadRects[i] = RectF(textSelection.rectF)
                    val text = pdfTextPage.getText(textSelection.textRange)
                    if (!TextUtils.isEmpty(text)) {
                        markedTextSb.append(text)
                    }
                }
                highlightAnnotation.quadRects = quadRects
                highlightAnnotation.markedText = markedTextSb.toString()
                highlightAnnotation.rect = annotRect
                highlightAnnotation.updateAp()
            }
            outputListener?.println("the key `" + keywords + "` have " + list.size + " results")
            val file = File(outputDir(), "TextSearchTest/TextSearchResults.pdf")
            saveSamplePDF(document, file, false)
            outputListener?.println("Done. Results saved in TextSearchResults.pdf")
            printFooter()
    }

    companion object {
        fun startSearch(document: CPDFDocument, keywords: String?): List<CPDFTextRange> {
            val textSearcher = com.compdfkit.ui.textsearch.CPDFTextSearcher(document.context, document);
            textSearcher.setSearchConfig(keywords, CPDFTextSearcher.PDFSearchOptions.PDFSearchCaseInsensitive)
            val searchTextInfoList: MutableList<CPDFTextRange> = ArrayList()
            for (i in 0 until document.pageCount) {
                val page = document.pageAtIndex(i)
                val textPage = page.textPage
                if (null == textPage || !textPage.isValid) {
                    continue
                }
                val searchPageContent: List<CPDFTextRange> = textSearcher.searchKeyword(i)
                if (searchPageContent.isNotEmpty()) {
                    searchTextInfoList.addAll(searchPageContent)
                }
                page.close()
            }
            return searchTextInfoList
        }
    }
}