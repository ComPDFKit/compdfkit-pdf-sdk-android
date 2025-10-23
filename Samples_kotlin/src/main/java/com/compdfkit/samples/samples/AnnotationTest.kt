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
import android.graphics.PointF
import android.graphics.RectF
import com.compdfkit.core.annotation.CPDFAnnotation
import com.compdfkit.core.annotation.CPDFBorderStyle
import com.compdfkit.core.annotation.CPDFCircleAnnotation
import com.compdfkit.core.annotation.CPDFFreetextAnnotation
import com.compdfkit.core.annotation.CPDFHighlightAnnotation
import com.compdfkit.core.annotation.CPDFInkAnnotation
import com.compdfkit.core.annotation.CPDFLineAnnotation
import com.compdfkit.core.annotation.CPDFLinkAnnotation
import com.compdfkit.core.annotation.CPDFSoundAnnotation
import com.compdfkit.core.annotation.CPDFSquareAnnotation
import com.compdfkit.core.annotation.CPDFStampAnnotation
import com.compdfkit.core.annotation.CPDFStampAnnotation.StandardStamp
import com.compdfkit.core.annotation.CPDFStampAnnotation.TextStamp
import com.compdfkit.core.annotation.CPDFTextAnnotation
import com.compdfkit.core.annotation.CPDFTextAttribute
import com.compdfkit.core.document.CPDFDestination
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.document.action.CPDFGoToAction
import com.compdfkit.core.page.CPDFPage
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils.getAssetsTempFile
import com.compdfkit.samples.util.OutputListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

