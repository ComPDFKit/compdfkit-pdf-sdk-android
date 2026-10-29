/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
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


/**
 * This sample shows how to convert a common PDF document to the PDF standards
 * supported by ComPDFKit : PDF/A, PDF/X, PDF/E, PDF/VT and PDF/UA.
 */
public class PDFATest extends PDFSamples {

    /**
     * All the document types {@link CPDFDocument#convertType(CPDFDocument.PDFDocumentType)} can
     * convert to. {@link CPDFDocument.PDFDocumentType#PDFType} is the plain PDF type and is not
     * a conversion target, so it is not listed here.
     */
    private static final ConvertItem[] CONVERT_ITEMS = {
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA1a, "PDF/A-1a", "PDFA1aTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA1b, "PDF/A-1b", "PDFA1bTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA2a, "PDF/A-2a", "PDFA2aTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA2u, "PDF/A-2u", "PDFA2uTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA2b, "PDF/A-2b", "PDFA2bTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA3a, "PDF/A-3a", "PDFA3aTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA3u, "PDF/A-3u", "PDFA3uTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeA3b, "PDF/A-3b", "PDFA3bTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeX1a, "PDF/X-1a", "PDFX1aTest.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeX3, "PDF/X-3", "PDFX3Test.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeX4, "PDF/X-4", "PDFX4Test.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeE1, "PDF/E-1", "PDFE1Test.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeE2, "PDF/E-2", "PDFE2Test.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeVT1, "PDF/VT-1", "PDFVT1Test.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeVT2, "PDF/VT-2", "PDFVT2Test.pdf"),
            new ConvertItem(CPDFDocument.PDFDocumentType.PDFTypeUA1, "PDF/UA-1", "PDFUA1Test.pdf"),
    };

    public PDFATest(){
        setTitle(R.string.pdf_a_test_title);
        setDescription(R.string.pdf_a_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        for (int i = 0; i < CONVERT_ITEMS.length; i++) {
            convertTo(i + 1, CONVERT_ITEMS[i]);
        }
        printFooter();
    }

    /**
     * Convert the sample document to the given PDF standard and save it as a new file.
     * Not every standard accepts every source document, so the result of
     * {@link CPDFDocument#convertType(CPDFDocument.PDFDocumentType)} is checked before saving.
     */
    private void convertTo(int index, ConvertItem item) {
        printDividingLine();
        outputListener.println("Samples " + index + " : convert to " + item.label);
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        if (!document.convertType(item.type)) {
            outputListener.println("Convert to " + item.label + " failed");
            document.close();
            return;
        }
        File file = new File(outputDir(), "PDFATest/" + item.fileName);
        saveSamplePDF(document, file, true);
        outputListener.println("Convert to " + item.label + " done");
        outputListener.println("Done. Result saved in " + item.fileName);

        CPDFDocument savedDocument = new CPDFDocument(context);
        CPDFDocument.PDFDocumentError openResult = savedDocument.open(file.getAbsolutePath());
        if (openResult != CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
            outputListener.println("Failed to reopen the saved file: " + openResult);
            savedDocument.close();
            return;
        }
        CPDFDocument.PDFDocumentType savedType = savedDocument.getType();
        outputListener.println("Saved document type: " + savedType.name());
        outputListener.println("Matches selected type: " + (savedType == item.type));
        savedDocument.close();
    }

    private static class ConvertItem {

        final CPDFDocument.PDFDocumentType type;

        final String label;

        final String fileName;

        ConvertItem(CPDFDocument.PDFDocumentType type, String label, String fileName) {
            this.type = type;
            this.label = label;
            this.fileName = fileName;
        }
    }
}
