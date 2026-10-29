/**
 * Copyright \u00a9 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.core.page.CPDFTextPage;
import com.compdfkit.core.page.CPDFTextRange;
import com.compdfkit.core.page.CPDFTextSelection;
import com.compdfkit.ui.reader.CPDFPageView;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.ui.reader.IReaderViewCallback;
import com.compdfkit.ui.widget.selection.CPDFPageSelections;
import com.compdfkit.ui.widget.selection.ISelectionHelper;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Selects text on PDF page taps using the reader's existing selection UI.
 *
 * <p>Create, configure, attach, and detach this controller on the main thread.
 * Construction alone does not register a listener or change CPDFDocumentFragment.
 * Keep the instance for the reader view's lifetime and detach it before destroying
 * that view. Attaching preserves other registered reader callbacks. Eligible page taps
 * are consumed through onPageTap, suppressing legacy document-area callbacks for all
 * listeners even when no text is selected; other point callbacks still run.</p>
 *
 * <p>Usage in a Fragment that owns a CPDFReaderView:</p>
 * <pre>{@code
 * private CPDFTapTextSelectionController textSelectionController;
 *
 * // Call after creating the reader, once for each view lifecycle.
 * private void configureTextSelection(CPDFReaderView readerView) {
 *     textSelectionController = new CPDFTapTextSelectionController(readerView);
 *     // AUTO is the default: paragraph, then line, then word on failure.
 *     // Choose PARAGRAPH, LINE, or WORD to disable automatic fallback.
 *     textSelectionController.setSelectionMode(
 *             CPDFTapTextSelectionController.SelectionMode.AUTO);
 *     textSelectionController.attach();
 * }
 *
 * @Override
 * public void onDestroyView() {
 *     if (textSelectionController != null) {
 *         textSelectionController.detach();
 *         textSelectionController = null;
 *     }
 *     super.onDestroyView();
 * }
 * }</pre>
 *
 * <p>Callbacks use reader-local pixel coordinates. Selection runs only in BROWSE
 * touch mode with VIEW, ALL, or ANNOT view mode. AUTO stops at the first operation
 * that creates a valid text range and drawable geometry. Independent modes do not
 * fall back. Selection reuses the highlight, handles, and context menu without
 * creating a persistent highlight annotation.</p>
 *
 * <p>Paragraphs are inferred from reading-layer line positions, spacing, and
 * indentation within the current page and column. Ambiguous layouts may yield
 * one line, which still counts as success. PDF layout does not reliably encode
 * semantic paragraphs. Validate complex tables and rotated text with your
 * documents. No content-editing model is loaded.</p>
 */
public final class CPDFTapTextSelectionController implements IReaderViewCallback {
    public enum SelectionMode {
        WORD,
        LINE,
        PARAGRAPH,
        /** Tries paragraph, line, then word until a drawable selection is established. */
        AUTO
    }

    // Limits use PDF units and are tightened by glyph height to prevent excessive snapping.
    private static final float MAX_CHARACTER_GAP_X = 2F;
    private static final float MAX_CHARACTER_GAP_Y = 1F;
    private final CPDFReaderView readerView;
    private SelectionMode selectionMode = SelectionMode.AUTO;
    private boolean attached;

    public CPDFTapTextSelectionController(CPDFReaderView readerView) {
        this.readerView = readerView;
    }

    /** Sets the granularity for subsequent taps. Defaults to AUTO; null keeps the current mode. */
    public void setSelectionMode(SelectionMode mode) {
        if (mode != null) {
            selectionMode = mode;
        }
    }

    /** Registers this listener with the reader. Repeated calls do not register it twice. */
    public void attach() {
        if (!attached) {
            readerView.setReaderViewCallback(this);
            attached = true;
        }
    }

    /** Removes this listener. Call from Fragment.onDestroyView() before releasing the reader. */
    public void detach() {
        if (attached) {
            readerView.removeReaderViewCallback(this);
            attached = false;
        }
    }

