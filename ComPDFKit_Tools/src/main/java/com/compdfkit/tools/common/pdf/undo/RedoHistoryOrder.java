/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.undo;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Records successful Undo sources so Redo can replay them in reverse operation order.
 */
final class RedoHistoryOrder {

    private final Deque<InkUndoRedoResolver.HistoryTarget> sources = new ArrayDeque<>();

    void recordUndo(InkUndoRedoResolver.HistoryTarget source) {
        sources.push(source);
    }

    @Nullable
    InkUndoRedoResolver.HistoryTarget peekRedo() {
        return sources.peek();
    }

    void completeRedo() {
        if (!sources.isEmpty()) {
            sources.pop();
        }
    }

    boolean isEmpty() {
        return sources.isEmpty();
    }

    void clear() {
        sources.clear();
    }
}
