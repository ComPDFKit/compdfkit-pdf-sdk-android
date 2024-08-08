package com.compdfkit.samples.samples

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.graphics.RectF
import com.compdfkit.core.document.CPDFDocument
import com.compdfkit.core.edit.CPDFEditPage
import com.compdfkit.core.edit.CPDFEditTextArea
import com.compdfkit.samples.PDFSamples
import com.compdfkit.samples.R
import com.compdfkit.samples.util.FileUtils
import com.compdfkit.samples.util.OutputListener
import com.compdfkit.ui.reader.CPDFPageView
import java.io.File


class ContentEditorTest : PDFSamples() {

    init {
        setTitle(R.string.content_editor_title)
        setDescription(R.string.content_editor_desc)
    }

    override fun run(outputListener: OutputListener?) {
        super.run(outputListener)
        printHead()
        // content editor insert text
        // Create a new document
        val document = CPDFDocument.createDocument(context())
        // Samples 1: Insert text on the first page of the document through content editing
        insertText(document)

        // Samples 2: Insert a picture on the second page of the document through content editing
        insertImage(document)
        // Save the generated PDF document
        val file = File(outputDir(), "ContentEditor/Insert_Text_Image_Test.pdf")
        saveSamplePDF(document, file, true)
        printFooter()
    }


    private fun insertText(document: CPDFDocument) {
        // Create a blank page with A4 size (595 x 842)
        document.insertBlankPage(0, 595f, 842f)
        // Get the first page of the document
        val page = document.pageAtIndex(0)
        val size = page.size

        // Convert the text area to the actual position on the page
        var area = run {
            // Set the position for adding text (top-left point)
            val point = Point(10, 10)
            val pointArea = RectF(point.x.toFloat(), point.y.toFloat(), point.x.toFloat(), point.y.toFloat())
            page.convertRectToPage(false, size.width(), size.height(), pointArea)
        }

        // Define the width and height of the text area
        val textAreaWidth = 200
        val textAreaHeight = 40
        var pageWidth = size.width().toInt()
        if (page.rotation == CPDFPageView.PageRotateType.PAGE_ROTATE_90.toInt() ||
            page.rotation == CPDFPageView.PageRotateType.PAGE_ROTATE_270.toInt()
        ) {
            pageWidth = size.height().toInt()
        }

        // Ensure the left start position of the text area does not exceed the page width
        if (area.left + textAreaWidth > pageWidth) {
            area.left = (pageWidth - textAreaWidth).toFloat()
        }

        // Ensure the top position of the text area is not less than 0 and does not exceed the page range
        if (area.top - textAreaHeight < 0) {
            area.top = textAreaHeight.toFloat()
        }

        // Determine the insertion position of the text area
        val rect = RectF(area.left, area.top, area.left + textAreaWidth, area.top - textAreaHeight)

        // Get the edit page object
        val cpdfEditPage = page.getEditPage(false)
        cpdfEditPage.beginEdit(CPDFEditPage.LoadTextImage)
        if (cpdfEditPage == null || !cpdfEditPage.isValid) {
            outputListener?.println("CPDFEditPage is INVALID")
            return
        }

        // Define the font, font size, and text color
        val fontName = "Arial"
        val fontSize = 30
        val textColor = Color.RED

        // Create a new text area
        val editTextArea = cpdfEditPage.createNewTextArea(
            rect,
            fontName,
            fontSize.toFloat(),
            textColor,
            255,
            false,
            false,
            CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft
        )

        // Get the start and end positions for text insertion
        val begin = editTextArea.beginCharPlace
        val end = editTextArea.endCharPlace

        // Define the content to be inserted
        val content = "Hello ComPDFKit"
        outputListener?.println("Insert text on the first page: $content")
        // Insert the content into the text area
        editTextArea.insertTextRange(begin.getPlace(), end.getPlace(), content)
    }


    private fun insertImage(document: CPDFDocument) {
        // Create a blank page with A4 size (595 x 842)
        document.insertBlankPage(1, 595F, 842F)
        // Get the first page of the document
        val page = document.pageAtIndex(1)

        // Get the edit page object
        val cpdfEditPage = page.getEditPage(false)

        // Start edit mode
        cpdfEditPage.beginEdit(CPDFEditPage.LoadImage)
        if (cpdfEditPage == null || !cpdfEditPage.isValid) {
            outputListener?.println("CPDFEditPage is INVALID")
            return
        }
        val point = Point(10, 10)
        var area = RectF(point.x.toFloat(), point.y.toFloat(), point.x.toFloat(), point.y.toFloat())
        val size = page.size
        var height = 200
        var width = 200
        // Convert the text area to the actual position on the page
        area = page.convertRectToPage(false, size.width(), size.height(), area)
        val imagePath = FileUtils.getAssetsTempFile(context(), "ComPDFKit.png")
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        val bitmapWidth = options.outWidth
        val bitmapHeight = options.outHeight
        val ratio = bitmapWidth * 1.0F / bitmapHeight
        if (ratio < 1) {
            height = (width / ratio).toInt()
        } else {
            width = (height * ratio).toInt()
        }
        var pagewidth = size.width().toInt()
        var pageheight = size.height().toInt()
        if (page.rotation == CPDFPageView.PageRotateType.PAGE_ROTATE_90.toInt() || page.rotation == CPDFPageView.PageRotateType.PAGE_ROTATE_270.toInt()) {
            pagewidth = size.height().toInt()
            pageheight = size.width().toInt()
        }
        if (width > pagewidth) {
            width = pagewidth
            height = (width / ratio).toInt()
            area.left = 0f
            if (area.top - height < 0) {
                area.top = height.toFloat()
            }
        } else if (height > pageheight) {
            height = pageheight
            width = (height * ratio).toInt()
            area.top = pageheight.toFloat()
            if (area.left + width > pagewidth) {
                area.left = (pagewidth - width).toFloat()
            }
        } else {
            if (area.left + width > pagewidth) {
                area.left = (pagewidth - width).toFloat()
            }
            if (area.top - height < 0) {
                area.top = height.toFloat()
            }
        }
        val rect = RectF(area.left, area.top, area.left + width, area.top - height)
        outputListener?.println("Insert image on the second page: assets/ComPDFKit.png")
        try {
            val editImageArea = cpdfEditPage.createNewImageArea(rect, imagePath, null)
            if (editImageArea == null || !editImageArea.isValid) {
                outputListener?.println("Failed to insert picture!!!")
            }
            cpdfEditPage.endEdit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}