    @Override
    public boolean onPageTap(PointF point) {
        if (!attached || point == null || !canSelectInCurrentMode()) {
            return false;
        }
        CPDFPageView pageView = findHitPage(point);
        if (pageView == null) {
            return false;
        }
        // Pass reader-local coordinates directly; do not subtract page offsets or divide by scale.
        PointF pdfPoint = readerView.convertPointToPage(point.x, point.y);
        if (pdfPoint == null) {
            return false;
        }
        selectWithFallback(selectionMode,
                () -> selectParagraph(pageView, point, pdfPoint),
                () -> selectLine(pageView, point, pdfPoint),
                () -> selectWord(pageView, point));
        // Consume eligible page taps even when no text is selected, avoiding toolbar toggles.
        return true;
    }

    interface SelectionAttempt {
        boolean select();
    }

    /**
     * Stops at the first successful operation so a smaller selection cannot overwrite it.
     * False means no drawable selection was established. SDK exceptions propagate unchanged.
     */
    static boolean selectWithFallback(SelectionMode mode, SelectionAttempt paragraph,
                                      SelectionAttempt line, SelectionAttempt word) {
        switch (mode) {
            case AUTO:
                return paragraph.select() || line.select() || word.select();
            case PARAGRAPH:
                return paragraph.select();
            case LINE:
                return line.select();
            case WORD:
            default:
                return word.select();
        }
    }

    private boolean selectWord(CPDFPageView pageView, PointF readerPoint) {
        ISelectionHelper helper = pageView.getSelectionHelper();
        if (!(helper instanceof CPDFPageSelections)) {
            return false;
        }
        CPDFDocument document = readerView.getPDFDocument();
        CPDFPage page = document == null ? null : document.pageAtIndex(pageView.getPageNum());
        CPDFTextPage textPage = page == null ? null : page.getTextPage();
        PointF pdfPoint = readerView.convertPointToPage(readerPoint.x, readerPoint.y);
        if (textPage == null || !textPage.isValid() || pdfPoint == null) {
            return false;
        }
        // Native queries may return nearby words; reject candidates outside the tap bounds.
        CPDFTextSelection[] words = textPage.getSelectionForWordPos(pdfPoint);
        boolean containsPoint = false;
        if (words != null) {
            for (CPDFTextSelection word : words) {
                RectF box = word == null ? null : normalized(word.getQuadBounds());
                if (box != null && box.contains(pdfPoint.x, pdfPoint.y)) {
                    containsPoint = true;
                    break;
                }
            }
        }
        if (!containsPoint) {
            return false;
        }
        // Reuse long-press word selection with page-view-local pixel coordinates.
        return helper.onLongPress(readerPoint.x - pageView.getLeft(),
                readerPoint.y - pageView.getTop())
                && hasDrawableTextSelection((CPDFPageSelections) helper);
    }

    private static boolean hasDrawableTextSelection(CPDFPageSelections helper) {
        boolean hasText = false;
        for (CPDFTextSelection item : helper.getSelections()) {
            CPDFTextRange range = item == null ? null : item.getTextRange();
            if (range != null && range.location >= 0 && range.length > 0) {
                hasText = true;
                break;
            }
        }
        if (!hasText) return false;
        for (RectF rect : helper.getSelectionsRect()) {
            if (normalized(rect) != null) return true;
        }
        return false;
    }

    private CPDFPageView findHitPage(PointF point) {
        for (int i = 0; i < readerView.getChildCount(); i++) {
            View child = readerView.getChildAt(i);
            if (child instanceof CPDFPageView
                    && point.x >= child.getLeft() && point.x < child.getRight()
                    && point.y >= child.getTop() && point.y < child.getBottom()) {
                return (CPDFPageView) child;
            }
        }
        return null;
    }

