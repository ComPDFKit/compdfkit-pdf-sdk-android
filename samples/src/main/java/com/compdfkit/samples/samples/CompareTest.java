/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.graphics.Color;
import android.graphics.RectF;

import com.compdfkit.core.compare.CPDFCompare;
import com.compdfkit.core.compare.CPDFCompareDrawings;
import com.compdfkit.core.compare.CPDFCompareResult;
import com.compdfkit.core.compare.CPDFCompareResults;
import com.compdfkit.core.document.CPDFAbility;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.edit.CPDFEditCharItem;
import com.compdfkit.core.edit.CPDFEditPage;
import com.compdfkit.core.edit.CPDFEditTextArea;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;
import java.util.List;

/**
 * Demonstrates the document comparison API.
 *
 * <p>Comparison needs two documents that actually differ. Instead of shipping a
 * pair of binary fixtures this sample builds the "modified" document at runtime:
 * it takes {@code CommonFivePage.pdf} and injects a marker sentence on the first
 * page through the content editor. That keeps the difference known and easy to
 * verify against the reported results.
 */
public class CompareTest extends PDFSamples {

    /** Text injected into the modified document; the compare result should point at it. */
    private static final String MARKER_TEXT = "COMPARE DEMO MARKER";

    /** Base document, written out unchanged so it can be inspected in Open Files. */
    private File originalFile;

    /** Copy of the base document carrying {@link #MARKER_TEXT} on page 0. */
    private File modifiedFile;

