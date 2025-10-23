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
import android.graphics.RectF;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFRedactAnnotation;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class PDFRedactTest extends PDFSamples {

    public PDFRedactTest() {
        setTitle(R.string.redact_test_title);
        setDescription(R.string.redact_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        outputListener.println();
        outputListener.println("The text need to be redact is : Page1");
        outputListener.println("Text in the redacted area is :");

        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFPage pdfPage = document.pageAtIndex(0);
        CPDFRedactAnnotation redactAnnotation = (CPDFRedactAnnotation) pdfPage.addAnnot(CPDFAnnotation.Type.REDACT);
        RectF pageSize = pdfPage.getSize();
        RectF insertRect = new RectF(300F, 240F, 400F, 320F);
        //coordinate conversion
        insertRect = pdfPage.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect);

        redactAnnotation.setRect(insertRect);
        CPDFTextAttribute textAttribute = new CPDFTextAttribute(
                CPDFTextAttribute.FontNameHelper.obtainFontName(CPDFTextAttribute.FontNameHelper.FontType.Helvetica, false, false), 14, Color.YELLOW);
        redactAnnotation.setTextDa(textAttribute);
        redactAnnotation.setFillColor(Color.RED);
        redactAnnotation.setOverLayText("REDACTED");
        redactAnnotation.updateAp();
        redactAnnotation.applyRedaction();
        File resultsFile = new File(outputDir(), "RedactTest/RedactTest.pdf");
        saveSamplePDF(document, resultsFile, false);
        outputListener.println("Done. Results saved in RedactTest.pdf");
        printDividingLine();
        printFooter();
    }
}