    private boolean canSelectInCurrentMode() {
        CPDFReaderView.ViewMode mode = readerView.getViewMode();
        // Select only while browsing; preserve drawing, form, and content-editing interactions.
        return readerView.getTouchMode() == CPDFReaderView.TouchMode.BROWSE
                && (mode == CPDFReaderView.ViewMode.VIEW
                || mode == CPDFReaderView.ViewMode.ALL
                || mode == CPDFReaderView.ViewMode.ANNOT);
    }

    /** Finds the tapped line and displays it with the existing text selection component. */
    private boolean selectLine(CPDFPageView pageView, PointF readerPoint, PointF pdfPoint) {
        ISelectionHelper selection = pageView.getSelectionHelper();
        CPDFDocument document = readerView.getPDFDocument();
        if (!(selection instanceof CPDFPageSelections)) {
            return false;
        }
        if (document == null) {
            return false;
        }
        CPDFPage page = document.pageAtIndex(pageView.getPageNum());
        CPDFTextPage textPage = page == null ? null : page.getTextPage();
        if (textPage == null || !textPage.isValid()) {
            return false;
        }
        CPDFTextSelection line = textPage.getSelectionForLineAtPos(pdfPoint);
        CPDFTextRange range = line == null ? null : line.getTextRange();
        RectF bounds = line == null ? null : normalized(line.getQuadBounds());
        // Reject nearby lines outside the tap bounds to avoid selecting across a column gutter.
        if (bounds == null || !bounds.contains(pdfPoint.x, pdfPoint.y)
                || range == null || range.location < 0 || range.length <= 0) {
            return false;
        }
        return selectTextRange(pageView, page, textPage, range, readerPoint);
    }

    /** Infers a paragraph from reading-layer layout without loading or changing editing objects. */
    private boolean selectParagraph(CPDFPageView pageView, PointF readerPoint, PointF pdfPoint) {
        CPDFDocument document = readerView.getPDFDocument();
        if (document == null) {
            return false;
        }
        CPDFPage page = document.pageAtIndex(pageView.getPageNum());
        CPDFTextPage textPage = page == null ? null : page.getTextPage();
        if (textPage == null || !textPage.isValid()) {
            return false;
        }
        if (!(pageView.getSelectionHelper() instanceof CPDFPageSelections)) {
            return false;
        }

        ParagraphMatch match = findParagraphRange(textPage, pdfPoint);
        if (match == null) {
            return false;
        }

        PointF initializationPoint = readerPoint;
        if (match.selectionPoint != null) {
            // Seed a gap hit with the confirmed glyph center; the original blank point may fail again.
            initializationPoint = readerView.convertPointFromPage(page,
                    match.selectionPoint.x, match.selectionPoint.y);

            if (initializationPoint == null) {
                return false;
            }
        }
        return selectTextRange(pageView, page, textPage, match.range, initializationPoint);
    }

    // Normalize text only for boundary detection; selections retain original reading indices.
    private static final Pattern LIST_START = Pattern.compile(
            "^(?:[0-9]{1,3}(?:[.][0-9]{1,3})*[.)\u3001]?\\s+|\\[[0-9]{1,3}\\]|[\uff08(][0-9\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341]+[\uff09)](?![:\uff1a;\uff1b,.\uff0c0-9])|[\u2022\u25cf\u25aa]|[\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341]+[\u3001.]).*");

