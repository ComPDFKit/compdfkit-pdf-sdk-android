/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.undo;

import androidx.annotation.NonNull;

import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;

/**
 * Selects the history source for annotation undo and redo actions.
 */
public final class InkUndoRedoResolver {

    public enum Action {
        UNDO,
        REDO
    }

    public enum HistoryTarget {
        NONE,
        INK,
        DOCUMENT
    }

    private InkUndoRedoResolver() {
    }

    @NonNull
    public static HistoryTarget resolve(@NonNull AnnotationsConfig.InkUndoRedoMode mode,
                                        @NonNull Action action,
                                        boolean isInkEditing,
                                        boolean inkAvailable,
                                        boolean documentAvailable) {
        if (!isInkEditing) {
            return documentAvailable ? HistoryTarget.DOCUMENT : HistoryTarget.NONE;
        }

        switch (mode) {
            case INK_ONLY:
                return inkAvailable ? HistoryTarget.INK : HistoryTarget.NONE;
            case DOCUMENT_ONLY:
                return documentAvailable ? HistoryTarget.DOCUMENT : HistoryTarget.NONE;
            case HYBRID:
            default:
                if (action == Action.REDO) {
                    // Mixed histories require the coordinator's recorded Undo order.
                    return HistoryTarget.NONE;
                }
                if (inkAvailable) {
                    return HistoryTarget.INK;
                }
                return documentAvailable ? HistoryTarget.DOCUMENT : HistoryTarget.NONE;
        }
    }
}
