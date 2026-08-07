package com.compdfkit.tools.common.pdf.undo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RedoHistoryOrderTest {

    @Test
    public void redoReplaysUndoSourcesInReverseOrder() {
        RedoHistoryOrder order = new RedoHistoryOrder();
        order.recordUndo(InkUndoRedoResolver.HistoryTarget.INK);
        order.recordUndo(InkUndoRedoResolver.HistoryTarget.INK);
        order.recordUndo(InkUndoRedoResolver.HistoryTarget.DOCUMENT);

        assertEquals(InkUndoRedoResolver.HistoryTarget.DOCUMENT, order.peekRedo());
        order.completeRedo();
        assertEquals(InkUndoRedoResolver.HistoryTarget.INK, order.peekRedo());
        order.completeRedo();
        assertEquals(InkUndoRedoResolver.HistoryTarget.INK, order.peekRedo());
        order.completeRedo();
        assertNull(order.peekRedo());
    }

    @Test
    public void clearInvalidatesRecordedRedoSources() {
        RedoHistoryOrder order = new RedoHistoryOrder();
        order.recordUndo(InkUndoRedoResolver.HistoryTarget.INK);

        order.clear();

        assertNull(order.peekRedo());
    }
}