    /**
     * Queries all reading lines once per tap. Native objects and layout are not cached across
     * taps, avoiding stale results after editing or navigation. Thresholds use PDF glyph height
     * and local line spacing. Editing sections may split paragraphs or include adjacent headings.
     */
    static ParagraphMatch findParagraphRange(CPDFTextPage textPage, PointF point) {
        if (textPage == null || !textPage.isValid() || point == null) {
            return null;
        }
        // Prefer an exact hit, then allow only a small glyph gap within the tapped physical line.
        int hit = textPage.getCharIndexAtPos(point, 0, 0);
        RectF rawBox = hit < 0 ? null : textPage.getCharBox(hit);
        RectF hitBox = normalized(rawBox);

        boolean exactHit = hitBox != null && hitBox.contains(point.x, point.y);
        int count = textPage.getCountChars();

        CPDFTextSelection[] selections = textPage.getSelectionsByLineForRange(new CPDFTextRange(0, count));

        if (selections == null) {
            return null;
        }
        PointF selectionPoint = null;
        if (!exactHit) {
            int nearby = findNearbyCharacter(textPage, selections, point, count);
            if (nearby < 0) {
                return null;
            }
            hit = nearby;
            RectF box = normalized(textPage.getCharBox(hit));
            selectionPoint = new PointF(box.centerX(), box.centerY());
        }
        GapChecker gaps = (begin, end) -> begin == end || onlyWhitespace(
                textPage.getText(new CPDFTextRange(begin, end - begin)));
        List<TextLine> lines = new ArrayList<>();

        for (CPDFTextSelection selection : selections) {
            CPDFTextRange range = selection == null ? null : selection.getTextRange();
            RectF box = selection == null ? null : normalized(selection.getQuadBounds());
            if (range == null || range.location < 0 || range.length <= 0 || box == null
                    || (long) range.location + range.length > count) {
                continue;
            }
            String text = textPage.getText(range);
            if (!onlyWhitespace(text)) {
                lines.add(new TextLine(range.location, range.location + range.length, box, text));
            }
        }

        mergeInlineFragments(lines, gaps);

        ParagraphMatch match = resolveParagraphLines(lines, hit, gaps);
        return match == null || selectionPoint == null ? match : new ParagraphMatch(match, selectionPoint);
    }

    /**
     * Reuses reading lines to find a visible glyph when the zero-tolerance query misses.
     * The tap must lie vertically inside the line and within a small distance of a glyph.
     * A wide line box alone is insufficient; native hit testing must confirm the glyph center.
     */
    private static int findNearbyCharacter(CPDFTextPage textPage, CPDFTextSelection[] lines,
                                           PointF point, int count) {
        int best = -1;
        float bestDistance = Float.POSITIVE_INFINITY;
        for (CPDFTextSelection line : lines) {
            CPDFTextRange range = line == null ? null : line.getTextRange();
            RectF bounds = line == null ? null : normalized(line.getQuadBounds());
            if (bounds == null || range == null || range.location < 0 || range.length <= 0
                    || (long) range.location + range.length > count
                    || point.y < bounds.top || point.y > bounds.bottom
                    || point.x < bounds.left - MAX_CHARACTER_GAP_X
                    || point.x > bounds.right + MAX_CHARACTER_GAP_X) {
                continue;
            }

            for (int index = range.location; index < range.location + range.length; index++) {
                RectF box = normalized(textPage.getCharBox(index));
                if (!isWithinCharacterSlop(box, point)) continue;
                float dx = distanceToInterval(point.x, box.left, box.right);
                float dy = distanceToInterval(point.y, box.top, box.bottom);
                float distance = dx * dx + dy * dy;
                if (distance >= bestDistance
                        || onlyWhitespace(textPage.getText(new CPDFTextRange(index, 1)))) continue;
                PointF center = new PointF(box.centerX(), box.centerY());
                // Reject invisible, overlapping, or unresolvable glyphs as initialization anchors.
                if (textPage.getCharIndexAtPos(center, 0, 0) != index) continue;
                best = index;
                bestDistance = distance;
            }
        }

        return best;
    }

    /** Testable geometry bounds. Input rectangles must be normalized; coordinates are unchanged. */
    static boolean isWithinCharacterSlop(RectF box, PointF point) {
        if (box == null || point == null || box.isEmpty()) return false;
        return distanceToInterval(point.x, box.left, box.right)
                <= Math.min(MAX_CHARACTER_GAP_X, box.height() * 0.25F)
                && distanceToInterval(point.y, box.top, box.bottom)
                <= Math.min(MAX_CHARACTER_GAP_Y, box.height() * 0.12F);
    }