    public CompareTest() {
        setTitle(R.string.compare_test_title);
        setDescription(R.string.compare_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        checkLicenseAbility();
        prepareDocuments();
        if (originalFile == null || modifiedFile == null) {
            outputListener.println("Failed to prepare the sample documents, aborting.");
            printFooter();
            return;
        }
        compareTextOnly();
        compareWithCombinedMask();
        comparePathAndShading();
        compareAndGenerateHighlightedDoc();
        compareDrawingsOverlay();
        compareIdenticalDocuments();
        printFooter();
    }

    // ------------------------------------------------------------------
    // Samples 0 : license
    // ------------------------------------------------------------------

    /**
     * Comparison is a licensed feature. When the key in use does not carry the
     * COMPARE permission every call below returns empty results instead of
     * failing loudly, so it is worth printing the state up front.
     */
    private void checkLicenseAbility() {
        outputListener.println();
        outputListener.println("Samples 0 : Check the COMPARE license ability");
        boolean allowed = CPDFAbility.checkAbility(CPDFAbility.Ability.COMPARE);
        outputListener.println("COMPARE ability : " + (allowed ? "Allowed" : "Not Allowed"));
        if (!allowed) {
            outputListener.println("The current license does not include comparison; "
                    + "the results below will be empty.");
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 1 : test material
    // ------------------------------------------------------------------

    /**
     * Writes the base document and a modified copy of it into the output
     * directory. Both end up in the Open Files list so the injected difference
     * can be checked by eye.
     */
    private void prepareDocuments() {
        outputListener.println("Samples 1 : Prepare two documents that differ");

        File dir = new File(outputDir(), "compareTest");
        File original = new File(dir, "Original.pdf");
        File modified = new File(dir, "Modified.pdf");

        // The untouched base document.
        CPDFDocument baseDocument = new CPDFDocument(context);
        baseDocument.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        saveSamplePDF(baseDocument, original, true);

        // The same document with one extra text area on the first page.
        CPDFDocument editedDocument = new CPDFDocument(context);
        editedDocument.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        boolean injected = injectMarkerText(editedDocument);
        saveSamplePDF(editedDocument, modified, true);

        if (!original.exists() || !modified.exists()) {
            printDividingLine();
            return;
        }
        originalFile = original;
        modifiedFile = modified;

        outputListener.println("Base     : " + original.getName());
        outputListener.println("Modified : " + modified.getName()
                + (injected ? " (injected \"" + MARKER_TEXT + "\" on page 1)"
                            : " (text injection failed)"));
        printDividingLine();
    }

    /**
     * Adds a text area carrying {@link #MARKER_TEXT} to the first page.
     *
     * @return true when the content editor accepted the insertion.
     */
    private boolean injectMarkerText(CPDFDocument document) {
        CPDFPage page = document.pageAtIndex(0);
        if (page == null || !page.isValid()) {
            return false;
        }
        CPDFEditPage editPage = page.getEditPage(false);
        if (editPage == null) {
            return false;
        }
        editPage.beginEdit(CPDFEditPage.LoadTextImage);
        if (!editPage.isValid()) {
            return false;
        }

        // Place the marker near the top-left corner of the page. Page space has
        // its origin at the bottom-left, hence top > bottom in the rectangle.
        RectF pageSize = page.getSize();
        float left = 40F;
        float top = pageSize.height() - 40F;
        RectF rect = new RectF(left, top, left + 260F, top - 40F);

        CPDFEditTextArea textArea = editPage.createNewTextArea(rect, "Helvetica", 18,
                Color.RED, 255, false, false,
                CPDFEditTextArea.PDFEditAlignType.PDFEditAlignLeft);
        if (textArea == null) {
            editPage.endEdit();
            return false;
        }
        CPDFEditCharItem begin = textArea.getBeginCharPlace();
        CPDFEditCharItem end = textArea.getEndCharPlace();
        textArea.insertTextRange(begin.getPlace(), end.getPlace(), MARKER_TEXT);
        editPage.endEdit();
        return true;
    }

    // ------------------------------------------------------------------
    // Samples 2 : text only
    // ------------------------------------------------------------------

    /**
     * The simplest comparison: one page pair, text differences only, without
     * drawing any highlight annotation.
     */
    private void compareTextOnly() {
        outputListener.println("Samples 2 : Compare page 1 of both documents, text only");

        CPDFDocument base = openOutput(originalFile);
        CPDFDocument compared = openOutput(modifiedFile);
        if (base == null || compared == null) {
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        CPDFCompare compare = base.createCompare(compared);
        if (compare == null) {
            outputListener.println("createCompare returned null.");
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        try {
            outputListener.println("Max page count : " + compare.getMaxPagesCount());
            CPDFCompareResults results = compare.doCompare(0, 0,
                    CPDFCompare.CompType.ONLY_TEXT, false);
            printResults(results);
            outputListener.println("Accumulated totals -> delete: " + compare.getTotalDeleteCount()
                    + ", insert: " + compare.getTotalInsertCount()
                    + ", replace: " + compare.getTotalReplaceCount()
                    + ", modify: " + compare.getTotalModifyCount());
        } finally {
            compare.close();
            closeQuietly(base, compared);
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 3 : bit mask semantics
    // ------------------------------------------------------------------

    /**
     * {@code CompType} is a <b>bit mask</b>, not a sequential enum: the values
     * are 0x1 / 0x2 / 0x4 / 0x8 / 0x10 and can be combined with {@code |}.
     * Passing 0 selects nothing and is rejected.
     */
    private void compareWithCombinedMask() {
        outputListener.println("Samples 3 : CompType is a bit mask");
        for (CPDFCompare.CompType type : CPDFCompare.CompType.values()) {
            outputListener.println("  " + type.name() + " = 0x"
                    + Integer.toHexString(type.getId()));
        }
        outputListener.println("  everything() = 0x"
                + Integer.toHexString(CPDFCompare.CompType.everything()));
        outputListener.println("Note : COMPARE_ALL is 0x7 (text|image|annot) and does NOT "
                + "include path or shading. Use everything() for all five targets.");

        CPDFDocument base = openOutput(originalFile);
        CPDFDocument compared = openOutput(modifiedFile);
        if (base == null || compared == null) {
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        CPDFCompare compare = base.createCompare(compared);
        if (compare == null) {
            outputListener.println("createCompare returned null.");
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        try {
            int mask = CPDFCompare.CompType.ONLY_TEXT.getId()
                    | CPDFCompare.CompType.ONLY_IMAGE.getId()
                    | CPDFCompare.CompType.ONLY_ANNOT.getId();
            outputListener.println("Comparing with mask 0x" + Integer.toHexString(mask)
                    + " (TEXT | IMAGE | ANNOT)");
            printResults(compare.doCompare(0, 0, mask, false));

            // An empty mask would silently compare nothing, so it is rejected.
            try {
                compare.doCompare(0, 0, 0, false);
                outputListener.println("Unexpected : an empty mask was accepted.");
            } catch (IllegalArgumentException e) {
                outputListener.println("Empty mask rejected as expected : " + e.getMessage());
            }
        } finally {
            compare.close();
            closeQuietly(base, compared);
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 4 : path / shading
    // ------------------------------------------------------------------

    /**
     * Vector paths and shadings are separate targets with their own result
     * lists. They are only filled in when their bits are part of the mask.
     */
    private void comparePathAndShading() {
        outputListener.println("Samples 4 : Compare vector paths and shadings");

        CPDFDocument base = openOutput(originalFile);
        CPDFDocument compared = openOutput(modifiedFile);
        if (base == null || compared == null) {
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        CPDFCompare compare = base.createCompare(compared);
        if (compare == null) {
            outputListener.println("createCompare returned null.");
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        try {
            CPDFCompareResults results = compare.doCompare(0, 0,
                    CPDFCompare.CompType.everything(), false);
            printResults(results);
            if (results != null) {
                printResultList("path", results.getPathResults());
                printResultList("shading", results.getShadingResults());
                if (results.getPathResults().isEmpty() && results.getShadingResults().isEmpty()) {
                    outputListener.println("No path or shading difference in this document pair; "
                            + "the lists are still returned (never null).");
                }
            }
        } finally {
            compare.close();
            closeQuietly(base, compared);
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 5 : highlights + result document
    // ------------------------------------------------------------------

    /**
     * With {@code drawHighlight = true} the differences are turned into
     * highlight annotations on the in-memory pages, which
     * {@link CPDFCompare#generateNewDoc(String)} then writes out. The colour of
     * each difference kind can be customised beforehand.
     */
    private void compareAndGenerateHighlightedDoc() {
        outputListener.println("Samples 5 : Custom highlight colours and result document");

        CPDFDocument base = openOutput(originalFile);
        CPDFDocument compared = openOutput(modifiedFile);
        if (base == null || compared == null) {
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        CPDFCompare compare = base.createCompare(compared);
        if (compare == null) {
            outputListener.println("createCompare returned null.");
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        try {
            // Only the RGB channels of the colour are used; the alpha is set
            // separately through the transparency setters (0..255).
            outputListener.println("setInsertResColor(GREEN)   : "
                    + compare.setInsertResColor(Color.GREEN));
            outputListener.println("setDeleteResColor(RED)     : "
                    + compare.setDeleteResColor(Color.RED));
            outputListener.println("setReplaceResColor(BLUE)   : "
                    + compare.setReplaceResColor(Color.BLUE));
            outputListener.println("setChangeResColor(YELLOW)  : "
                    + compare.setChangeResColor(Color.YELLOW));
            outputListener.println("setInsertResTransparency   : "
                    + compare.setInsertResTransparency(128));
            outputListener.println("setDeleteResTransparency   : "
                    + compare.setDeleteResTransparency(128));
            outputListener.println("setReplaceResTransparency  : "
                    + compare.setReplaceResTransparency(128));
            outputListener.println("setChangeResTransparency   : "
                    + compare.setChangeResTransparency(128));

            // Compare every page pair so the generated document is complete.
            int pageCount = compare.getMaxPagesCount();
            for (int i = 0; i < pageCount; i++) {
                compare.doCompare(i, i, CPDFCompare.CompType.COMPARE_ALL, true);
            }
            outputListener.println("Compared " + pageCount + " page pair(s) with highlights.");

            File result = new File(outputDir(), "compareTest/CompareResult.pdf");
            result.getParentFile().mkdirs();
            boolean generated = compare.generateNewDoc(result.getAbsolutePath());
            outputListener.println("generateNewDoc : " + generated);
            if (generated && result.exists()) {
                addFileList(result.getAbsolutePath());
                outputListener.println("Done. Result saved in CompareResult.pdf");
            }
        } finally {
            compare.close();
            closeQuietly(base, compared);
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 6 : overlay comparison
    // ------------------------------------------------------------------

    /**
     * Overlay comparison paints both documents on top of each other with
     * different colours and a blend mode, which suits engineering drawings
     * better than the per-difference highlight above.
     */
    private void compareDrawingsOverlay() {
        outputListener.println("Samples 6 : Overlay comparison (CPDFCompareDrawings)");

        CPDFDocument base = openOutput(originalFile);
        CPDFDocument compared = openOutput(modifiedFile);
        if (base == null || compared == null) {
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        CPDFCompareDrawings drawings = base.createCompareDrawings(compared);
        if (drawings == null) {
            outputListener.println("createCompareDrawings returned null.");
            closeQuietly(base, compared);
            printDividingLine();
            return;
        }
        try {
            outputListener.println("setColorCompare(BLUE)      : "
                    + drawings.setColorCompare(Color.BLUE));
            outputListener.println("setColorCompared(RED)      : "
                    + drawings.setColorCompared(Color.RED));
            outputListener.println("setFillAlphaCompare(0.5)   : "
                    + drawings.setFillAlphaCompare(0.5F));
            outputListener.println("setFillAlphaCompared(0.5)  : "
                    + drawings.setFillAlphaCompared(0.5F));
            outputListener.println("setStrokeAlphaCompare(1.0) : "
                    + drawings.setStrokeAlphaCompare(1.0F));
            outputListener.println("setStrokeAlphaCompared(1.0): "
                    + drawings.setStrokeAlphaCompared(1.0F));
            outputListener.println("setBlendMode(MULTIPLY)     : "
                    + drawings.setBlendMode(CPDFCompareDrawings.BlendMode.MULTIPLY));

            boolean done = drawings.compareContent();
            outputListener.println("compareContent : " + done);
            if (!done) {
                return;
            }
            // The generated document must be closed before the session.
            CPDFDocument generated = drawings.getDocGenerate(context);
            if (generated == null) {
                outputListener.println("getDocGenerate returned null.");
                return;
            }
            File overlay = new File(outputDir(), "compareTest/OverlayResult.pdf");
            saveSamplePDF(generated, overlay, true);
            outputListener.println("Done. Result saved in OverlayResult.pdf");
        } finally {
            drawings.close();
            closeQuietly(base, compared);
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 7 : sanity check
    // ------------------------------------------------------------------

    /**
     * Comparing a document against an identical copy of itself must report no
     * differences at all.
     */
    private void compareIdenticalDocuments() {
        outputListener.println("Samples 7 : Compare a document against an identical copy");

        CPDFDocument base = openOutput(originalFile);
        CPDFDocument copy = openOutput(originalFile);
        if (base == null || copy == null) {
            closeQuietly(base, copy);
            printDividingLine();
            return;
        }
        CPDFCompare compare = base.createCompare(copy);
        if (compare == null) {
            outputListener.println("createCompare returned null.");
            closeQuietly(base, copy);
            printDividingLine();
            return;
        }
        try {
            CPDFCompareResults results = compare.doCompare(0, 0,
                    CPDFCompare.CompType.everything(), false);
            printResults(results);
            outputListener.println("isEmpty : " + (results != null && results.isEmpty())
                    + " (expected true)");
        } finally {
            compare.close();
            closeQuietly(base, copy);
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Opens one of the documents written by {@link #prepareDocuments()}.
     *
     * @return the opened document, or null when it could not be read.
     */
    private CPDFDocument openOutput(File file) {
        CPDFDocument document = new CPDFDocument(context);
        CPDFDocument.PDFDocumentError error = document.open(file.getAbsolutePath());
        if (error != CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
            outputListener.println("Failed to open " + file.getName() + " : " + error);
            document.close();
            return null;
        }
        return document;
    }

    /** Closes every non-null document. */
    private void closeQuietly(CPDFDocument... documents) {
        for (CPDFDocument document : documents) {
            if (document != null) {
                document.close();
            }
        }
    }

    /** Prints the counts and every non-empty result list of one comparison. */
    private void printResults(CPDFCompareResults results) {
        if (results == null) {
            outputListener.println("doCompare returned null.");
            return;
        }
        outputListener.println("Counts -> delete: " + results.getDeleteCount()
                + ", insert: " + results.getInsertCount()
                + ", replace: " + results.getReplaceCount()
                + ", annotInsert: " + results.getAnnotInsertCount()
                + ", annotModified: " + results.getAnnotModifiedCount()
                + ", annotDeleted: " + results.getAnnotDeletedCount());
        printResultList("text", results.getTextResults());
        printResultList("image", results.getImageResults());
        printResultList("annot", results.getAnnotResults());
    }

    /** Prints one result list, one line per difference. */
    private void printResultList(String label, List<CPDFCompareResult> results) {
        if (results == null || results.isEmpty()) {
            return;
        }
        outputListener.println(label + " results : " + results.size());
        for (int i = 0; i < results.size(); i++) {
            CPDFCompareResult result = results.get(i);
            outputListener.println("  [" + i + "] " + result.getResultTypeEnum()
                    + "  oldPage=" + result.oldPageIndex
                    + "  compPage=" + result.compPageIndex
                    + "  oldRect=" + result.oldRect
                    + "  compRect=" + result.compRect);
        }
    }
}
