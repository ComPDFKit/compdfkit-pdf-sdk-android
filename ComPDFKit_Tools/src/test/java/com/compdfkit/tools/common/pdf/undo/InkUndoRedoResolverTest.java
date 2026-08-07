package com.compdfkit.tools.common.pdf.undo;

import static org.junit.Assert.assertEquals;

import com.compdfkit.tools.common.pdf.config.AnnotationsConfig;

import org.junit.Test;

public class InkUndoRedoResolverTest {

    @Test
    public void inkOnlyUsesOnlyInkHistory() {
        assertEquals(InkUndoRedoResolver.HistoryTarget.INK, resolve(
                AnnotationsConfig.InkUndoRedoMode.INK_ONLY, true, true, true));
        assertEquals(InkUndoRedoResolver.HistoryTarget.NONE, resolve(
                AnnotationsConfig.InkUndoRedoMode.INK_ONLY, true, false, true));
    }

    @Test
    public void documentOnlyUsesOnlyDocumentHistory() {
        assertEquals(InkUndoRedoResolver.HistoryTarget.DOCUMENT, resolve(
                AnnotationsConfig.InkUndoRedoMode.DOCUMENT_ONLY, true, true, true));
        assertEquals(InkUndoRedoResolver.HistoryTarget.NONE, resolve(
                AnnotationsConfig.InkUndoRedoMode.DOCUMENT_ONLY, true, true, false));
    }

    @Test
    public void hybridFallsBackToDocumentHistoryForUndo() {
        assertEquals(InkUndoRedoResolver.HistoryTarget.INK, resolve(
                AnnotationsConfig.InkUndoRedoMode.HYBRID, true, true, true));
        assertEquals(InkUndoRedoResolver.HistoryTarget.DOCUMENT, resolve(
                AnnotationsConfig.InkUndoRedoMode.HYBRID, true, false, true));
    }

    @Test
    public void hybridRedoRequiresCoordinatorOrder() {
        assertEquals(InkUndoRedoResolver.HistoryTarget.NONE, InkUndoRedoResolver.resolve(
                AnnotationsConfig.InkUndoRedoMode.HYBRID, InkUndoRedoResolver.Action.REDO,
                true, true, true));
        assertEquals(InkUndoRedoResolver.HistoryTarget.NONE, InkUndoRedoResolver.resolve(
                AnnotationsConfig.InkUndoRedoMode.HYBRID, InkUndoRedoResolver.Action.REDO,
                true, false, true));
    }

    @Test
    public void nonInkEditingAlwaysUsesDocumentHistory() {
        assertEquals(InkUndoRedoResolver.HistoryTarget.DOCUMENT, resolve(
                AnnotationsConfig.InkUndoRedoMode.INK_ONLY, false, true, true));
        assertEquals(InkUndoRedoResolver.HistoryTarget.NONE, resolve(
                AnnotationsConfig.InkUndoRedoMode.HYBRID, false, true, false));
    }

    @Test
    public void configurationValuesUseDocumentedJsonNames() {
        assertEquals(AnnotationsConfig.InkUndoRedoMode.INK_ONLY,
                AnnotationsConfig.InkUndoRedoMode.fromString("inkOnly"));
        assertEquals(AnnotationsConfig.InkUndoRedoMode.DOCUMENT_ONLY,
                AnnotationsConfig.InkUndoRedoMode.fromString("documentOnly"));
        assertEquals(AnnotationsConfig.InkUndoRedoMode.HYBRID,
                AnnotationsConfig.InkUndoRedoMode.fromString("hybrid"));
        assertEquals(AnnotationsConfig.InkUndoRedoMode.INK_ONLY,
                AnnotationsConfig.InkUndoRedoMode.fromString("INK_ONLY"));
    }

    private InkUndoRedoResolver.HistoryTarget resolve(
            AnnotationsConfig.InkUndoRedoMode mode,
            boolean isInkEditing,
            boolean inkAvailable,
            boolean documentAvailable) {
        return InkUndoRedoResolver.resolve(mode, InkUndoRedoResolver.Action.UNDO,
                isInkEditing, inkAvailable, documentAvailable);
    }
}