    private static float distanceToInterval(float value, float start, float end) {
        return Math.max(0, Math.max(start - value, value - end));
    }

    static void mergeInlineFragments(List<TextLine> lines, GapChecker gaps) {
        // Superscripts and font changes can split a physical line. Merge only close baselines,
        // small gaps, and contiguous reading indices; never join fragments across a column gutter.
        Collections.sort(lines, (a, b) -> Integer.compare(a.start, b.start));
        for (int i = 1; i < lines.size();) {
            TextLine a = lines.get(i - 1);
            TextLine b = lines.get(i);
            float h = Math.max(a.box.height(), b.box.height());
            float overlap = Math.min(a.box.bottom, b.box.bottom) - Math.max(a.box.top, b.box.top);
            if (b.start >= a.end && gaps.isWhitespace(a.end, b.start)
                    && overlap > Math.min(a.box.height(), b.box.height()) * 0.5F
                    && Math.abs(a.box.centerY() - b.box.centerY()) < h * 0.6F
                    && b.box.left >= a.box.right - h * 0.5F
                    && b.box.left - a.box.right < h * 1.2F) {
                RectF union = new RectF(a.box);
                union.union(b.box);
                lines.set(i - 1, new TextLine(a.start, b.end, union, a.text + " " + b.text));
                lines.remove(i);
            } else {
                i++;
            }
        }
    }

    /** Pure layout rules shared by real-page fixtures and synthetic boundary regression tests. */
    static ParagraphMatch resolveParagraphLines(List<TextLine> lines, int hit, GapChecker gaps) {
        TextLine anchor = null;
        for (TextLine line : lines) {
            if (line.start <= hit && hit < line.end) {
                anchor = line;
                break;
            }
        }
        if (anchor == null) {
            return null;
        }

        if (anchor.box.width() < anchor.box.height() * 1.5F) {
            return new ParagraphMatch(anchor, anchor, 1, "LINE_ONLY_ORIENTATION");
        }
        final TextLine target = anchor;
        List<TextLine> nearby = new ArrayList<>();
        for (TextLine line : lines) {
            float h = Math.max(anchor.box.height(), line.box.height());
            float overlap = horizontalOverlap(anchor.box, line.box);
            if (overlap >= Math.min(anchor.box.width(), line.box.width()) * 0.55F
                    // Right-edge alignment alone could include a spanning header in the right column.
                    && Math.abs(anchor.box.left - line.box.left) < h * 3
                    && Math.abs(anchor.box.centerY() - line.box.centerY()) < h * 20
                    && line.box.height() >= anchor.box.height() * 0.65F
                    && line.box.height() <= anchor.box.height() * 1.5F) {
                nearby.add(line);
            }
        }
        Collections.sort(nearby, (a, b) -> Float.compare(
                Math.abs(a.box.centerY() - target.box.centerY()),
                Math.abs(b.box.centerY() - target.box.centerY())));
        if (nearby.size() > 16) {
            nearby = new ArrayList<>(nearby.subList(0, 16));
        }
        List<Float> lefts = new ArrayList<>(), rights = new ArrayList<>(), heights = new ArrayList<>();
        for (TextLine line : nearby) {
            lefts.add(line.box.left);
            rights.add(line.box.right);
            heights.add(line.box.height());
        }
        // Keep hanging reference labels that extend beyond the body text left edge.
        float left = percentile(lefts, 0F);
        float right = percentile(rights, 0.75F);
        float height = percentile(heights, 0.5F);

        if (right - left < height * 6) {
            return new ParagraphMatch(anchor, anchor, 1, "LINE_ONLY_NARROW_BLOCK");
        }
        List<TextLine> column = new ArrayList<>();
        for (TextLine line : lines) {
            if (line.box.left >= left - height * 0.6F && line.box.left < left + height * 3
                    && line.box.right <= right + height * 0.8F
                    && line.box.right > left + height
                    && line.box.height() > height * 0.6F && line.box.height() < height * 1.6F) {
                column.add(line);
            }
        }
        // PDF coordinates start at the bottom left: sort by descending y, then ascending x.
        Collections.sort(column, (a, b) -> {
            int order = Float.compare(b.box.centerY(), a.box.centerY());
            return order == 0 ? Float.compare(a.box.left, b.box.left) : order;
        });
        int index = column.indexOf(anchor);

        if (index < 0) {
            return new ParagraphMatch(anchor, anchor, 1, "LINE_ONLY_AMBIGUOUS_COLUMN");
        }
        List<Float> pitches = new ArrayList<>();
        for (int i = Math.max(1, index - 8); i <= Math.min(column.size() - 1, index + 8); i++) {
            float pitch = column.get(i - 1).box.centerY() - column.get(i).box.centerY();
            if (pitch > height * 0.8F && pitch < height * 2.5F) {
                pitches.add(pitch);
            }
        }
        float pitch = pitches.isEmpty() ? height * 1.4F : percentile(pitches, 0.5F);

        int first = index, last = index;
        while (first > 0 && joinsParagraph(column.get(first - 1), column.get(first),
                left, right, height, pitch, gaps)) {
            first--;
        }
        while (last + 1 < column.size() && joinsParagraph(column.get(last), column.get(last + 1),
                left, right, height, pitch, gaps)) {
            last++;
        }

        return new ParagraphMatch(column.get(first), column.get(last), last - first + 1,
                first == last ? "LINE_ONLY_BOUNDARY" : "LAYOUT_PARAGRAPH");
    }

