/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.graphics.Color;

import com.compdfkit.core.document.CPDFBates;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class BatesTest extends PDFSamples {

    public BatesTest(){
        setTitle(R.string.bates_test_title);
        setDescription(R.string.bates_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        addCommonBates();
        editDateBates();
        clearBates();
        printFooter();
    }

    /**
     * Samples 1 : Add bates
     */
    private void addCommonBates(){
        printDividingLine();
        outputListener.println("Samples 1 : Add Bates");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFBates bates = document.getBates();
        int num = 6;
        for (int i = 0; i < num; i++) {
            outputListener.println("\nText : <<#3#5#Prefix-#-Suffix>>");
            bates.setText(i, "<<#3#5#Prefix-#-Suffix>>");
            bates.setTextColor(i, Color.RED);
            bates.setFontSize(i, 14);
            if (i == 0) {
                outputListener.println("Location: Top Left");
            } else if (i == 1) {
                outputListener.println("Location: Top Middle");
            } else if (i == 2) {
                outputListener.println("Location: Top Right");
            } else if (i == 3) {
                outputListener.println("Location: Botton Left");
            } else if (i == 4) {
                outputListener.println("Location: Botton Middle");
            } else {
                outputListener.println("Location: Botton Right");
            }
        }
        bates.setPages("0,1,2,3,4");
        bates.update();
        File file = new File(outputDir(), "BatesTest/AddBatesTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Results saved in 1 AddBatesTest.pdf");
        printDividingLine();
    }

    /**
     * Samples 2: edit bates
     */
    private void editDateBates(){
        outputListener.println("Samples 2 : edit bates");
        CPDFDocument document = new CPDFDocument(context);
        File file = new File(outputDir(), "BatesTest/AddBatesTest.pdf");
        document.open(file.getAbsolutePath());
        CPDFBates bates = document.getBates();
        outputListener.println("Get old bates 0 succeeded, text is " + bates.getText(0));
        outputListener.println("Change bates 0 succeeded, new text is <<#3#1#ComPDFKit-#-ComPDFKit>>");
        bates.setText(0, "<<#3#1#ComPDFKit-#-ComPDFKit>>");
        bates.update();
        File resultsFile = new File(outputDir(), "BatesTest/EditBatesTest.pdf");
        saveSamplePDF(document, resultsFile, false);
        outputListener.println("Done. Results saved in EditBatesTest.pdf");
        printDividingLine();
    }

    /**
     * samples 3 : clear bates
     */
    private void clearBates(){
        outputListener.println("Samples 3 : clear bates");
        CPDFDocument document = new CPDFDocument(context);
        File file = new File(outputDir(), "BatesTest/AddBatesTest.pdf");
        document.open(file.getAbsolutePath());
        CPDFBates bates = document.getBates();
        bates.clear();
        File resultsFile = new File(outputDir(), "BatesTest/ClearBatesTest.pdf");
        saveSamplePDF(document, resultsFile, false);
        outputListener.println("Done. Results saved in ClearBatesTest.pdf");
        printDividingLine();
    }
}
