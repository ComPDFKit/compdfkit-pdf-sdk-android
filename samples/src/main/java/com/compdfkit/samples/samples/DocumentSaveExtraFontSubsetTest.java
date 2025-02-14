package com.compdfkit.samples.samples;


import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;

import com.compdfkit.core.common.CPDFDocumentException;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditCharItem;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.core.edit.CPDFEditTextArea;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.OutputListener;
import com.compdfkit.ui.reader.CPDFPageView;

import java.io.File;

public class DocumentSaveExtraFontSubsetTest extends PDFSamples {

    public DocumentSaveExtraFontSubsetTest(){
        setTitle(R.string.content_document_save_extra_font_subset_title);
        setDescription(R.string.content_document_save_extra_font_subset_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        // Create a new document
        CPDFDocument document = CPDFDocument.createDocument(context);
        // Samples 1: Insert text on the first page of the document through content editing
        insertText(document, "Hello ComPDFKit Extra Font Subset");

        // Save the generated PDF document
        File file = new File(outputDir(), "DocumentTest/Sample_Extra_FontSubset.pdf");
        saveSamplePDF(document, file, true, true);

        CPDFDocument document2 = CPDFDocument.createDocument(context);
        insertText(document2, "Hello ComPDFKit Font set not saved");

        File file1 = new File(outputDir(), "DocumentTest/Sample_Extra_NotSaved_FontSubset.pdf");
        saveSamplePDF(document2, file1, true, false);

        printFooter();
    }


    private void insertText(CPDFDocument document, String content){
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
        int textAreaHeight = 60;

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
        cpdfEditPage.beginEdit(CPDFEditPage.LoadText);

        if (cpdfEditPage == null || !cpdfEditPage.isValid()) {
            outputListener.println("CPDFEditPage is INVALID");
            return;
        }

        // Define the font, font size, and text color
//        String fontName = "Arial";
        String fontName = "Arial";
        int fontSize = 30;
        int textColor = Color.RED;

        // Create a new text area
        CPDFEditTextArea editTextArea = cpdfEditPage.createNewTextArea(rect, fontName, fontSize, textColor, 255, false, false, CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);
        if (editTextArea != null) {
            // Get the start and end positions for text insertion
            CPDFEditCharItem begin = editTextArea.getBeginCharPlace();
            CPDFEditCharItem end = editTextArea.getEndCharPlace();
            outputListener.println("Insert text on the first page: " + content);
            // Insert the content into the text area
            editTextArea.insertTextRange(begin.getPlace(), end.getPlace(), content);
            cpdfEditPage.endEdit();
        }

    }

    protected void saveSamplePDF(CPDFDocument document, File file, boolean close, boolean extraFontSubset){
        try {
            file.getParentFile().mkdirs();
            document.saveAs(file.getAbsolutePath(), false, false, extraFontSubset);
            if (file.exists()) {
                getOutputFileList().add(file.getAbsolutePath());
            }
            if (close){
                document.close();
            }
        } catch (CPDFDocumentException e) {
            e.printStackTrace();
        }
    }

}
