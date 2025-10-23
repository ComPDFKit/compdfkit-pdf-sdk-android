/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.graphics.Color;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFHeaderFooter;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;

public class HeaderFooterTest extends PDFSamples {

    public HeaderFooterTest() {
        setTitle(R.string.header_footer_test_title);
        setDescription(R.string.header_footer_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        addCommonHeaderFooter();
        addPageHeaderFooter();
        editHeaderFooter();
        clearHeaderFooterTest();
        printFooter();
    }

    /**
     * samples 1 : add header
     */
    private void addCommonHeaderFooter() {
        printDividingLine();
        outputListener.println("Samples 1 : Insert common header footer");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFHeaderFooter headerFooter = document.getHeaderFooter();
        String headerStr = "ComPDFKit";
        // index 0 : top left
        // index 1 : top middle
        // index 2 : top right
        int index = 3;
        for (int i = 0; i < index; i++) {
            outputListener.println("Text: " + headerStr);
            headerFooter.setText(i, headerStr);
            headerFooter.setTextColor(i, Color.RED);
            headerFooter.setFontSize(i, 14);
            if (i == 0) {
                outputListener.println("Location: Top Left");
            } else if (i == 1) {
                outputListener.println("Location: Top Middle");
            } else {
                outputListener.println("Location: Top Right");
            }
            outputListener.println();
        }
        headerFooter.setPages("0,1,2,3,4");
        headerFooter.update();
        File file = new File(outputDir(), "HeaderFooterTest/AddCommonHeaderFooter.pdf");
        saveSamplePDF(document, file, false);
        outputListener.println("Done. Results saved in AddCommonHeaderFooter.pdf");
        printDividingLine();
    }

    private void addPageHeaderFooter() {
        outputListener.println("Samples 2 : Insert page header footer");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFHeaderFooter headerFooter = document.getHeaderFooter();
        String headerStr = "ComPDFKit";
        for (int i = 0; i < document.getPageCount(); i++) {
            int pageNumber = i + 1;
            int index = 6;
            // index 0 : top left
            // index 1 : top middle
            // index 2 : top right
            // index 3 : bottom left
            // index 4 : bottom middle
            // index 5 : bottom right
            for (int i1 = 0; i1 < index; i1++) {
                if (i1 < 3) {
                    outputListener.println("Text: " + headerStr);
                    headerFooter.setText(i1, headerStr);
                    headerFooter.setTextColor(i1, Color.BLACK);
                } else {
                    outputListener.println("Text: 0" + pageNumber);
                    headerFooter.setText(i1, "0" + pageNumber);
                    headerFooter.setTextColor(i1, Color.RED);
                }
                if (i1 == 0) {
                    outputListener.println("Location: Top Left");
                } else if (i1 == 1) {
                    outputListener.println("Location: Top Middle");
                } else if (i1 == 2) {
                    outputListener.println("Location: Top Right");
                } else if (i1 == 3) {
                    outputListener.println("Location: Bottom Left");
                } else if (i1 == 4) {
                    outputListener.println("Location: Bottom Middle");
                } else {
                    outputListener.println("Location: Bottom Right");
                }
                headerFooter.setFontSize(i, 14);
            }
            headerFooter.setPages("" + i);
            headerFooter.update();
        }
        File file = new File(outputDir(), "HeaderFooterTest/AddPageHeaderFooter.pdf");
        saveSamplePDF(document, file, false);
        outputListener.println("Done. Results saved in AddPageHeaderFooter.pdf");
        printDividingLine();
    }

    /**
     * samples 3 : edit top left header
     */
    private void editHeaderFooter() {
        outputListener.println("Samples 3 : Edit top left header");
        CPDFDocument document = new CPDFDocument(context);
        File file = new File(outputDir(), "HeaderFooterTest/AddCommonHeaderFooter.pdf");
        document.open(file.getAbsolutePath());
        CPDFHeaderFooter headerFooter = document.getHeaderFooter();
        outputListener.println("Get old head and footer 0 succeeded, text is " + headerFooter.getText(0));
        outputListener.println("Change head and footer 0 succeeded, new text is ComPDFKit Samples");
        // change top left text
        headerFooter.setText(0, "ComPDFKit Samples");
        headerFooter.update();
        File resultsFile = new File(outputDir(), "HeaderFooterTest/EditHeaderFooterTest.pdf");
        saveSamplePDF(document, resultsFile, false);
        outputListener.println("Done. Results saved in EditHeaderFooterTest.pdf");
        printDividingLine();
    }

    private void clearHeaderFooterTest(){
        outputListener.println("Samples 4 : Clean all header and footer");
        CPDFDocument document = new CPDFDocument(context);
        File file = new File(outputDir(), "HeaderFooterTest/AddCommonHeaderFooter.pdf");
        document.open(file.getAbsolutePath());
        CPDFHeaderFooter headerFooter = document.getHeaderFooter();
        outputListener.println("");
        headerFooter.clear();
        File resultsFile = new File(outputDir(), "HeaderFooterTest/ClearHeaderFooterTest.pdf");
        saveSamplePDF(document, resultsFile, false);
        outputListener.println("Done. Results saved in ClearHeaderFooterTest.pdf");
        printDividingLine();
    }
}
