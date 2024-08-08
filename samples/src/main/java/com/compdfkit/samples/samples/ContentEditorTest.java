package com.compdfkit.samples.samples;


import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditCharItem;
import com.compdfkit.core.edit.CPDFEditImageArea;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.core.edit.CPDFEditTextArea;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;
import com.compdfkit.ui.reader.CPDFPageView;

import java.io.File;

public class ContentEditorTest extends PDFSamples {

    public ContentEditorTest(){
        setTitle(R.string.content_editor_title);
        setDescription(R.string.content_editor_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        // content editor insert text
        // Create a new document
        CPDFDocument document = CPDFDocument.createDocument(context);
        // Samples 1: Insert text on the first page of the document through content editing
        insertText(document);

        // Samples 2: Insert a picture on the second page of the document through content editing
        insertImage(document);
        // Save the generated PDF document
        File file = new File(outputDir(), "ContentEditor/InsertTextTest.pdf");
        saveSamplePDF(document, file, true);
        printFooter();
    }


    private void insertText(CPDFDocument document){
        // Create a blank page with A4 size (595 x 842)
        document.insertBlankPage(0, 595, 842);
        // Get the first page of the document
        CPDFPage page = document.pageAtIndex(0);
        // Set the position for adding text (top-left point)
        Point point = new Point(10, 10);
        RectF area = new RectF(point.x, point.y, point.x, point.y);
        RectF size = page.getSize();

        // Convert the text area to the actual position on the page
        area = page.convertRectToPage(false, size.width(), size.height(), area);

        // Define the width and height of the text area
        int textAreaWidth = 200;
        int textAreaHeight = 40;

        int pageWidth = (int) size.width();
        if (page.getRotation() == CPDFPageView.PageRotateType.PAGE_ROTATE_90.toInt() ||
                page.getRotation() == CPDFPageView.PageRotateType.PAGE_ROTATE_270.toInt()) {
            pageWidth = (int) size.height();
        }

        // Ensure the left start position of the text area does not exceed the page width
        if (area.left + textAreaWidth > pageWidth) {
            area.left = pageWidth - textAreaWidth;
        }

        // Ensure the top position of the text area is not less than 0 and does not exceed the page range
        if (area.top - textAreaHeight < 0) {
            area.top = textAreaHeight;
        }

        // Determine the insertion position of the text area
        RectF rect = new RectF(area.left, area.top, area.left + textAreaWidth, area.top - textAreaHeight);

        // Get the edit page object
        CPDFEditPage cpdfEditPage = page.getEditPage(false);
        cpdfEditPage.beginEdit(CPDFEditPage.LoadTextImage);

        if (cpdfEditPage == null || !cpdfEditPage.isValid()) {
            outputListener.println("CPDFEditPage is INVALID");
            return;
        }

        // Define the font, font size, and text color
        String fontName = "Arial";
        int fontSize = 30;
        int textColor = Color.RED;

        // Create a new text area
        CPDFEditTextArea editTextArea = cpdfEditPage.createNewTextArea(rect, fontName, fontSize, textColor, 255, false, false, CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);

        // Get the start and end positions for text insertion
        CPDFEditCharItem begin = editTextArea.getBeginCharPlace();
        CPDFEditCharItem end = editTextArea.getEndCharPlace();

        // Define the content to be inserted
        String content = "Hello ComPDFKit";

        outputListener.println("Insert text on the first page: " + content);
        // Insert the content into the text area
        editTextArea.insertTextRange(begin.getPlace(), end.getPlace(), content);
    }


    private void insertImage(CPDFDocument document){
        // Create a blank page with A4 size (595 x 842)
        document.insertBlankPage(1, 595, 842);
        // Get the first page of the document
        CPDFPage page = document.pageAtIndex(1);

        // Get the edit page object
        CPDFEditPage cpdfEditPage = page.getEditPage(false);

        // Start edit mode
        cpdfEditPage.beginEdit(CPDFEditPage.LoadImage);

        if (cpdfEditPage == null || !cpdfEditPage.isValid()) {
            outputListener.println("CPDFEditPage is INVALID");
            return;
        }

        Point point = new Point(10, 10);
        RectF area = new RectF(point.x, point.y, point.x, point.y);
        RectF size = page.getSize();

        int height = 200;
        int width = 200;
        // Convert the text area to the actual position on the page
        area = page.convertRectToPage(false, size.width(), size.height(), area);

        String imagePath = FileUtils.getAssetsTempFile(context, "ComPDFKit.png");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int bitmapwidth = options.outWidth;
        int bitmapheight = options.outHeight;

        float ratio = bitmapwidth * 1.0f / bitmapheight;
        if (ratio < 1) {
            height = (int)(width / ratio);
        } else {
            width = (int)(height * ratio);
        }

        int pagewidth = (int)size.width();
        int pageheight = (int)size.height();
        if (page.getRotation() == CPDFPageView.PageRotateType.PAGE_ROTATE_90.toInt() || page.getRotation() == CPDFPageView.PageRotateType.PAGE_ROTATE_270.toInt()) {
            pagewidth = (int)size.height();
            pageheight = (int)size.width();
        }

        if (width > pagewidth) {
            width = pagewidth;
            height = (int)(width / ratio);
            area.left = 0;
            if (area.top - height < 0) {
                area.top = height;
            }
        } else if (height > pageheight) {
            height = pageheight;
            width = (int)(height * ratio);
            area.top = pageheight;
            if (area.left + width > pagewidth) {
                area.left = pagewidth - width;
            }
        } else {
            if (area.left + width > pagewidth) {
                area.left = pagewidth - width;
            }
            if (area.top - height < 0) {
                area.top = height;
            }
        }

        RectF rect = new RectF(area.left, area.top, area.left + width, area.top - height);
        outputListener.println("Insert image on the second page: assets/ComPDFKit.png" );
        try{
            CPDFEditImageArea editImageArea = cpdfEditPage.createNewImageArea(rect, imagePath, null);
            if (editImageArea == null || !editImageArea.isValid()){
                outputListener.println("Failed to insert picture!!!");
            }
            cpdfEditPage.endEdit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