    private static boolean joinsParagraph(TextLine above, TextLine below, float left, float right,
                                          float height, float pitch, GapChecker gaps) {
        float distance = above.box.centerY() - below.box.centerY();
        // Reading indices must follow geometry without including another column or caption.
        if (below.start < above.end || !gaps.isWhitespace(above.end, below.start)
                || distance < height * 0.6F || distance > pitch * 1.35F + 0.5F
                || horizontalOverlap(above.box, below.box) <= 0
                || heading(above, right - left, height) || heading(below, right - left, height)
                || LIST_START.matcher(below.text).matches()) {
            return false;
        }
        // First-line indentation starts a paragraph; hanging list indentation stays in the item.
        if (below.box.left - above.box.left > height * 0.7F
                && !LIST_START.matcher(above.text).matches()) {
            return false;
        }
        // Stop after a clearly short trailing line; a period alone does not end a paragraph.
        return right - above.box.right <= Math.max(height * 2, (right - left) * 0.12F);
    }

    private static boolean heading(TextLine line, float width, float height) {
        return line.box.width() < width * 0.85F
                && (LIST_START.matcher(line.text).matches() || line.box.height() > height * 1.14F);
    }

    private static float horizontalOverlap(RectF a, RectF b) {
        return Math.max(0, Math.min(a.right, b.right) - Math.max(a.left, b.left));
    }

    private static float percentile(List<Float> values, float fraction) {
        Collections.sort(values);
        return values.get(Math.round((values.size() - 1) * fraction));
    }

    private static RectF normalized(RectF bounds) {
        if (bounds == null || Float.isNaN(bounds.left) || Float.isNaN(bounds.top)
                || Float.isNaN(bounds.right) || Float.isNaN(bounds.bottom)
                || Float.isInfinite(bounds.left) || Float.isInfinite(bounds.top)
                || Float.isInfinite(bounds.right) || Float.isInfinite(bounds.bottom)) {
            return null;
        }
        RectF result = new RectF(bounds);
        result.sort();
        return result.width() < 0.001F || result.height() < 0.001F ? null : result;
    }