class AnnotationTest : PDFSamples() {
    init {
        setTitle(R.string.annotation_test_title)
        setDescription(R.string.annotation_test_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        val document = CPDFDocument(context())
        document.open(getAssetsTempFile(context(), "CommonFivePage.pdf"))
        printDividingLine()
        //------------------------------------------
        //Samples 1 : add freetext annotation
        addFreeText(document)

        //------------------------------------------
        //Samples 2 : add ink annotation
        addInk(document)

        //------------------------------------------
        //Samples 3 : add line annotation
        addLine(document)

        //------------------------------------------
        //Samples 4 : add circle annotation
        addCircleShape(document)

        //------------------------------------------
        //Samples 5 : add square annotation
        addSquareShape(document)

        //------------------------------------------
        //Samples 6 : add highlight(markup) annotation
        addHighlight(document)

        //------------------------------------------
        //Samples 7 : add link annotation
        addLink(document)

        //------------------------------------------
        //Samples 8 : add note annotation
        addNote(document)

        //------------------------------------------
        //Samples 8 : add sound annotation
        addSound(document)

        //------------------------------------------
        //Samples 9 : add stamp annotation
        addStamp(document)
        val file = File(outputDir(), "AnnotationTest/CreateAnnotationTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener!!.println("Done.")
        outputListener.println("\nDone. Result saved in CreateAnnotationTest.pdf")
        printDividingLine()

        //------------------------------------------
        //Samples 10 : print annotation list info
        printAnnotationList()

        //------------------------------------------
        //Samples 11 : delete annotation
        deleteAnnotation()
        printFooter()
    }

    /**
     * Samples 1 : add freetext annotation
     *
     * @param document
     */
    private fun addFreeText(document: CPDFDocument) {
        // Insert the free text annotation into the first page of the PDF document.
        val page1 = document.pageAtIndex(0)
        (page1.addAnnot(CPDFAnnotation.Type.FREETEXT) as CPDFFreetextAnnotation).apply {
            rect = getConvertRect(page1, RectF(10f, 200f, 160f, 570f))
            // set text alignment
            freetextAlignment = CPDFFreetextAnnotation.Alignment.ALIGNMENT_LEFT
            // Set text font, bold, italic, color and font size
            freetextDa = CPDFTextAttribute(CPDFTextAttribute.FontNameHelper.obtainFontName(
                    CPDFTextAttribute.FontNameHelper.FontType.Courier, false, false
            ), 12f, Color.RED)
            // set text color opacity
            alpha = 255
            content = """
             Some swift brown fox snatched a gray hare out of the air by freezing it with an angry glare.
             Aha!
             And there was much rejoicing!
             """
            updateAp()
        }
    }

    /**
     * Samples 2 : add ink annotation
     *
     * @param document
     */
    private fun addInk(document: CPDFDocument) {
        // Insert the ink annotation into the first page of the PDF document.
        val page1 = document.pageAtIndex(0)
        val mDrawing = arrayListOf(
                arrayListOf(
                        PointF(100F, 100F),
                        PointF(110F, 110F),
                        PointF(120F, 120F)
                ),
                arrayListOf(
                        PointF(115F, 115F),
                        PointF(130F, 130F),
                        PointF(160F, 160F)
                )
        )
        val scaleValue = 1F
        val borderWidth = 5F
        val inkAnnotation = page1.addAnnot(CPDFAnnotation.Type.INK) as CPDFInkAnnotation
        inkAnnotation.color = Color.RED
        inkAnnotation.alpha = 255
        inkAnnotation.borderWidth = borderWidth

        var rect: RectF? = null
        val size = document.getPageSize(0)
        if (size.isEmpty) {
            return
        }
        val lineCount = mDrawing.size
        val path: Array<Array<PointF?>?> = arrayOfNulls(lineCount)
        for (lineIndex in 0 until lineCount) {
            val line = mDrawing[lineIndex]
            val pointCount = line.size
            val linePath = arrayOfNulls<PointF>(pointCount)
            for (pointIndex in 0 until pointCount) {
                val point = line[pointIndex]
                // Calculate the smallest Rect that the Path is surrounded by
                if (rect == null) {
                    rect = RectF(point.x, point.y, point.x, point.y)
                } else {
                    rect.union(point.x, point.y)
                }
                // Calculate the coordinate points that are converted to the Page and stored in the linePath collection
                linePath[pointIndex] = page1.convertPointToPage(false, size.width(), size.height(), point)
            }
            path[lineIndex] = linePath
        }
        val dx = borderWidth / scaleValue / 2
        rect?.inset(-dx, -dx)
        rect?.set(page1.convertRectToPage(false, size.width(), size.height(), rect))
        inkAnnotation.inkPath = path
        inkAnnotation.rect = rect
        inkAnnotation.updateAp()
        mDrawing.clear()
    }

    /**
     * Samples 3 : add line shape annotation
     *
     * @param document
     */
    private fun addLine(document: CPDFDocument) {
        // Add a green dotted line annotation
        val page2 = document.pageAtIndex(1)
        (page2.addAnnot(CPDFAnnotation.Type.LINE) as CPDFLineAnnotation).apply {
            // line start point
            val startPoint = PointF(200F, 100F)
            // line end point
            val endPoint = PointF(50F, 300F)
            // Get the position of the line on the page
            rect = kotlin.run {
                val area = RectF()
                convertLinePoint(page2, startPoint, endPoint, area)
                area
            }
            setLinePoints(startPoint, endPoint)
            // Sets the arrowhead shape at both ends of the line
            setLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE, CPDFLineAnnotation.LineType.LINETYPE_NONE)
            // Set line to dash and spacer width
            borderStyle = CPDFBorderStyle(CPDFBorderStyle.Style.Border_Dashed, 10F, floatArrayOf(8F, 4F))
            // set line width
            borderWidth = 3F
            borderAlpha = 255
            borderColor = Color.GREEN
            updateAp()
        }


        //Add a solid red line annotation with an arrow type
        (page2.addAnnot(CPDFAnnotation.Type.LINE) as CPDFLineAnnotation).apply {
            val startPoint2 = PointF(200F, 350F)
            val endPoint2 = PointF(50F, 550F)
            rect = kotlin.run {
                val area2 = RectF()
                convertLinePoint(page2, startPoint2, endPoint2, area2)
                area2
            }
            setLinePoints(startPoint2, endPoint2)
            // Set the start position of the arrow as circle and the end of the arrow as the arrow type
            setLineType(CPDFLineAnnotation.LineType.LINETYPE_CIRCLE, CPDFLineAnnotation.LineType.LINETYPE_ARROW)
            // set line to solid
            borderStyle = CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10f, floatArrayOf(8f, 0f))
            borderWidth = 5F
            borderAlpha = 255
            borderColor = Color.RED
            updateAp()
        }

        // Add a solid red line annotation
        (page2.addAnnot(CPDFAnnotation.Type.LINE) as CPDFLineAnnotation).apply {
            val startPoint3 = PointF(400F, 100F)
            val endPoint3 = PointF(250F, 300F)
            rect = kotlin.run {
                val area3 = RectF()
                convertLinePoint(page2, startPoint3, endPoint3, area3)
                area3
            }
            setLinePoints(startPoint3, endPoint3)
            setLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE, CPDFLineAnnotation.LineType.LINETYPE_NONE)
            borderStyle = CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10F, floatArrayOf(8F, 0F))
            borderWidth = 5F
            borderAlpha = 255
            borderColor = Color.BLUE
            updateAp()
        }
    }

    private fun convertLinePoint(page: CPDFPage, startPoint: PointF, endPoint: PointF, area: RectF) {
        val pageSize = page.size
        val lineWidth = 10F

        val minX = startPoint.x.coerceAtMost(endPoint.x) - lineWidth * 2
        val minY = startPoint.y.coerceAtMost(endPoint.y) - lineWidth * 2
        val maxX = startPoint.x.coerceAtLeast(endPoint.x) + lineWidth * 2
        val maxY = startPoint.y.coerceAtLeast(endPoint.y) + lineWidth * 2

        area.set(minX, minY, maxX, maxY)
        area.set(page.convertRectToPage(false, pageSize.width(), pageSize.height(), area))
        startPoint.set(page.convertPointToPage(false, pageSize.width(), pageSize.height(), startPoint))
        endPoint.set(page.convertPointToPage(false, pageSize.width(), pageSize.height(), endPoint))
    }

    /**
     * Samples 4 : add circle shape annotation
     *
     * @param document
     */
    private fun addCircleShape(document: CPDFDocument) {
        // Add a circular annotation with a green border and blue fill
        val page3 = document.pageAtIndex(2)
        (page3.addAnnot(CPDFAnnotation.Type.CIRCLE) as CPDFCircleAnnotation).apply {
            val insertRect = getConvertRect(page3, RectF(50F, 50F, 150F, 150F))
            rect = insertRect
            // set border color
            borderColor = Color.parseColor("#3863F1")
            // Set border to solid line
            borderStyle = CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10F, floatArrayOf(8.0F, 0F)).apply {
                borderWidth = 5F
            }
            borderAlpha = 255
            fillColor = Color.parseColor("#31BC98")
            fillAlpha = 255
            updateAp()
        }

        //Add a circular shape annotation with a dotted border.
        (page3.addAnnot(CPDFAnnotation.Type.CIRCLE) as CPDFCircleAnnotation).apply {
            rect = getConvertRect(page3, RectF(50F, 200F, 150F, 300F))
            borderColor = Color.parseColor("#3863F1")
            borderStyle = CPDFBorderStyle(CPDFBorderStyle.Style.Border_Dashed, 10F, floatArrayOf(8.0F, 4F)).apply {
                borderWidth = 5F
            }
            borderAlpha = 127
            fillColor = Color.parseColor("#31BC98")
            fillAlpha = 127
            updateAp()
        }
    }

    /**
     * Samples 5 : add square shape annotation
     *
     * @param document
     */
    private fun addSquareShape(document: CPDFDocument) {
        // Add a rectangle with a blue border and green fill
        val page3 = document.pageAtIndex(2)
        (page3.addAnnot(CPDFAnnotation.Type.SQUARE) as CPDFSquareAnnotation).apply {
            rect = getConvertRect(page3, RectF(50F, 350F, 300F, 450F))
            borderColor = Color.parseColor("#3863F1")
            borderStyle = CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10F, floatArrayOf(8.0F, 0F)).apply {
                borderWidth = 10F
            }
            borderAlpha = 255
            fillColor = Color.parseColor("#31BC98")
            fillAlpha = 255
            updateAp()
        }

        // Add a rectangle with a blue dotted border and a green fill with 50% transparency
        // Add a rectangle with a blue dotted border and a green fill with 50% transparency
        val pageSize = page3.size
        // Add a rectangle with a blue dotted border and a green fill with 50% transparency
        val squareAnnotation2 = page3.addAnnot(CPDFAnnotation.Type.SQUARE) as CPDFSquareAnnotation
        var insertRect2: RectF? = RectF(50F, 500F, 300F, 600F)
        insertRect2 = page3.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect2)
        squareAnnotation2.rect = insertRect2
        squareAnnotation2.borderColor = Color.parseColor("#3863F1")
        val borderStyle2 = CPDFBorderStyle(CPDFBorderStyle.Style.Border_Dashed, 10f, floatArrayOf(8.0f, 4f))
        borderStyle2.borderWidth = 10f
        squareAnnotation2.borderStyle = borderStyle2
        squareAnnotation2.borderAlpha = 127
        squareAnnotation2.fillColor = Color.parseColor("#31BC98")
        squareAnnotation2.fillAlpha = 127
        squareAnnotation2.updateAp()
    }

    /**
     * Samples 6 : add high annotation
     * Here is a demonstration of searching out keywords in the search page and adding highlighted annotation
     *
     * @param document
     */
    private fun addHighlight(document: CPDFDocument) {
        //Also search for the `Page` keyword in the 3rd of the document
        val pdfPage = document.pageAtIndex(3)
        //Then, add a highlight annotation for the specific area.
        (pdfPage.addAnnot(CPDFAnnotation.Type.HIGHLIGHT) as CPDFHighlightAnnotation).apply {
            var annotRect = RectF(315F, 258F, 372F, 288F)
            color = Color.YELLOW
            alpha = 255 / 2
            quadRects = kotlin.run {
                annotRect = pdfPage.convertRectToPage(false, pdfPage.size.width(), pdfPage.size.height(), annotRect)
                arrayOf(annotRect)
            }
            markedText = "Page"
            rect = annotRect
            updateAp()
            outputListener!!.println(annotRect.toString())
        }
    }

    /**
     * Samples 7 : add link annotation
     *
     * @param document
     */
    private fun addLink(document: CPDFDocument) {
        val page = document.pageAtIndex(3)
        (page.addAnnot(CPDFAnnotation.Type.LINK) as CPDFLinkAnnotation).apply {
            rect = getConvertRect(page, RectF(50F, 50F, 150F, 150F))
            val firstPageHeight = document.getPageSize(0).height()
            // Add page jump link action
            linkAction = CPDFGoToAction().apply {
                val destination = CPDFDestination(1, 0F, firstPageHeight, 1F)
                setDestination(document, destination)
            }
            updateAp()
        }
    }

    /**
     * Samples 8 : add note annotation
     *
     * @param document
     */
    private fun addNote(document: CPDFDocument) {
        val page = document.pageAtIndex(3)
        val textAnnotation = page.addAnnot(CPDFAnnotation.Type.TEXT) as CPDFTextAnnotation
        //get the actual size of the page you want to insert
        val insertRect = getConvertRect(page, RectF(50F, 200F, 100F, 250F))
        textAnnotation.rect = insertRect
        textAnnotation.content = "ComPDFKit"
        textAnnotation.updateAp()
    }

    /**
     * Samples 9 : add sound annotation
     *
     * @param document
     */
    private fun addSound(document: CPDFDocument) {
        val page = document.pageAtIndex(3)
        val soundAnnotation = page.addAnnot(CPDFAnnotation.Type.SOUND) as CPDFSoundAnnotation
        val insertRect = getConvertRect(page, RectF(50F, 300F, 100F, 350F))
        soundAnnotation.rect = insertRect
        soundAnnotation.setSoundPath(getAssetsTempFile(context(), "Bird.wav"))
        soundAnnotation.updateAp()
    }

    /**
     * Samples 10 : add stamp annotation
     *
     * @param document
     */
    private fun addStamp(document: CPDFDocument) {
        // add standard stamp annotation
        var yOffset: Int
        var lastOffset = 0F
        for (i in StandardStamp.values().indices) {
            val page = document.pageAtIndex(4)
            val standardStamp = StandardStamp.values()[i]
            if (standardStamp == StandardStamp.UNKNOWN) {
                continue
            }
            // add Standard stamp
            val standard = page.addAnnot(CPDFAnnotation.Type.STAMP) as CPDFStampAnnotation
            standard.standardStamp = standardStamp
            val pageSize = page.size
            val insertRect = standard.rect
            insertRect.set(page.convertRectFromPage(false, pageSize.width(), pageSize.height(), insertRect))
            val defaultWidth = 100F
            var x = 50
            if (i == 10) {
                lastOffset = 50F
            }
            if (i >= 10) {
                x = 150
            }
            yOffset = lastOffset.toInt() + 10
            val vertex = PointF(x.toFloat(), yOffset.toFloat())
            insertRect[vertex.x, vertex.y, vertex.x + defaultWidth] = vertex.y + defaultWidth * abs(insertRect.height() / insertRect.width())
            lastOffset = insertRect.bottom
            standard.rect = page.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect)
            standard.updateAp()
        }

        //add text stamp annotations
        val page = document.pageAtIndex(4)
        val stampAnnotation = page.addAnnot(CPDFAnnotation.Type.STAMP) as CPDFStampAnnotation
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = df.format(Date())
        stampAnnotation.textStamp = TextStamp(
                "ComPDFKit", date, CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT.id,
                CPDFStampAnnotation.TextStampColor.TEXTSTAMP_GREEN.id)
        val pageSize = page.size
        val insertRect = stampAnnotation.rect
        insertRect.set(page.convertRectFromPage(false, pageSize.width(), pageSize.height(), insertRect))
        val defaultWidth = 150F
        val vertex = PointF(300F, 50F)
        insertRect[vertex.x, vertex.y, vertex.x + defaultWidth] = vertex.y + defaultWidth * abs(insertRect.height() / insertRect.width())
        stampAnnotation.rect = page.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect)
        stampAnnotation.updateAp()

        // add image stamp annotations
        val standard = page.addAnnot(CPDFAnnotation.Type.STAMP) as CPDFStampAnnotation
        val imagePath = getAssetsTempFile(context(), "ComPDFKit.png")
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)

        standard.rect = kotlin.run {
            val imageDefaultWidth = 100F
            val imageVertex = PointF(300F, 300F)
            val imageInsertRect = RectF(imageVertex.x, imageVertex.y, imageVertex.x + imageDefaultWidth, imageVertex.y + imageDefaultWidth * options.outHeight / options.outWidth)
            val pageSize1 = page.size
            imageInsertRect.set(page.convertRectToPage(false, pageSize1.width(), pageSize1.height(), imageInsertRect))
            imageInsertRect
        }
        if (imagePath?.endsWith("png", true) == true) {
            val tmpOptions = BitmapFactory.Options()
            tmpOptions.inMutable = true
            val bitmap = BitmapFactory.decodeFile(imagePath, tmpOptions)
            standard.updateApWithBitmap(bitmap)
            bitmap.recycle()
        } else {
            standard.setImageStamp(imagePath)
            standard.updateAp()
        }
    }

    private fun getConvertRect(page: CPDFPage, rectF: RectF): RectF {
        val size = page.size
        return page.convertRectToPage(false, size.width(), size.height(), rectF)
    }

    /**
     * Samples 10 : print annotation list info
     */
    private fun printAnnotationList() {
        printDividingLine()
        val sampleFile = File(outputDir(), "AnnotationTest/CreateAnnotationTest.pdf")
        val document = CPDFDocument(context())
        document.open(sampleFile.absolutePath)
        for (i in 0 until document.pageCount) {
            val page = document.pageAtIndex(i)
            for (annotation in page.annotations) {
                outputListener?.println("Page: " + (i + 1))
                outputListener?.println("Annot Type: " + annotation.type.name)
                val position = page.convertRectFromPage(false, document.getPageSize(i).width(),
                        document.getPageSize(i).height(), annotation.rect)
                outputListener?.println(String.format("Position: %d, %d, %d, %d", position.left.toInt(), position.top.toInt(), position.right.toInt(), position.bottom.toInt()))
                printDividingLine()
            }
        }
        document.close()
    }

    /**
     * Samples 11 : delete annotation
     */
    private fun deleteAnnotation() {
        val sampleFile = File(outputDir(), "AnnotationTest/CreateAnnotationTest.pdf")
        val document = CPDFDocument(context())
        document.open(sampleFile.absolutePath)
        for (i in 0 until document.pageCount) {
            val page = document.pageAtIndex(i)
            val annotations = page.annotations
            if (annotations != null && annotations.size > 0) {
                page.deleteAnnotation(annotations[0])
                break
            }
        }
        val file = File(outputDir(), "AnnotationTest/DeleteAnnotationTest.pdf")
        saveSamplePDF(document, file, true)
        outputListener?.println("Done.")
        outputListener?.println("Done. Results saved in DeleteAnnotationTest.pdf")
    }
}