package com.compdfkit.tools.common.pdf;

import static org.junit.Assert.*;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.page.*;
import com.compdfkit.ui.reader.*;
import com.compdfkit.ui.widget.selection.CPDFPageSelections;
import org.junit.*;
import org.junit.runner.RunWith;
import org.json.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/** Local sample regression using SDK test initialization via init.gradle; PDFs stay outside Git. */
@RunWith(AndroidJUnit4.class)
public class CPDFTapTextSelectionInstrumentedTest {
    @BeforeClass public static void initSdk() throws Exception {
        Method init=Class.forName("com.compdfkit.ui.instrumented.CPDFUiTestAssets").getDeclaredMethod("initSdk");
        init.setAccessible(true);
        init.invoke(null);
    }

    private static CPDFDocument open(String name) {
        CPDFDocument doc=new CPDFDocument(InstrumentationRegistry.getInstrumentation().getTargetContext());
        File f=new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(),name+".pdf");
        assertEquals(CPDFDocument.PDFDocumentError.PDFDocumentErrorSuccess,doc.open(f.getAbsolutePath()));
        return doc;
    }

    private static PointF visiblePoint(CPDFTextPage text,int start,int end) {
        for(int i=start;i<end;i++){
            RectF b=text.getCharBox(i);
            if(b==null || Math.abs(b.width())<0.001F || Math.abs(b.height())<0.001F
                    || CPDFTapTextSelectionController.onlyWhitespace(text.getText(new CPDFTextRange(i,1)))) continue;
            PointF point=new PointF(b.centerX(),b.centerY());
            if(text.getCharIndexAtPos(point,0,0)>=0)return point;
        }
        return null;
    }

    private static void golden(String name,int pageIndex,int[][] ranges) {
        CPDFDocument doc=open(name);
        try {
            CPDFTextPage tp=doc.pageAtIndex(pageIndex).getTextPage();
            CPDFTextSelection[] lines=tp.getSelectionsByLineForRange(new CPDFTextRange(0,tp.getCountChars()));
            for(int[] expected:ranges) {
                int probes=0;
                for(CPDFTextSelection line:lines){
                    CPDFTextRange r=line.getTextRange();
                    if(r.location<expected[0] || r.location>=expected[1])continue;
                    PointF p=visiblePoint(tp,r.location,r.location+r.length);
                    if(p==null)continue;
                    CPDFTapTextSelectionController.ParagraphMatch match=CPDFTapTextSelectionController.findParagraphRange(tp,p);
                    String label=name+" page="+(pageIndex+1)+" hit="+r.location;
                    assertNotNull(label,match);
                    assertEquals(label+" begin",expected[0],match.range.location);
                    assertEquals(label+" end",expected[1],match.range.location+match.range.length);
                    probes++;
                }
                assertTrue(probes>0);
            }
            assertFalse(doc.hasChanges());
            assertNull("Reading selection must not load edit structures",doc.pageAtIndex(pageIndex).getCachedEditPage());
            if (pageIndex != 0) assertNull(CPDFTapTextSelectionController.findParagraphRange(tp,new PointF(295,400)));
        } finally {doc.close();}
    }

    @Test public void chineseBothColumnsAndHeadings() {
        golden("urethral",1,new int[][]{{0,101},{122,364},{364,479},{479,599},{599,900},
                {923,1238},{1238,1350},{1350,1570},{1570,2137},{2137,2594}});
    }

    /** Coordinates can hit text or whitespace in different documents; do not always snap. */
    @Test public void loggedPointKeepsDocumentSpecificHitBehavior() {
        for (String name : new String[]{"urethral", "svm"}) {
            CPDFDocument doc = open(name);
            try {
                CPDFTextPage text = doc.pageAtIndex(1).getTextPage();
                PointF point = new PointF(191.30429F, 386.5038F);
                CPDFTapTextSelectionController.ParagraphMatch match =
                        CPDFTapTextSelectionController.findParagraphRange(text, point);
                if ("urethral".equals(name)) {
                    assertEquals(634, text.getCharIndexAtPos(point, 0, 0));
                    assertNotNull(match);
                    assertEquals(599, match.range.location);
                    assertEquals(301, match.range.length);
                } else {
                    assertEquals(-1, text.getCharIndexAtPos(point, 8, 8));
                    assertNull("The reported coordinates are empty space in this sample", match);
                }
            } finally { doc.close(); }
        }
    }

    @Test public void smallInterCharacterGapCanSelectParagraph() {
        CPDFDocument doc = open("urethral");
        try {
            CPDFTextPage text = doc.pageAtIndex(1).getTextPage();
            PointF point = chineseCharacterGap(text);
            assertEquals(-1, text.getCharIndexAtPos(point, 0, 0));
            CPDFTapTextSelectionController.ParagraphMatch match = CPDFTapTextSelectionController.findParagraphRange(
                    text, point);
            assertNotNull("A small gap between body characters should still select their paragraph", match);
            assertEquals(599, match.range.location);
            assertEquals(301, match.range.length);
            assertNotNull(match.selectionPoint);
            assertEquals(634, text.getCharIndexAtPos(match.selectionPoint, 0, 0));
            // Gutters, margins, and inter-line whitespace must not snap to nearby paragraphs.
            for (PointF blank : new PointF[]{new PointF(295, 400), new PointF(1, 400),
                    new PointF(191.3F, 397.5F), new PointF(191.3F, 382.5F)}) {
                assertNull("Whitespace must remain unselected: " + blank,
                        CPDFTapTextSelectionController.findParagraphRange(text, blank));
            }
            assertFalse(doc.hasChanges());
        } finally { doc.close(); }
    }

    private static PointF chineseCharacterGap(CPDFTextPage text) {
        RectF first = new RectF(text.getCharBox(633)); first.sort();
        RectF second = new RectF(text.getCharBox(634)); second.sort();
        assertTrue(second.left > first.right);
        return new PointF((first.right + second.left) * 0.5F,
                (Math.max(first.top, second.top) + Math.min(first.bottom, second.bottom)) * 0.5F);
    }

    @Test public void characterGapCreatesSelectionInAnnotModeAtDifferentScales() throws Exception {
        PointF point = new PointF(190.05756F, 387.17456F);
        verifySelection("urethral", 1, 599, 900, 1F, point);
        verifySelection("urethral", 1, 599, 900, 2.0553544F, point);
    }

    @Test public void characterGapShowsSelectionOnActualDocumentScreen() throws Exception {
        captureScreen("urethral", 1, 599, 900, new PointF(190.05756F, 387.17456F));
    }
    @Test public void englishBothColumnsAndSuperscripts() {
        golden("svm",3,new int[][]{{214,556},{581,957},{988,1567},{1567,2053},
                {2053,2299},{2299,3026},{3066,3571},{3619,3946},{3996,4378}});
    }
    @Test public void chinesePageContinuation() {
        golden("urethral",2,new int[][]{{0,41},{41,106},{117,262}});
    }
    @Test public void chineseAbstract() {
        golden("urethral",0,new int[][]{{294,827}});
    }
    @Test public void referenceHangingIndentAndVolumeNumbers() {
        golden("urethral",4,new int[][]{{64,211},{211,421},{677,855}});
    }

    @Test public void allCustomerPagesRemainReadOnlyAndRangesContainHit() throws Exception {
        JSONObject report=new JSONObject();
        for(String name:new String[]{"svm","urethral"}) {
            CPDFDocument doc=open(name);
            List<Long> times=new ArrayList<>();
            int probes=0,selected=0,multiple=0;
            try {
                for(int pi=0;pi<doc.getPageCount();pi++){
                    CPDFPage page=doc.pageAtIndex(pi);
                    CPDFTextPage text=page.getTextPage();
                    CPDFTextSelection[] lines=text.getSelectionsByLineForRange(new CPDFTextRange(0,text.getCountChars()));
                    if(lines==null)continue;
                    for(int li=0;li<lines.length;li+=5){
                        CPDFTextRange range=lines[li].getTextRange();
                        PointF point=visiblePoint(text,range.location,range.location+range.length);
                        if(point==null)continue;
                        int hit=text.getCharIndexAtPos(point,0,0);
                        long start=SystemClock.elapsedRealtimeNanos();
                        CPDFTapTextSelectionController.ParagraphMatch match=CPDFTapTextSelectionController.findParagraphRange(text,point);
                        times.add((SystemClock.elapsedRealtimeNanos()-start)/1000);
                        probes++;
                        if(match==null)continue;
                        assertTrue(match.range.location>=0);
                        assertTrue(match.range.length>0);
                        assertTrue(match.range.location+match.range.length<=text.getCountChars());
                        assertTrue(match.range.location<=hit && hit<match.range.location+match.range.length);
                        selected++;
                        if(match.lineCount>1)multiple++;
                    }
                    assertFalse(name+" page="+pi,doc.hasChanges());
                    assertNull(page.getCachedEditPage());
                }
                Collections.sort(times);
                report.put(name,new JSONObject().put("pages",doc.getPageCount()).put("probes",probes)
                        .put("matches",selected).put("multipleLines",multiple)
                        .put("medianMicros",times.get(times.size()/2))
                        .put("p95Micros",times.get((int)(times.size()*0.95)))
                        .put("maxMicros",times.get(times.size()-1)));
            } finally {doc.close();}
        }
        try(Writer w=new OutputStreamWriter(new FileOutputStream(new File(
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(),
                "paragraph-validation.json")),"UTF-8")){w.write(report.toString(2));}
    }

    @Test public void callbackCreatesRealSelectionInBothPdfFiles() throws Exception {
        verifySelection("urethral",1,1350,1570,1F);
        verifySelection("urethral",1,1570,2137,1.6F);
        verifySelection("svm",3,581,957,1F);
        verifySelection("svm",3,3066,3571,1.4F);
    }

    private static void main(Runnable action) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action);
    }
    private static void layout(CPDFReaderView reader){
        reader.measure(View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1800,View.MeasureSpec.EXACTLY));
        reader.layout(0,0,1080,1800);
    }
    private static void verifySelection(String name,int pageIndex,int start,int end,float scale) throws Exception {
        verifySelection(name, pageIndex, start, end, scale, null);
    }

    private static void verifySelection(String name,int pageIndex,int start,int end,float scale,
                                        PointF explicitPoint) throws Exception {
        verifySelection(name, pageIndex, start, end, scale, explicitPoint, 0);
    }

    // -1: constructor default AUTO; 0: PARAGRAPH; 1/2: force preceding stages to fail.
    private static void verifySelection(String name,int pageIndex,int start,int end,float scale,
                                        PointF explicitPoint, int forcedFailures) throws Exception {
        CPDFDocument doc=open(name);
        AtomicReference<CPDFReaderView> ref=new AtomicReference<>();
        AtomicReference<CPDFTapTextSelectionController> exampleRef=new AtomicReference<>();
        try {
            main(()->{
                CPDFReaderView reader=new CPDFReaderView(InstrumentationRegistry.getInstrumentation().getTargetContext());
                ref.set(reader);
                reader.setPDFDocument(doc);
                reader.setViewMode(explicitPoint == null ? CPDFReaderView.ViewMode.VIEW : CPDFReaderView.ViewMode.ANNOT);
                layout(reader);
            });
            SystemClock.sleep(500);
            main(()->{layout(ref.get());ref.get().setDisplayPageIndex(pageIndex);});
            SystemClock.sleep(500);
            main(()->{layout(ref.get());ref.get().setScale(scale);});
            SystemClock.sleep(500);
            main(()->{
                CPDFReaderView reader=ref.get();layout(reader);
                CPDFPageView view=null;
                for(int i=0;i<reader.getChildCount();i++){
                    CPDFPageView child=(CPDFPageView)reader.getChildAt(i);
                    if(child.getPageNum()==pageIndex){view=child;break;}
                }
                assertNotNull(view);
                CPDFPage page=doc.pageAtIndex(pageIndex);
                CPDFTextPage text=page.getTextPage();
                PointF hit=explicitPoint == null ? visiblePoint(text,start,end) : explicitPoint;
                if (explicitPoint != null) assertEquals(-1, text.getCharIndexAtPos(hit, 0, 0));
                CPDFTapTextSelectionController example=new CPDFTapTextSelectionController(reader);
                exampleRef.set(example);
                if (forcedFailures == 0) example.setSelectionMode(CPDFTapTextSelectionController.SelectionMode.PARAGRAPH);
                example.attach();
                PointF readerPoint = reader.convertPointFromPage(page, hit.x, hit.y);
                String expectedText = compact(text.getText(new CPDFTextRange(start, end - start)));
                if (forcedFailures <= 0) {
                    assertTrue(example.onPageTap(readerPoint));
                } else {
                    // Inject failure only at the preceding stage; line/word selection still uses the actual SDK.
                    final CPDFPageView pageView = view;
                    if (forcedFailures == 1) {
                        CPDFTextSelection line = text.getSelectionForLineAtPos(hit);
                        assertNotNull(line);
                        expectedText = compact(text.getText(line.getTextRange()));
                    } else {
                        CPDFTextSelection[] words = text.getSelectionForWordPos(hit);
                        assertNotNull(words);
                        StringBuilder selectedWord = new StringBuilder();
                        for (CPDFTextSelection word : words) selectedWord.append(text.getText(word.getTextRange()));
                        expectedText = compact(selectedWord.toString());
                    }
                    assertTrue(CPDFTapTextSelectionController.selectWithFallback(
                            CPDFTapTextSelectionController.SelectionMode.AUTO,
                            () -> false,
                            () -> forcedFailures == 1 && invokeSelection(example, "selectLine", pageView, readerPoint, hit),
                            () -> invokeSelection(example, "selectWord", pageView, readerPoint, hit)));
                }
                CPDFPageSelections selection=(CPDFPageSelections)view.getSelectionHelper();
                assertFalse(selection.getSelections().isEmpty());
                assertFalse(selection.getSelectionsRect().isEmpty());
                StringBuilder actual=new StringBuilder();
                for(CPDFTextSelection selected:selection.getSelections())actual.append(text.getText(selected.getTextRange()));
                assertEquals("Displayed selection must match the successful selection stage",
                        expectedText, compact(actual.toString()));
                assertNotNull(reader.getTextSelectionSnapshot(pageIndex));
                assertFalse(doc.hasChanges());
                // Gutter regression: a blank tap must not select another paragraph or modify the document.
                CPDFTextRange before = reader.getTextSelectionSnapshot(pageIndex);
                PointF gutter = new PointF(295, 400);
                assertNull(CPDFTapTextSelectionController.findParagraphRange(text, gutter));
                assertTrue(example.onPageTap(reader.convertPointFromPage(page, gutter.x, gutter.y)));
                CPDFTextRange after = reader.getTextSelectionSnapshot(pageIndex);
                assertNotNull(after);
                assertEquals(before.location, after.location);
                assertEquals(before.length, after.length);
                assertFalse(doc.hasChanges());
            });
        } finally {
            main(()->{if(exampleRef.get()!=null)exampleRef.get().detach();if(ref.get()!=null)ref.get().removeAllViews();});
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            doc.close();
        }
    }
    @Test public void defaultAutoSelectsParagraphInBothDocuments() throws Exception {
        verifySelection("urethral", 1, 599, 900, 1F, null, -1);
        verifySelection("svm", 3, 581, 957, 1F, null, -1);
    }

    @Test public void autoFallbackCreatesRealLineAndWordSelections() throws Exception {
        verifySelection("svm", 3, 581, 957, 1F, null, 1);
        verifySelection("svm", 3, 581, 957, 1F, null, 2);
    }

    private static boolean invokeSelection(CPDFTapTextSelectionController example, String method,
                                           CPDFPageView page, PointF readerPoint, PointF pdfPoint) {
        try {
            Method target;
            if ("selectLine".equals(method)) {
                target = CPDFTapTextSelectionController.class.getDeclaredMethod(method,
                        CPDFPageView.class, PointF.class, PointF.class);
                target.setAccessible(true);
                return (Boolean) target.invoke(example, page, readerPoint, pdfPoint);
            }
            target = CPDFTapTextSelectionController.class.getDeclaredMethod(method, CPDFPageView.class, PointF.class);
            target.setAccessible(true);
            return (Boolean) target.invoke(example, page, readerPoint);
        } catch (ReflectiveOperationException error) {
            throw new AssertionError("Actual SDK selection failed", error);
        }
    }

    @Test public void actualDocumentScreenShowsParagraphSelection() throws Exception {
        captureScreen("urethral",1,1350,1570);
        captureScreen("svm",3,3066,3571);
    }

    private static void captureScreen(String name,int pageIndex,int start,int end) throws Exception {
        captureScreen(name, pageIndex, start, end, null);
    }

    private static void captureScreen(String name,int pageIndex,int start,int end,
                                      PointF explicitPoint) throws Exception {
        android.app.Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();
        android.content.Context context=instrumentation.getTargetContext();
        android.app.Instrumentation.ActivityMonitor monitor=instrumentation.addMonitor(
                ParagraphPreviewActivity.class.getName(),null,false);
        String command="am start -W -n "+context.getPackageName()+"/"+ParagraphPreviewActivity.class.getName()
                +" --es file_path "+new File(context.getFilesDir(),name+".pdf").getAbsolutePath()
                +" --ei extra_page_index "+pageIndex;
        try (android.os.ParcelFileDescriptor commandOutput=instrumentation.getUiAutomation().executeShellCommand(command);
             InputStream input=new android.os.ParcelFileDescriptor.AutoCloseInputStream(commandOutput)) {
            byte[] buffer=new byte[1024];
            while(input.read(buffer)!=-1) { }
        }
        CPDFDocumentActivity activity=(CPDFDocumentActivity)instrumentation.waitForMonitorWithTimeout(monitor,8000);
        instrumentation.removeMonitor(monitor);
        assertNotNull("The test-only preview Activity did not start",activity);
        AtomicReference<CPDFDocumentFragment> fragmentRef=new AtomicReference<>();
        AtomicReference<CPDFTapTextSelectionController> controllerRef=new AtomicReference<>();
        try {
            long deadline=SystemClock.uptimeMillis()+8000;
            do {
                main(()->{
                    CPDFDocumentFragment f=(CPDFDocumentFragment)activity.getSupportFragmentManager().findFragmentByTag("documentFragment");
                    if(f!=null && f.pdfView!=null && f.pdfView.getCPdfReaderView().getPDFDocument()!=null
                            && f.pdfView.getCPdfReaderView().getChildCount()>0)fragmentRef.set(f);
                });
                if(fragmentRef.get()!=null)break;
                SystemClock.sleep(100);
            } while(SystemClock.uptimeMillis()<deadline);
            assertNotNull(fragmentRef.get());
            CPDFReaderView reader=fragmentRef.get().pdfView.getCPdfReaderView();
            main(()->{
                reader.setContinueMode(false);reader.setDisplayPageIndex(pageIndex);
                if (explicitPoint != null) reader.setViewMode(CPDFReaderView.ViewMode.ANNOT);
            });
            SystemClock.sleep(800);
            AtomicReference<PointF> screenPoint=new AtomicReference<>();
            main(()->{
                CPDFPage page=reader.getPDFDocument().pageAtIndex(pageIndex);
                PointF pdf=explicitPoint == null ? visiblePoint(page.getTextPage(),start,end) : explicitPoint;
                PointF point=reader.convertPointFromPage(page,pdf.x,pdf.y);
                int[] origin=new int[2];reader.getLocationOnScreen(origin);
                point.offset(origin[0],origin[1]);screenPoint.set(point);
            });
            // The production Fragment does not install tap selection. Opt in only for this test.
            main(()->{
                assertNull(reader.getTextSelectionSnapshot(pageIndex));
                CPDFTapTextSelectionController controller=new CPDFTapTextSelectionController(reader);
                controllerRef.set(controller);
                controller.attach();
            });
            PointF p=screenPoint.get();long time=SystemClock.uptimeMillis();
            android.view.MotionEvent down=android.view.MotionEvent.obtain(time,time,0,p.x,p.y,0);
            android.view.MotionEvent up=android.view.MotionEvent.obtain(time,time+60,1,p.x,p.y,0);
            try{instrumentation.sendPointerSync(down);instrumentation.sendPointerSync(up);}
            finally{down.recycle();up.recycle();}
            SystemClock.sleep(800);
            main(()->{
                CPDFTextRange range=reader.getTextSelectionSnapshot(pageIndex);assertNotNull(range);
                CPDFTextPage tp=reader.getPDFDocument().pageAtIndex(pageIndex).getTextPage();
                assertEquals(compact(tp.getText(new CPDFTextRange(start,end-start))),compact(tp.getText(range)));
                assertFalse(reader.getPDFDocument().hasChanges());
            });
            android.graphics.Bitmap screenshot=instrumentation.getUiAutomation().takeScreenshot();
            assertNotNull(screenshot);
            try(OutputStream out=new FileOutputStream(new File(context.getFilesDir(),"paragraph-"+name
                    +(explicitPoint == null ? "" : "-gap")+".png"))){
                assertTrue(screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG,100,out));
            } finally{screenshot.recycle();}
        } finally {
            main(()->{
                if(controllerRef.get()!=null)controllerRef.get().detach();
                activity.finish();
            });
            instrumentation.waitForIdleSync();
        }
    }

    private static String compact(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch != 0 && !Character.isWhitespace(ch) && !Character.isSpaceChar(ch)) result.append(ch);
        }
        return result.toString();
    }
}
