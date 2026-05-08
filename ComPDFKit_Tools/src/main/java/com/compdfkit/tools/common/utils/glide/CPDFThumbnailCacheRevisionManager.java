package com.compdfkit.tools.common.utils.glide;

import androidx.annotation.NonNull;

import com.compdfkit.core.document.CPDFDocument;

import java.util.Map;
import java.util.WeakHashMap;

public final class CPDFThumbnailCacheRevisionManager {

    private static final Map<CPDFDocument, Integer> DOCUMENT_REVISIONS = new WeakHashMap<>();

    private CPDFThumbnailCacheRevisionManager() {
    }

    public static synchronized int getRevision(CPDFDocument document) {
        if (document == null) {
            return 0;
        }
        Integer revision = DOCUMENT_REVISIONS.get(document);
        return revision == null ? 0 : revision;
    }

    public static synchronized int bumpRevision(@NonNull CPDFDocument document) {
        int nextRevision = getRevision(document) + 1;
        DOCUMENT_REVISIONS.put(document, nextRevision);
        return nextRevision;
    }

    public static synchronized void clear(@NonNull CPDFDocument document) {
        DOCUMENT_REVISIONS.remove(document);
    }
}
