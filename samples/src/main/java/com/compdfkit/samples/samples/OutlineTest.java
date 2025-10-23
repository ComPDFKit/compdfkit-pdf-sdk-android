/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;


import com.compdfkit.core.document.CPDFDestination;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFOutline;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;

public class OutlineTest extends PDFSamples {

    public OutlineTest(){
        setTitle(R.string.outline_test_title);
        setDescription(R.string.outline_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        createOutline();
        printOutline();
        printFooter();
    }

    /**
     * Sample 1 : create outline test
     */
    private void createOutline(){
        outputListener.println();
        // Open test pdf document
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFOutline rootOutline;
        // Check if there is a root node outline
        if (document.getOutlineRoot() == null) {
            rootOutline = document.newOutlineRoot();
        }else {
            rootOutline = document.getOutlineRoot();
        }
        // add outline data
        CPDFOutline child = insertOutline(rootOutline, "1. page1", 0);
        // Add sub-outline data for first page outline
        insertOutline(child, "1.1 page1_1", 0);
        insertOutline(rootOutline, "2. page2", 1);
        insertOutline(rootOutline, "3. page3", 2);
        insertOutline(rootOutline, "4. page4", 3);
        insertOutline(rootOutline, "5. page5", 4);
        File file = new File(outputDir(), "OutlineTest/CreateOutlineTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done.");
        outputListener.println("Done. Results saved in CreateOutlineTest.pdf");
    }

    /**
     * insert outline data
     * @param rootOutline parent Outline
     * @param title outline title
     * @param pageIndex jump document page index
     * @return CPDFOutline
     */
    private CPDFOutline insertOutline(CPDFOutline rootOutline, String title, int pageIndex){
        CPDFOutline child = rootOutline.insertChildAtIndex(pageIndex);
        child.setTitle(title);
        CPDFDestination destination = new CPDFDestination(pageIndex, 0,0, 1F);
        child.setDestination(destination);
        return child;
    }

    private void printOutline(){
        outputListener.println();
        CPDFDocument document = new CPDFDocument(context);
        File sampleFile = new File(outputDir(), "OutlineTest/CreateOutlineTest.pdf");
        document.open(sampleFile.getAbsolutePath());
        CPDFOutline outlineRoot = document.getOutlineRoot();
        if (outlineRoot != null) {
            printChildOutline(outlineRoot.getChildList());
        }
    }

    private void printChildOutline(CPDFOutline[] outlines){
        for (CPDFOutline outline : outlines) {
            StringBuilder tab = new StringBuilder();
            if (outline.getLevel() > 1) {
                for (int i = 0; i < outline.getLevel(); i++) {
                    tab.append("\t");
                }
            }
            outputListener.println(tab + "->" + outline.getTitle());
            if (outline.getChildList() != null && outline.getChildList().length > 0){
                printChildOutline(outline.getChildList());
            }
        }
    }
}
