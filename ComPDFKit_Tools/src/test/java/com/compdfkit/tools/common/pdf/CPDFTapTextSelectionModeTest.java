package com.compdfkit.tools.common.pdf;

import static org.junit.Assert.*;

import android.graphics.PointF;
import com.compdfkit.tools.common.pdf.CPDFTapTextSelectionController.SelectionMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class CPDFTapTextSelectionModeTest {
    private final StringBuilder calls = new StringBuilder();

    private CPDFTapTextSelectionController.SelectionAttempt attempt(String name, boolean result) {
        return () -> { calls.append(name); return result; };
    }

    private boolean select(SelectionMode mode, boolean paragraph, boolean line, boolean word) {
        return CPDFTapTextSelectionController.selectWithFallback(mode,
                attempt("P", paragraph), attempt("L", line), attempt("W", word));
    }

    @Before public void resetDiagnostics() { ShadowLog.clear(); ShadowToast.reset(); }

    @Test public void autoStopsWhenParagraphSucceeds() {
        assertTrue(select(SelectionMode.AUTO, true, true, true));
        assertEquals("P", calls.toString());
    }

    @Test public void autoSelectsLineWhenParagraphFails() {
        assertTrue(select(SelectionMode.AUTO, false, true, true));
        assertEquals("PL", calls.toString());
    }

    @Test public void autoSelectsWordWhenParagraphAndLineFail() {
        assertTrue(select(SelectionMode.AUTO, false, false, true));
        assertEquals("PLW", calls.toString());
    }

    @Test public void autoStopsAfterAllThreeAttemptsFail() {
        assertFalse(select(SelectionMode.AUTO, false, false, false));
        assertEquals("PLW", calls.toString());
    }

    @Test public void explicitParagraphDoesNotFallBack() {
        assertFalse(select(SelectionMode.PARAGRAPH, false, true, true));
        assertEquals("P", calls.toString());
    }

    @Test public void explicitLineDoesNotFallBack() {
        assertFalse(select(SelectionMode.LINE, true, false, true));
        assertEquals("L", calls.toString());
    }

    @Test public void explicitWordDoesNotAttemptOtherModes() {
        assertTrue(select(SelectionMode.WORD, true, true, true));
        assertEquals("W", calls.toString());
    }

    @Test public void exceptionIsNotConvertedToSelectionFailure() {
        IllegalStateException expected = new IllegalStateException("SDK failure");
        try {
            CPDFTapTextSelectionController.selectWithFallback(SelectionMode.AUTO,
                    () -> { throw expected; }, attempt("L", true), attempt("W", true));
            fail("SDK exception must propagate");
        } catch (IllegalStateException actual) {
            assertSame(expected, actual);
        }
        assertEquals("", calls.toString());
    }

    @Test public void detachedCallbacksDoNotEmitLogsOrCoordinateToasts() {
        CPDFTapTextSelectionController example = new CPDFTapTextSelectionController(null);
        assertFalse(example.onPageTap(new PointF(10, 20)));
        example.onTapMainDocArea();
        assertTrue(ShadowLog.getLogsForTag("CPDFTapPointDemo").isEmpty());
        assertEquals(0, ShadowToast.shownToastCount());
    }
}
