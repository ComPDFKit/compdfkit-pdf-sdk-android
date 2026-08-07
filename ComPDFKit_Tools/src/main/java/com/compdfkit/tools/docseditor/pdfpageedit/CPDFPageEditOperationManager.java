/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.docseditor.pdfpageedit;

import android.content.Context;
import android.net.Uri;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.common.utils.CUriUtil;
import com.compdfkit.tools.common.utils.glide.CPDFThumbnailCacheRevisionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes PDF page editing operations and returns UI-neutral results.
 *
 * <p>This class contains no Fragment, RecyclerView, toolbar, or Toast logic.</p>
 */
class CPDFPageEditOperationManager {

    /**
     * Imports all pages from the source document and removes the pages being replaced.
     *
     * @param targetDocument the document currently displayed in the editor.
     * @param sourceDocument the document selected as replacement content.
     * @param replacedPages zero-based page indexes selected for replacement.
     * @return selected indexes for the newly inserted pages, or null when replacement fails.
     */
    int[] replacePages(CPDFDocument targetDocument, CPDFDocument sourceDocument, int[] replacedPages) {
        if (targetDocument == null || sourceDocument == null || isEmpty(replacedPages)) {
            return null;
        }

        int[] sourcePages = buildSequentialPages(sourceDocument.getPageCount());
        int insertIndex = replacedPages[replacedPages.length - 1] + 1;
        if (!targetDocument.importPages(sourceDocument, sourcePages, insertIndex)) {
            return null;
        }
        if (!targetDocument.removePages(replacedPages)) {
            return null;
        }

        CPDFThumbnailCacheRevisionManager.bumpRevision(targetDocument);
        return buildReplacementSelection(replacedPages, sourcePages.length);
    }

    /**
     * Exports selected pages into a new PDF file in the requested public directory.
     *
     * @param context a Context used to create the destination Uri.
     * @param sourceDocument the document that provides selected pages.
     * @param selectedPages zero-based page indexes to export.
     * @param publicDirectory destination directory relative to public storage.
     * @param saveExtraFontSubset whether extra font subsets should be embedded during save.
     * @return the saved file Uri, or null when export fails.
     */
    Uri extractPages(Context context, CPDFDocument sourceDocument, int[] selectedPages,
                     String publicDirectory, boolean saveExtraFontSubset) {
        if (context == null || sourceDocument == null || isEmpty(selectedPages)) {
            return null;
        }

        Uri saveUri = CUriUtil.createFileUri(context, publicDirectory,
                buildExtractFileName(sourceDocument, selectedPages), "application/pdf");
        if (saveUri == null) {
            return null;
        }

        CPDFDocument newDocument = CPDFDocument.createDocument(context);
        boolean saved = newDocument.importPages(sourceDocument, selectedPages, 0);
        try {
            saved &= newDocument.saveAs(saveUri, false, saveExtraFontSubset);
        } catch (Exception ignored) {
            saved = false;
        }
        return saved ? saveUri : null;
    }

    /**
     * Copies selected pages in place and returns the indexes of the newly inserted copies.
     *
     * @param document the document being edited.
     * @param selectedPages zero-based page indexes to copy.
     * @return selected indexes for copied pages, or null when no page is copied.
     */
    int[] copyPages(CPDFDocument document, int[] selectedPages) {
        if (document == null || isEmpty(selectedPages)) {
            return null;
        }

        boolean copied = false;
        boolean[] copiedPages = new boolean[selectedPages.length];
        for (int i = selectedPages.length - 1; i >= 0; i--) {
            int pageIndex = selectedPages[i];
            if (pageIndex < 0 || pageIndex >= document.getPageCount()) {
                continue;
            }
            copiedPages[i] = document.copyPage(pageIndex, pageIndex + 1) != null;
            copied |= copiedPages[i];
        }

        if (!copied) {
            return null;
        }

        CPDFThumbnailCacheRevisionManager.bumpRevision(document);
        return buildCopiedPageSelection(selectedPages, copiedPages);
    }

    /**
     * Rotates each selected page clockwise by 90 degrees.
     *
     * @param document the document being edited.
     * @param selectedPages zero-based page indexes to rotate.
     * @return true when all selected pages are rotated successfully.
     */
    boolean rotatePages(CPDFDocument document, int[] selectedPages) {
        if (document == null || isEmpty(selectedPages)) {
            return false;
        }

        for (int pageIndex : selectedPages) {
            CPDFPage page = document.pageAtIndex(pageIndex);
            if (page == null) {
                return false;
            }
            if (!page.setRotation(page.getRotation() + 90)) {
                return false;
            }
        }
        CPDFThumbnailCacheRevisionManager.bumpRevision(document);
        return true;
    }

    /**
     * Removes selected pages from the document.
     *
     * @param document the document being edited.
     * @param selectedPages zero-based page indexes to delete.
     * @return true when pages are removed successfully.
     */
    boolean deletePages(CPDFDocument document, int[] selectedPages) {
        if (document == null || isEmpty(selectedPages)) {
            return false;
        }
        boolean removed = document.removePages(selectedPages);
        if (removed) {
            CPDFThumbnailCacheRevisionManager.bumpRevision(document);
        }
        return removed;
    }

    /**
     * Moves a page from one index to another.
     *
     * @param document the document being edited.
     * @param sourcePosition the zero-based page index being moved.
     * @param targetPosition the zero-based destination index.
     * @return true when the move succeeds.
     */
    boolean movePage(CPDFDocument document, int sourcePosition, int targetPosition) {
        if (document == null) {
            return false;
        }
        boolean moved = document.movePage(sourcePosition, targetPosition);
        if (moved) {
            CPDFThumbnailCacheRevisionManager.bumpRevision(document);
        }
        return moved;
    }

    private boolean isEmpty(int[] pages) {
        return pages == null || pages.length == 0;
    }

    private int[] buildSequentialPages(int pageCount) {
        int[] pages = new int[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pages[i] = i;
        }
        return pages;
    }

    private int[] buildReplacementSelection(int[] replacedPages, int insertedPageCount) {
        int firstInsertedPage = replacedPages[replacedPages.length - 1] + 1 - replacedPages.length;
        int[] selectedPages = new int[insertedPageCount];
        for (int i = 0; i < insertedPageCount; i++) {
            selectedPages[i] = firstInsertedPage + i;
        }
        return selectedPages;
    }

    private int[] buildCopiedPageSelection(int[] selectedPages, boolean[] copiedPages) {
        List<Integer> copiedSelection = new ArrayList<>();
        int copiedCount = 0;
        for (int i = 0; i < selectedPages.length; i++) {
            if (copiedPages[i]) {
                copiedCount++;
                copiedSelection.add(selectedPages[i] + copiedCount);
            }
        }

        int[] pages = new int[copiedSelection.size()];
        for (int i = 0; i < copiedSelection.size(); i++) {
            pages[i] = copiedSelection.get(i);
        }
        return pages;
    }

    private String buildExtractFileName(CPDFDocument document, int[] exportPages) {
        String fileName = document.getFileName();
        int extensionIndex = fileName == null ? -1 : fileName.toLowerCase().indexOf(".pdf");
        String baseName = extensionIndex > 0 ? fileName.substring(0, extensionIndex) : "Extract";
        StringBuilder newName = new StringBuilder(baseName);
        newName.append("_Page");
        for (int i = 0; i < exportPages.length; i++) {
            if (i != 0) {
                newName.append(",");
            }
            newName.append(exportPages[i] + 1);
        }
        newName.append(".pdf");
        return newName.toString();
    }

}
