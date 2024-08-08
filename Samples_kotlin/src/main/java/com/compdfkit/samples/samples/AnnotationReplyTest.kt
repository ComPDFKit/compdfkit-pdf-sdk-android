package com.compdfkit.samples.samples

import android.graphics.Color
import android.graphics.RectF
import com.compdfkit.core.annotation.CPDFAnnotation
import com.compdfkit.core.annotation.CPDFFreetextAnnotation
import com.compdfkit.core.annotation.CPDFTextAttribute
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.OutputListener
import java.io.File

class AnnotationReplyTest : PDFSamples() {
    init {
        setTitle(R.string.annotation_reply_title)
        setDescription(R.string.annotation_reply_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        printDividingLine()
        val document = CPDFDocument.createDocument(context())
        document.insertBlankPage(0, 595F, 842F)

        // Insert the free text annotation into the first page of the PDF document.
        val page = document.pageAtIndex(0)
        val freetextAnnotation =
            page.addAnnot(CPDFAnnotation.Type.FREETEXT) as CPDFFreetextAnnotation
        val size = page.size
        val freeText1Rect =
            page.convertRectToPage(false, size.width(), size.height(), RectF(10F, 20F, 160F, 100F))
        freetextAnnotation.setRect(freeText1Rect)
        freetextAnnotation.setContent("FreeText Annotation Reply Test")
        val textAttribute = CPDFTextAttribute(
            CPDFTextAttribute.FontNameHelper.obtainFontName(
                CPDFTextAttribute.FontNameHelper.FontType.Courier, false, false
            ), 12f, Color.RED
        )
        freetextAnnotation.setFreetextDa(textAttribute)
        // set text color opacity
        freetextAnnotation.setAlpha(255)
        freetextAnnotation.setTitle("ComPDFKit")
        freetextAnnotation.updateAp()

        // ----------------------------------------
        // Samples1 : create annotation reply info
        if (freetextAnnotation.allReplyAnnotations == null || freetextAnnotation.allReplyAnnotations.isEmpty()) {
            outputListener?.println("Annotation Type:${freetextAnnotation.type.name}")
            // -----------------------
            val replyAnnotation = freetextAnnotation.createReplyAnnotation()
            replyAnnotation.setMarkedAnnotState(CPDFAnnotation.MarkState.MARKED)
            replyAnnotation.setReviewAnnotState(CPDFAnnotation.ReviewState.REVIEW_COMPLETED)
            replyAnnotation.setTitle("Youna")
            replyAnnotation.setContent("Hello ComPDFKit")

            outputListener?.println("Create Reply Annotation:")
            outputListener?.println("Title:${replyAnnotation.title}")
            outputListener?.println("Content:${replyAnnotation.content}")
            outputListener?.println("ReviewState:${replyAnnotation.reviewAnnotState.name}")
            outputListener?.println("MarkedState:${replyAnnotation.markedAnnotState.name}")
            outputListener?.println("Create Reply Annotation End !!!")

            printDividingLine()

            // -----------------------
            val replyAnnotation1 = freetextAnnotation.createReplyAnnotation()
            replyAnnotation1.setMarkedAnnotState(CPDFAnnotation.MarkState.UNMARKED)
            replyAnnotation1.setReviewAnnotState(CPDFAnnotation.ReviewState.REVIEW_ACCEPTED)
            replyAnnotation1.setTitle("C-Long")
            replyAnnotation1.setContent("Hello World")
            outputListener?.println("Create Reply Annotation:")
            outputListener?.println("Title:${replyAnnotation1.title}")
            outputListener?.println("Content:${replyAnnotation1.content}")
            outputListener?.println("ReviewState:${replyAnnotation1.reviewAnnotState.name}")
            outputListener?.println("MarkedState:${replyAnnotation1.markedAnnotState.name}")
            outputListener?.println("Create Reply Annotation End !!!")
            printDividingLine()
        }

        // Samples 2: get all reply annotations
        val replyAnnotations = freetextAnnotation.allReplyAnnotations
        outputListener?.println("Get All Reply Annotations: ")
        outputListener?.println("Size: ${replyAnnotations.size}")
        outputListener?.println("Annotation Type:${freetextAnnotation.type.name}")

        for (replyAnnotation in replyAnnotations) {
            printDividingLine()
            outputListener?.println("Title:${replyAnnotation.title}")
            outputListener?.println("Content:${replyAnnotation.content}")
            outputListener?.println("ReviewState:${replyAnnotation.reviewAnnotState.name}")
            outputListener?.println("MarkedState:${replyAnnotation.markedAnnotState.name}")
        }
        saveSamplePDF(document, File(outputDir(), "AnnotationTest/AnnotationReplyTest.pdf"), true)
        printFooter()
    }
}
