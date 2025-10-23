/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */


package com.compdfkit.samples.samples;

import android.graphics.RectF;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextRange;
import com.compdfkit.core.page.CPDFTextSelection;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;


public class TextExtractTest extends PDFSamples {

    public TextExtractTest(){
        setTitle(R.string.text_extract_test_title);
        setDescription(R.string.text_extract_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        extractPageText();
        extractAllPageText();
        extractRectRangeText();
        printFooter();
    }

    /**
     * Samples 1: Extract all text content in the specified page
     */
    private void extractPageText(){
        outputListener.println("Samples 1: Extract all text content in the specified page");
        outputListener.println("Opening the Samples PDF File");
        outputListener.println("The text content of the first page of the document:");
        printDividingLine();
        outputListener.println("Text : ");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "text.pdf"));
        CPDFPage page = document.pageAtIndex(0);
        CPDFTextPage textPage = page.getTextPage();
        String pageText = textPage.getText(new CPDFTextRange(0, textPage.getCountChars() - 1) );
        outputListener.print(pageText);
        outputListener.println("\nDone!");
        printDividingLine();
    }

    /**
     * Samples 2: Extract all text content of the document
     */
    private void extractAllPageText(){
        outputListener.println("Samples 2: Extract all text content of the document");
        outputListener.println("Opening the Samples PDF File");
        printDividingLine();
        outputListener.println("Text : ");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "text.pdf"));
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFPage page = document.pageAtIndex(i);
            CPDFTextPage textPage = page.getTextPage();
            String pageText = textPage.getText(new CPDFTextRange(0, textPage.getCountChars() - 1) );
            outputListener.print(pageText);
        }
        outputListener.println("Done!");
        printDividingLine();
    }

    private void extractRectRangeText(){
        outputListener.println("Samples 3: Extract Select Rect Range Text");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "text.pdf"));
        CPDFPage page = document.pageAtIndex(0);
        CPDFTextPage textPage = page.getTextPage();
        // Extract the text within the range of （0, 0, 500, 500) on the first page
        RectF selectRect = new RectF(0f, 0f, 300f, 300f);
        RectF size = page.getSize();
        selectRect = page.convertRectFromPage(false, size.width(), size.height(), selectRect);
        CPDFTextSelection[] selections = textPage.getSelectionsByLineForRect(selectRect);
        outputListener.println("Range : (0, 0, 300, 300)");
        outputListener.println("Text : ");
        for (int i = 0; i < selections.length; i++) {
            CPDFTextSelection textSelection = selections[i];
            if (textSelection == null) {
                continue;
            }
            String text = textPage.getText(textSelection.getTextRange());
            outputListener.print(text);
        }
        outputListener.println("\nDone!");
        printDividingLine();
    }
}
