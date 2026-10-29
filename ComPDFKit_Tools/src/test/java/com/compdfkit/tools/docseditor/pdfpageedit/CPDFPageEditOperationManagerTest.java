/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.docseditor.pdfpageedit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class CPDFPageEditOperationManagerTest {

    @Test
    public void buildExtractFileNameUsesSourceNameAndPageCount() {
        assertEquals("Report_Extract_1Pages.pdf",
                CPDFPageEditOperationManager.buildExtractFileName("Report.pdf", 1));
        assertEquals("Report_Extract_3Pages.pdf",
                CPDFPageEditOperationManager.buildExtractFileName("Report.pdf", 3));
    }

    @Test
    public void buildExtractFileNameDoesNotListEverySelectedPage() {
        String fileName = CPDFPageEditOperationManager.buildExtractFileName("Report.pdf", 500);

        assertEquals("Report_Extract_500Pages.pdf", fileName);
        assertFalse(fileName.contains(","));
        assertTrue(fileName.getBytes(StandardCharsets.UTF_8).length <= 240);
    }

    @Test
    public void buildExtractFileNameTruncatesLongUtf8BaseNameAtCharacterBoundary() {
        StringBuilder sourceName = new StringBuilder();
        for (int index = 0; index < 100; index++) {
            sourceName.append("页面");
        }
        sourceName.append(".pdf");

        String fileName = CPDFPageEditOperationManager.buildExtractFileName(sourceName.toString(), 500);
        String baseName = fileName.substring(0, fileName.indexOf("_Extract_"));

        assertTrue(fileName.getBytes(StandardCharsets.UTF_8).length <= 240);
        assertTrue(fileName.endsWith("_Extract_500Pages.pdf"));
        assertTrue(baseName.matches("(页面)+"));
    }

    @Test
    public void buildExtractFileNameHandlesMissingAndMultipleExtensions() {
        assertEquals("Report_Extract_2Pages.pdf",
                CPDFPageEditOperationManager.buildExtractFileName("Report", 2));
        assertEquals("Report.v2_Extract_2Pages.pdf",
                CPDFPageEditOperationManager.buildExtractFileName("Report.v2.pdf", 2));
        assertEquals("Extract_2Pages.pdf",
                CPDFPageEditOperationManager.buildExtractFileName(null, 2));
        assertEquals("Extract_2Pages.pdf",
                CPDFPageEditOperationManager.buildExtractFileName("", 2));
    }
}
