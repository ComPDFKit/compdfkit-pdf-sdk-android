package com.compdfkit.tools.common.pdf;

import static org.junit.Assert.*;
import android.graphics.RectF;
import android.graphics.PointF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.io.*;
import java.util.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class CPDFTapTextSelectionParagraphTest {
    private static final CPDFTapTextSelectionController.GapChecker CONTIGUOUS = (a, b) -> a == b;

    // Real-sample line geometry and indices; text uses placeholders instead of customer PDFs.
    private List<CPDFTapTextSelectionController.TextLine> sample(String name) throws Exception {
        List<CPDFTapTextSelectionController.TextLine> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/paragraph/" + name + ".tsv"), "UTF-8"))) {
            for (String s; (s=r.readLine())!=null;) {
                String[] f=s.split("\t",7);
                lines.add(new CPDFTapTextSelectionController.TextLine(Integer.parseInt(f[0]),
                        Integer.parseInt(f[1]),new RectF(Float.parseFloat(f[2]),Float.parseFloat(f[3]),
                        Float.parseFloat(f[4]),Float.parseFloat(f[5])),f[6]));
            }
        }
        CPDFTapTextSelectionController.mergeInlineFragments(lines, CONTIGUOUS);
        return lines;
    }

    private void expectEveryLine(String sample, int start, int end) throws Exception {
        List<CPDFTapTextSelectionController.TextLine> lines=sample(sample);
        int probes=0;
        for (CPDFTapTextSelectionController.TextLine line:lines) {
            if(line.start<start || line.start>=end) continue;
            CPDFTapTextSelectionController.ParagraphMatch match=
                    CPDFTapTextSelectionController.resolveParagraphLines(lines,line.start,CONTIGUOUS);
            assertNotNull(match);
            assertEquals(sample+" hit="+line.start+" begin", start,match.range.location);
            assertEquals(sample+" hit="+line.start+" end",end,match.range.location+match.range.length);
            probes++;
        }
        assertTrue(probes>0);
    }

    @Test public void chineseIndentedParagraph() throws Exception {expectEveryLine("urethral-2",0,101);}
    @Test public void chineseHeadingExcluded() throws Exception {expectEveryLine("urethral-2",122,364);}
    @Test public void chineseShortLastLine() throws Exception {expectEveryLine("urethral-2",364,479);}
    @Test public void chineseAdjacentParagraphs() throws Exception {expectEveryLine("urethral-2",479,599);}
    @Test public void chineseLongParagraph() throws Exception {expectEveryLine("urethral-2",599,900);}
    @Test public void chineseRightColumn() throws Exception {expectEveryLine("urethral-2",1350,1570);}
    @Test public void chineseLongRightColumn() throws Exception {expectEveryLine("urethral-2",1570,2137);}
    @Test public void chineseCrossPageContinuationStopsLocally() throws Exception {expectEveryLine("urethral-3",0,41);}
    @Test public void chineseParagraphAfterContinuation() throws Exception {expectEveryLine("urethral-3",41,106);}
    @Test public void englishSectionFragmentsRejoin() throws Exception {expectEveryLine("svm-4",214,556);}
    @Test public void englishHyphenatedLines() throws Exception {expectEveryLine("svm-4",581,957);}
    @Test public void englishIndentSeparatesParagraphs() throws Exception {expectEveryLine("svm-4",988,1567);}
    @Test public void englishFirstLineIndented() throws Exception {expectEveryLine("svm-4",1567,2053);}
    @Test public void englishShortLastLineWithReference() throws Exception {expectEveryLine("svm-4",2053,2299);}
    @Test public void englishInlineSuperscriptFragments() throws Exception {expectEveryLine("svm-4",2299,3026);}
    @Test public void englishRightColumnExcludesNextHeading() throws Exception {expectEveryLine("svm-4",3066,3571);}
    @Test public void englishSameColumnNextParagraph() throws Exception {expectEveryLine("svm-4",3619,3946);}
    @Test public void chineseReferenceHangingIndent() throws Exception {expectEveryLine("urethral-5",64,211);}
    @Test public void referenceShortLastLineRemainsIncluded() throws Exception {expectEveryLine("urethral-5",211,421);}
    @Test public void referenceVolumeNumberIsNotANewList() throws Exception {expectEveryLine("urethral-5",677,855);}

    private CPDFTapTextSelectionController.TextLine line(int start,int end,float x,float y,float right,String text){
        return new CPDFTapTextSelectionController.TextLine(start,end,new RectF(x,y,right,y-10),text);
    }
    @Test public void foreignTextBetweenRangesStopsExpansion(){
        List<CPDFTapTextSelectionController.TextLine> lines=Arrays.asList(
                line(0,20,50,100,280,"body"),line(40,60,50,85,280,"body"));
        assertEquals(20,CPDFTapTextSelectionController.resolveParagraphLines(lines,5,CONTIGUOUS).range.length);
    }
    @Test public void reverseReadingOrderStopsExpansion(){
        List<CPDFTapTextSelectionController.TextLine> lines=Arrays.asList(
                line(20,40,50,100,280,"body"),line(0,20,50,85,280,"body"));
        assertEquals(20,CPDFTapTextSelectionController.resolveParagraphLines(lines,5,CONTIGUOUS).range.length);
    }
    @Test public void listHangingIndentRemainsOneItem(){
        List<CPDFTapTextSelectionController.TextLine> lines=Arrays.asList(
                line(0,20,50,100,280,"1. First item"),line(20,40,62,85,280,"continued"),
                line(40,60,50,70,280,"2. Next item"));
        assertEquals(40,CPDFTapTextSelectionController.resolveParagraphLines(lines,5,CONTIGUOUS).range.length);
    }
    @Test public void nullTerminatorIsNotForeignText(){
        assertTrue(CPDFTapTextSelectionController.onlyWhitespace("\r\n\u0000\u3000"));
        assertFalse(CPDFTapTextSelectionController.onlyWhitespace("\u6b63\u6587\u0000"));
        assertFalse(CPDFTapTextSelectionController.onlyWhitespace(null));
    }
    @Test public void missingHitDoesNotChooseFirstParagraph(){
        assertNull(CPDFTapTextSelectionController.resolveParagraphLines(
                Collections.singletonList(line(0,20,50,100,280,"body")),30,CONTIGUOUS));
    }

    @Test public void nearbyCharacterAllowsOnlySmallHorizontalGap() {
        RectF box = new RectF(10, 10, 20, 20);
        assertTrue(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(8, 15)));
        assertTrue(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(22, 15)));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(22.01F, 15)));
    }
    @Test public void nearbyCharacterHasSmallerVerticalAllowance() {
        RectF box = new RectF(10, 10, 20, 20);
        assertTrue(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(15, 9)));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(15, 8.99F)));
    }
    @Test public void smallFontTightensHitAllowance() {
        RectF box = new RectF(10, 10, 12, 12);
        assertTrue(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(12.4F, 11)));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(12.6F, 11)));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(11, 12.3F)));
    }
    @Test public void largeFontDoesNotExpandAbsoluteHitLimit() {
        RectF box = new RectF(10, 10, 50, 50);
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(53, 30)));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(box, new PointF(30, 52)));
    }
    @Test public void invalidHitGeometryIsRejected() {
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(null, new PointF(1, 1)));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(new RectF(), new PointF(1, 1)));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(new RectF(0, 0, 10, 10), null));
        assertFalse(CPDFTapTextSelectionController.isWithinCharacterSlop(
                new RectF(0, 0, 10, 10), new PointF(Float.NaN, 5)));
    }
    @Test public void insideCharacterRemainsAcceptedWithoutMovingInput() {
        RectF box = new RectF(10, 10, 20, 20); PointF point = new PointF(15, 15);
        assertTrue(CPDFTapTextSelectionController.isWithinCharacterSlop(box, point));
        assertEquals(new RectF(10, 10, 20, 20), box);
        assertEquals(new PointF(15, 15), point);
    }
}
