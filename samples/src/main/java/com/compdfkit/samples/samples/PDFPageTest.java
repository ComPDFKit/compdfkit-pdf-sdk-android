/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class PDFPageTest extends PDFSamples {


    public PDFPageTest() {
        setTitle(R.string.pdf_page_test_title);
        setDescription(R.string.pdf_page_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        insertBlankPage();
        insertPdfPage();
        splitPages();
        mergePages();
        deletePages();
        rotatePage();
        replacePages();
        extractPages();
        printFooter();
    }

    /**
     * Samples - 1: Insert a blank A4-sized page into the sample document
     */
    private void insertBlankPage() {
        printDividingLine();
        outputListener.println("Samples 1: Insert a blank A4-sized page into the sample document");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        document.insertBlankPage(1, 595, 842);
        outputListener.println("Insert PageIndex : 1");
        outputListener.println("Size : 595*842");
        File file = new File(outputDir(), "pdfPage/Insert_Blank_Page.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in Insert_Blank_Page.pdf");
        printDividingLine();
    }

    /**
     * Samples - 2: Import pages from another document into the example document
     */
    private void insertPdfPage() {
        outputListener.println("Samples 2: Import pages from another document into the example document");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        outputListener.println("Open the document to be imported");
        //Open the document to be imported
        CPDFDocument document2 = new CPDFDocument(context);
        document2.open(FileUtils.getAssetsTempFile(context, "text.pdf"));
        document.importPages(document2, new int[]{0}, 1);
        File file = new File(outputDir(), "pdfPage/Import_Document_Page.pdf");
        saveSamplePDF(document, file, true);

        outputListener.println("Done. Result saved in Import_Document_Page.pdf");
        printDividingLine();
    }

    /**
     * Samples - 3: Split a PDF document into multiple pages
     */
    private void splitPages() {
        outputListener.println("Samples 3: Split a PDF document into multiple pages");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFDocument newDocument = CPDFDocument.createDocument(context);
            newDocument.importPages(document, new int[]{i}, 0);
            File file = new File(outputDir(), "pdfPage/CommonFivePage_Split_Page_" + (i + 1) + ".pdf");
            outputListener.println("Done. Result saved in \nCommonFivePage_Split_Page_" + (i + 1) + ".pdf");
            saveSamplePDF(newDocument, file, true);
        }
        document.close();
        outputListener.println("Done!");
        printDividingLine();
    }

    /**
     * Samples - 4: Merge split documents
     */
    private void mergePages() {
        outputListener.println("Samples 4: Merge split documents");
        int pageNum = 5;
        CPDFDocument document = CPDFDocument.createDocument(context);
        for (int i = 0; i < pageNum; i++) {
            File file = new File(outputDir(), "pdfPage/CommonFivePage_Split_Page_" + (i + 1) + ".pdf");
            if (file.exists()) {
                outputListener.println("Opening " + file.getName());
                CPDFDocument newDocument = new CPDFDocument(context);
                newDocument.open(file.getAbsolutePath());
                document.importPages(newDocument, new int[]{0}, i);
            }
        }
        File file = new File(outputDir(), "pdfPage/Merge_Pages.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in\nMerge_Pages.pdf");
        printDividingLine();
    }

    /**
     * Samples - 5: Delete the specified page of the document
     */
    private void deletePages() {
        outputListener.println("Samples 5: Delete the specified page of the document");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        int[] evenNumbers = getEvenNumbers(1, document.getPageCount() - 1);
        document.removePages(evenNumbers);
        File file = new File(outputDir(), "pdfPage/Remove_Page.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in\nRemove_Page.pdf");
        printDividingLine();
    }

    public static int[] getEvenNumbers(int start, int end) {
        int size = (end - start) / 2 + 1;
        int[] evenNumbers = new int[size];
        int index = 0;
        for (int i = start; i <= end; i++) {
            if (i % 2 != 0) {
                evenNumbers[index] = i;
                index++;
            }
        }
        return evenNumbers;
    }

    /**
     * Samples - 6: Rotate document pages
     */
    private void rotatePage() {
        outputListener.println("Samples 6: Rotate document pages");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        document.pageAtIndex(0).setRotation(90);
        File file = new File(outputDir(), "pdfPage/Rotate_Pages.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in\nRotate_Pages.pdf");
        printDividingLine();
    }

    /**
     * Samples - 7: Replace specified pages of example documentation with other documentation specified pages
     */
    private void replacePages() {
        outputListener.println("Samples 7: Replace specified pages of example documentation with other documentation specified pages");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        document.removePages(new int[]{1});
        //open second pdf Document
        CPDFDocument document2 = new CPDFDocument(context);
        document2.open(FileUtils.getAssetsTempFile(context, "text.pdf"));
        document.importPages(document2, new int[]{0}, 1);
        File file = new File(outputDir(), "pdfPage/Replace_Pages.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in\nReplace_Pages.pdf");
        printDividingLine();
    }

    /**
     * Samples - 8: Extract specific pages of a document
     */
    private void extractPages() {
        outputListener.println("Samples 8: Extract specific pages of a document");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFDocument newDocument = CPDFDocument.createDocument(context);
        newDocument.importPages(document, new int[]{1}, 0);
        File file = new File(outputDir(), "pdfPage/ExtractPages.pdf");
        outputListener.println("Done. Result saved in \nCommonFivePage_Extract_Page_1.pdf");
        saveSamplePDF(newDocument, file, true);
        document.close();
        printDividingLine();
    }
}
