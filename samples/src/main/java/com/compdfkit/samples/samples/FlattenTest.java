/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class FlattenTest extends PDFSamples {

    public FlattenTest(){
        setTitle(R.string.flatten_test_title);
        setDescription(R.string.flatten_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        outputListener.println();
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "Annotations.pdf"));
        // Get the total number of comments in the pdf document
        int annotationCount = 0;
        for (int i = 0; i < document.getPageCount(); i++) {
            annotationCount += document.pageAtIndex(i).getAnnotCount();
        }
        outputListener.println(annotationCount + " annotations in the file.");
        // Flatten processing of pdf pages
        document.flattenAllPages(CPDFPage.PDFFlattenOption.FLAT_NORMALDISPLAY);
        // save document
        File file = new File(outputDir(),
                "Flatten/FlattenTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in FlattenTest.pdf");

        CPDFDocument newFileDocument = new CPDFDocument(context);
        newFileDocument.open(file.getAbsolutePath());
        //Open the document again and get the number of annotations in the document
        annotationCount = 0;
        for (int i = 0; i < newFileDocument.getPageCount(); i++) {
            annotationCount += newFileDocument.pageAtIndex(i).getAnnotCount();
        }
        newFileDocument.close();
        outputListener.println(annotationCount + " annotations in the new file.");

        printDividingLine();
        printFooter();
    }
}