    static boolean onlyWhitespace(String text) {
        if (text == null) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            // Native GetText may append a NUL terminator; it is not visible text.
            if (ch != 0 && !Character.isWhitespace(ch) && !Character.isSpaceChar(ch)) {
                return false;
            }
        }
        return true;
    }

    interface GapChecker {
        boolean isWhitespace(int start, int end);
    }

    static final class TextLine {
        final int start, end;
        final RectF box;
        final String text;

        TextLine(int start, int end, RectF box, String text) {
            this.start = start;
            this.end = end;
            this.box = normalized(box);
            this.text = Normalizer.normalize(text == null ? "" : text.replace("\u0000", ""),
                    Normalizer.Form.NFKC).replaceAll("[\\s\\p{Z}]+", " ").trim();
        }
    }

    static final class ParagraphMatch {
        final CPDFTextRange range;
        final int lineCount;
        final String reason;
        final PointF selectionPoint;

        ParagraphMatch(TextLine first, TextLine last, int lineCount, String reason) {
            range = new CPDFTextRange(first.start, last.end - first.start);
            this.lineCount = lineCount;
            this.reason = reason;
            selectionPoint = null;
        }

        ParagraphMatch(ParagraphMatch match, PointF selectionPoint) {
            range = match.range;
            lineCount = match.lineCount;
            reason = match.reason;
            this.selectionPoint = new PointF(selectionPoint.x, selectionPoint.y);
        }
    }

    /** Initializes and expands the selection to the requested range, reusing handles and menus. */
    private boolean selectTextRange(CPDFPageView pageView, CPDFPage page, CPDFTextPage textPage,
                                    CPDFTextRange range, PointF readerPoint) {
        ISelectionHelper selection = pageView.getSelectionHelper();
        if (!(selection instanceof CPDFPageSelections)) {
            return false;
        }
        int count = textPage.getCountChars();
        if (range == null || range.location < 0 || range.length <= 0 || range.location >= count) {
            return false;
        }
        int end = (int) Math.min((long) count,
                (long) range.location + range.length);
        // Convert only the first and last visible glyphs, not every character in the paragraph.
        PointF first = rangeEndpoint(pageView, page, textPage, range.location, end, 1);
        PointF last = rangeEndpoint(pageView, page, textPage, end - 1, range.location - 1, -1);
        if (first == null || last == null) {
            return false;
        }
        boolean initialized = selection.onLongPress(
                readerPoint.x - pageView.getLeft(), readerPoint.y - pageView.getTop());

        if (!initialized) {
            return false;
        }
        CPDFPageSelections textSelection = (CPDFPageSelections) selection;

        // Keep the initial selection for a single glyph; findSelections ignores coincident endpoints.

        if (!first.equals(last)) {
            textSelection.findSelections(first, last);
        }

        pageView.invalidate();
        textSelection.showContextMenuForCurrentSelection();

        return hasDrawableTextSelection(textSelection);
    }

    private PointF rangeEndpoint(CPDFPageView pageView, CPDFPage page, CPDFTextPage textPage,
                                 int start, int limit, int step) {
        for (int i = start; i != limit; i += step) {
            RectF box = normalized(textPage.getCharBox(i));
            if (box == null) {
                continue;
            }
            PointF position = readerView.convertPointFromPage(page, box.centerX(), box.centerY());
            if (position != null) {
                position.offset(-pageView.getLeft(), -pageView.getTop());

                return position;
            }
        }

        return null;
    }

    @Override
    public void onTapMainDocArea() {
        // Background taps without coordinates do not initiate text selection.
    }

    @Override public void onMoveToChild(int pageIndex) { }
    @Override public void onEndScroll() { }
    @Override public void onScrolling() { }
    @Override public void onRecordLastJumpPageNum(int lastPageIndex) { }
}
