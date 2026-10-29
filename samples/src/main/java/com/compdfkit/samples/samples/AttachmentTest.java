/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import com.compdfkit.core.attachment.CPDFAttachment;
import com.compdfkit.core.attachment.CPDFAttachmentInfo;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Demonstrates the PDF attachment (embedded file) API.
 *
 * <p>The sample embeds two files from assets into {@code CommonFivePage.pdf},
 * lists them, reads and exports one of them, edits an attachment in place,
 * removes one, and finally reopens the saved document to prove the attachments
 * were persisted.
 *
 * <p>Attachment changes are made in memory; a save is required to persist them.
 */
public class AttachmentTest extends PDFSamples {

    /** Document produced by the first section and consumed by the later ones. */
    private File withAttachmentsFile;

    public AttachmentTest() {
        setTitle(R.string.attachment_test_title);
        setDescription(R.string.attachment_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        addAttachments();
        if (withAttachmentsFile == null) {
            outputListener.println("Failed to prepare the sample document, aborting.");
            printFooter();
            return;
        }
        listAttachments();
        readAndExportAttachment();
        modifyAttachment();
        removeAttachment();
        reopenAndVerify();
        addMissingSourceFile();
        printFooter();
    }

    // ------------------------------------------------------------------
    // Samples 1 : add
    // ------------------------------------------------------------------

    /**
     * Embeds two files. {@code addAttachment} returns a handle that must be
     * closed before the document; the description is optional metadata shown by
     * most PDF readers next to the file name.
     */
    private void addAttachments() {
        outputListener.println();
        outputListener.println("Samples 1 : Add attachments to a document");

        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        if (!document.isValid()) {
            outputListener.println("Failed to open CommonFivePage.pdf");
            document.close();
            printDividingLine();
            return;
        }

        outputListener.println("Attachment count before : " + document.getAttachmentCount());

        addOne(document, FileUtils.getAssetsTempFile(context, "ComPDFKit.png"),
                "ComPDFKit.png", "The ComPDFKit logo");
        addOne(document, FileUtils.getAssetsTempFile(context, "Bird.wav"),
                "Bird.wav", "A short bird recording");

        outputListener.println("Attachment count after  : " + document.getAttachmentCount());

        File file = new File(outputDir(), "attachmentTest/WithAttachments.pdf");
        saveSamplePDF(document, file, true);
        if (file.exists()) {
            withAttachmentsFile = file;
            outputListener.println("Done. Result saved in WithAttachments.pdf");
        }
        printDividingLine();
    }

    /** Embeds one file and prints the outcome. */
    private void addOne(CPDFDocument document, String sourcePath, String name,
                        String description) {
        CPDFAttachment attachment = document.addAttachment(sourcePath, name);
        if (attachment == null) {
            outputListener.println("Failed to add " + name);
            return;
        }
        try {
            attachment.setDescription(description);
            outputListener.println("Added " + attachment.getName()
                    + " (index " + attachment.getIndex() + ")");
        } finally {
            attachment.close();
        }
    }

    // ------------------------------------------------------------------
    // Samples 2 : enumerate
    // ------------------------------------------------------------------

    /**
     * {@code getAttachmentList} returns metadata only and does not allocate a
     * native handle per attachment, so it is the cheap way to build a UI list.
     */
    private void listAttachments() {
        outputListener.println("Samples 2 : List the attachments");

        CPDFDocument document = openWithAttachments();
        if (document == null) {
            printDividingLine();
            return;
        }
        try {
            List<CPDFAttachmentInfo> infos = document.getAttachmentList();
            outputListener.println("Count : " + infos.size());
            outputListener.println("Note : attachments are stored in a PDF name tree, so the "
                    + "index follows the name order, not the order they were added.");
            for (CPDFAttachmentInfo info : infos) {
                outputListener.println("  [" + info.getIndex() + "] " + info.getName()
                        + "  mimeType=" + info.getMimeType()
                        + "  size=" + info.getSizeBytes() + " bytes"
                        + "  description=" + info.getDescription());
            }
        } finally {
            document.close();
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 3 : read / export
    // ------------------------------------------------------------------

    /**
     * The payload can be pulled into memory with {@code read()} or streamed
     * straight to disk with {@code saveTo}.
     */
    private void readAndExportAttachment() {
        outputListener.println("Samples 3 : Read and export an attachment");

        CPDFDocument document = openWithAttachments();
        if (document == null) {
            printDividingLine();
            return;
        }
        // Look the attachment up by name: the index is the name-tree position,
        // which is not necessarily the order the files were added in.
        int index = indexOf(document, "ComPDFKit.png");
        CPDFAttachment attachment = index < 0 ? null : document.getAttachment(index);
        if (attachment == null) {
            outputListener.println("ComPDFKit.png not found in the document.");
            document.close();
            printDividingLine();
            return;
        }
        try {
            byte[] data = attachment.read();
            outputListener.println("read() on " + attachment.getName()
                    + " (index " + index + ") : "
                    + (data == null ? "null" : data.length + " bytes"));

            File exported = new File(outputDir(),
                    "attachmentTest/exported_" + attachment.getName());
            exported.getParentFile().mkdirs();
            boolean saved = attachment.saveTo(exported);
            outputListener.println("saveTo : " + saved);
            if (saved && exported.exists()) {
                // Not a PDF, so register it manually to make it show up in Open Files.
                addFileList(exported.getAbsolutePath());
                outputListener.println("Exported to " + exported.getName()
                        + " (" + exported.length() + " bytes)");
            }
        } finally {
            attachment.close();
            document.close();
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 4 : modify
    // ------------------------------------------------------------------

    /**
     * Both the description and the payload of an existing attachment can be
     * replaced in place. The document still has to be saved afterwards.
     */
    private void modifyAttachment() {
        outputListener.println("Samples 4 : Modify an attachment in place");

        CPDFDocument document = openWithAttachments();
        if (document == null) {
            printDividingLine();
            return;
        }
        int index = indexOf(document, "Bird.wav");
        CPDFAttachment attachment = index < 0 ? null : document.getAttachment(index);
        if (attachment == null) {
            outputListener.println("Bird.wav not found in the document.");
            document.close();
            printDividingLine();
            return;
        }
        try {
            byte[] before = attachment.read();
            outputListener.println("Editing " + attachment.getName() + " at index " + index);
            outputListener.println("Before : description=" + attachment.getDescription()
                    + ", size=" + (before == null ? 0 : before.length) + " bytes");

            outputListener.println("setDescription : "
                    + attachment.setDescription("Replaced by AttachmentTest"));
            byte[] payload = "Replaced attachment payload."
                    .getBytes(Charset.forName("UTF-8"));
            outputListener.println("write : " + attachment.write(payload));

            byte[] after = attachment.read();
            outputListener.println("After  : description=" + attachment.getDescription()
                    + ", size=" + (after == null ? 0 : after.length) + " bytes");
        } finally {
            attachment.close();
        }
        File file = new File(outputDir(), "attachmentTest/ModifiedAttachment.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in ModifiedAttachment.pdf");
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 5 : remove
    // ------------------------------------------------------------------

    /**
     * Removal shifts the indices of every later attachment down by one, so when
     * deleting several always start from the highest index.
     */
    private void removeAttachment() {
        outputListener.println("Samples 5 : Remove an attachment");

        CPDFDocument document = openWithAttachments();
        if (document == null) {
            printDividingLine();
            return;
        }
        outputListener.println("Count before : " + document.getAttachmentCount());
        outputListener.println("removeAttachment(1) : " + document.removeAttachment(1));
        outputListener.println("Count after  : " + document.getAttachmentCount());

        File file = new File(outputDir(), "attachmentTest/RemovedAttachment.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in RemovedAttachment.pdf");
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Samples 6 : persistence
    // ------------------------------------------------------------------

    /**
     * Reopens the saved documents to confirm that the additions and the removal
     * really made it into the files.
     */
    private void reopenAndVerify() {
        outputListener.println("Samples 6 : Reopen the saved documents and verify");

        printCountOf("WithAttachments.pdf", 2);
        printCountOf("ModifiedAttachment.pdf", 2);
        printCountOf("RemovedAttachment.pdf", 1);

        printDividingLine();
    }

    /** Opens one output document and prints its attachment count. */
    private void printCountOf(String name, int expected) {
        File file = new File(outputDir(), "attachmentTest/" + name);
        if (!file.exists()) {
            outputListener.println(name + " : missing");
            return;
        }
        CPDFDocument document = new CPDFDocument(context);
        CPDFDocument.PDFDocumentError error = document.open(file.getAbsolutePath());
        if (error != CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
            outputListener.println(name + " : failed to open (" + error + ")");
            document.close();
            return;
        }
        try {
            outputListener.println(name + " : " + document.getAttachmentCount()
                    + " attachment(s), expected " + expected);
        } finally {
            document.close();
        }
    }

    // ------------------------------------------------------------------
    // Samples 7 : error case
    // ------------------------------------------------------------------

    /**
     * When the source file cannot be read {@code addAttachment} returns null and
     * — importantly — leaves the attachment count untouched. The underlying
     * pdfium call can leave an orphan entry behind on failure, which the JNI
     * layer rolls back.
     */
    private void addMissingSourceFile() {
        outputListener.println("Samples 7 : Add an attachment from a missing source file");

        CPDFDocument document = openWithAttachments();
        if (document == null) {
            printDividingLine();
            return;
        }
        try {
            int before = document.getAttachmentCount();
            CPDFAttachment attachment = document.addAttachment(
                    new File(outputDir(), "attachmentTest/does_not_exist.txt").getAbsolutePath(),
                    "does_not_exist.txt");
            int after = document.getAttachmentCount();
            outputListener.println("addAttachment returned : "
                    + (attachment == null ? "null (expected)" : "a handle (unexpected)"));
            outputListener.println("Count before : " + before + ", count after : " + after
                    + (before == after ? " (unchanged, as expected)" : " (LEAKED AN ENTRY)"));
            if (attachment != null) {
                attachment.close();
            }
        } finally {
            document.close();
        }
        printDividingLine();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Opens the document written by {@link #addAttachments()}.
     *
     * @return the opened document, or null when it could not be read.
     */
    /**
     * @return the index of the attachment with the given name, or -1 when the
     *         document does not contain it.
     */
    private int indexOf(CPDFDocument document, String name) {
        for (CPDFAttachmentInfo info : document.getAttachmentList()) {
            if (name.equals(info.getName())) {
                return info.getIndex();
            }
        }
        return -1;
    }

    private CPDFDocument openWithAttachments() {
        CPDFDocument document = new CPDFDocument(context);
        CPDFDocument.PDFDocumentError error =
                document.open(withAttachmentsFile.getAbsolutePath());
        if (error != CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess) {
            outputListener.println("Failed to open " + withAttachmentsFile.getName()
                    + " : " + error);
            document.close();
            return null;
        }
        return document;
    }
}
