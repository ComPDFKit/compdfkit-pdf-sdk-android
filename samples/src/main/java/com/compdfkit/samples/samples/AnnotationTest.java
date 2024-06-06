/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.CPDFBorderStyle;
import com.compdfkit.core.annotation.CPDFCircleAnnotation;
import com.compdfkit.core.annotation.CPDFFreetextAnnotation;
import com.compdfkit.core.annotation.CPDFHighlightAnnotation;
import com.compdfkit.core.annotation.CPDFInkAnnotation;
import com.compdfkit.core.annotation.CPDFLineAnnotation;
import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.annotation.CPDFSoundAnnotation;
import com.compdfkit.core.annotation.CPDFSquareAnnotation;
import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.core.annotation.CPDFTextAnnotation;
import com.compdfkit.core.annotation.CPDFTextAttribute;
import com.compdfkit.core.document.CPDFDestination;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.action.CPDFGoToAction;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.SampleApplication;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AnnotationTest extends PDFSamples {

    public AnnotationTest() {
        setTitle(R.string.annotation_test_title);
        setDescription(R.string.annotation_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();

        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));

        printDividingLine();
        //------------------------------------------
        //Samples 1 : add freetext annotation
        addFreeText(document);

        //------------------------------------------
        //Samples 2 : add ink annotation
        addInk(document);

        //------------------------------------------
        //Samples 3 : add line annotation
        addLine(document);

        //------------------------------------------
        //Samples 4 : add circle annotation
        addCircleShape(document);

        //------------------------------------------
        //Samples 5 : add square annotation
        addSquareShape(document);

        //------------------------------------------
        //Samples 6 : add highlight(markup) annotation
        addHighlight(document);

        //------------------------------------------
        //Samples 7 : add link annotation
        addLink(document);

        //------------------------------------------
        //Samples 8 : add note annotation
        addNote(document);

        //------------------------------------------
        //Samples 8 : add sound annotation
        addSound(document);

        //------------------------------------------
        //Samples 9 : add stamp annotation
        addStamp(document);

        File file = new File(outputDir(), "AnnotationTest/CreateAnnotationTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done.");
        outputListener.println("\nDone. Result saved in CreateAnnotationTest.pdf");
        printDividingLine();

        //------------------------------------------
        //Samples 10 : print annotation list info
        printAnnotationList();

        //------------------------------------------
        //Samples 11 : delete annotation
        deleteAnnotation();

        printFooter();
    }

    /**
     * Samples 1 : add freetext annotation
     *
     * @param document
     */
    private void addFreeText(CPDFDocument document) {
        // Insert the free text annotation into the first page of the PDF document.
        CPDFPage page1 = document.pageAtIndex(0);
        CPDFFreetextAnnotation freetextAnnotation = (CPDFFreetextAnnotation) page1.addAnnot(CPDFAnnotation.Type.FREETEXT);
        RectF freeText1Rect = getConvertRect(page1, new RectF(10, 200, 160, 570));
        freetextAnnotation.setRect(freeText1Rect);
        // set text alignment
        freetextAnnotation.setFreetextAlignment(CPDFFreetextAnnotation.Alignment.ALIGNMENT_LEFT);
        // Set text font, bold, italic, color and font size
        CPDFTextAttribute textAttribute = new CPDFTextAttribute(CPDFTextAttribute.FontNameHelper.obtainFontName(
                CPDFTextAttribute.FontNameHelper.FontType.Courier, false, false
        ), 12, Color.RED);

        freetextAnnotation.setFreetextDa(textAttribute);
        // set text color opacity
        freetextAnnotation.setAlpha(255);
        freetextAnnotation.setContent("Some swift brown fox snatched a gray hare out of the air by freezing it with an angry glare.\n" +
                "\n" +
                "Aha!\n" +
                "\n" +
                "And there was much rejoicing!");
        freetextAnnotation.updateAp();
    }

    /**
     * Samples 2 : add ink annotation
     *
     * @param document
     */
    private void addInk(CPDFDocument document) {
        // Insert the ink annotation into the first page of the PDF document.
        CPDFPage page1 = document.pageAtIndex(0);
        ArrayList<ArrayList<PointF>> mDrawing = new ArrayList<>();
        ArrayList<PointF> arc1 = new ArrayList<>();
        arc1.add(new PointF(100, 100));
        arc1.add(new PointF(110, 110));
        arc1.add(new PointF(120, 120));
        mDrawing.add(arc1);
        ArrayList<PointF> arc2 = new ArrayList<>();
        arc2.add(new PointF(115, 115));
        arc2.add(new PointF(130, 130));
        arc2.add(new PointF(160, 160));
        mDrawing.add(arc2);
        float scaleValue = 1F;
        float borderWidth = 5F;
        CPDFInkAnnotation inkAnnotation = (CPDFInkAnnotation) page1.addAnnot(CPDFInkAnnotation.Type.INK);
        inkAnnotation.setColor(Color.RED);
        inkAnnotation.setAlpha(255);
        inkAnnotation.setBorderWidth(borderWidth);
        RectF rect = null;
        RectF size = document.getPageSize(0);
        if (size.isEmpty()) {
            return;
        }
        int lineCount = mDrawing.size();
        PointF[][] path = new PointF[lineCount][];
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            ArrayList<PointF> line = mDrawing.get(lineIndex);
            int pointCount = line.size();
            PointF[] linePath = new PointF[pointCount];
            for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
                PointF point = line.get(pointIndex);
                // Calculate the smallest Rect that the Path is surrounded by
                if (rect == null) {
                    rect = new RectF(point.x, point.y, point.x, point.y);
                } else {
                    rect.union(point.x, point.y);
                }
                // Calculate the coordinate points that are converted to the Page and stored in the linePath collection
                linePath[pointIndex] = (page1.convertPointToPage(false, size.width(), size.height(), point));
            }
            path[lineIndex] = linePath;
        }
        float dx = borderWidth / scaleValue / 2;
        rect.inset(-dx, -dx);
        rect.set(page1.convertRectToPage(false, size.width(), size.height(), rect));
        inkAnnotation.setInkPath(path);
        inkAnnotation.setRect(rect);
        inkAnnotation.updateAp();
        mDrawing.clear();
    }

    /**
     * Samples 3 : add line shape annotation
     *
     * @param document
     */
    private void addLine(CPDFDocument document) {
        // Add a green dotted line annotation
        CPDFPage page2 = document.pageAtIndex(1);
        CPDFLineAnnotation lineAnnotation = (CPDFLineAnnotation) page2.addAnnot(CPDFAnnotation.Type.LINE);
        // line start point
        PointF startPoint = new PointF(200, 100);
        // line end point
        PointF endPoint = new PointF(50, 300);
        RectF area = new RectF();
        // Get the position of the line on the page
        convertLinePoint(page2, startPoint, endPoint, area);
        lineAnnotation.setRect(area);
        lineAnnotation.setLinePoints(startPoint, endPoint);
        // Sets the arrowhead shape at both ends of the line
        lineAnnotation.setLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE, CPDFLineAnnotation.LineType.LINETYPE_NONE);
        // Set line to dash and spacer width
        CPDFBorderStyle borderStyle = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Dashed, 10, new float[]{8, 4F});
        lineAnnotation.setBorderStyle(borderStyle);
        // set line width
        lineAnnotation.setBorderWidth(3);
        lineAnnotation.setBorderAlpha(255);
        lineAnnotation.setBorderColor(Color.GREEN);
        lineAnnotation.updateAp();

        //Add a solid red line annotation with an arrow type
        CPDFLineAnnotation lineAnnotation2 = (CPDFLineAnnotation) page2.addAnnot(CPDFAnnotation.Type.LINE);
        PointF startPoint2 = new PointF(200, 350);
        PointF endPoint2 = new PointF(50, 550);
        RectF area2 = new RectF();
        convertLinePoint(page2, startPoint2, endPoint2, area2);
        lineAnnotation2.setRect(area2);
        lineAnnotation2.setLinePoints(startPoint2, endPoint2);
        // Set the start position of the arrow as circle and the end of the arrow as the arrow type
        lineAnnotation2.setLineType(CPDFLineAnnotation.LineType.LINETYPE_CIRCLE, CPDFLineAnnotation.LineType.LINETYPE_ARROW);
        // set line to solid
        CPDFBorderStyle borderStyle2 = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10, new float[]{8, 0});
        lineAnnotation2.setBorderStyle(borderStyle2);
        lineAnnotation2.setBorderWidth(5);
        lineAnnotation2.setBorderAlpha(255);
        lineAnnotation2.setBorderColor(Color.RED);
        lineAnnotation2.updateAp();

        // Add a solid red line annotation
        CPDFLineAnnotation lineAnnotation3 = (CPDFLineAnnotation) page2.addAnnot(CPDFAnnotation.Type.LINE);
        PointF startPoint3 = new PointF(400, 100);
        PointF endPoint3 = new PointF(250, 300);
        RectF area3 = new RectF();
        convertLinePoint(page2, startPoint3, endPoint3, area3);
        lineAnnotation3.setRect(area3);
        lineAnnotation3.setLinePoints(startPoint3, endPoint3);
        lineAnnotation3.setLineType(CPDFLineAnnotation.LineType.LINETYPE_NONE, CPDFLineAnnotation.LineType.LINETYPE_NONE);
        CPDFBorderStyle borderStyle3 = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10, new float[]{8, 0});
        lineAnnotation3.setBorderStyle(borderStyle3);
        lineAnnotation3.setBorderWidth(5);
        lineAnnotation3.setBorderAlpha(255);
        lineAnnotation3.setBorderColor(Color.BLUE);
        lineAnnotation3.updateAp();
    }

    private void convertLinePoint(CPDFPage page, PointF startPoint, PointF endPoint, RectF area) {
        RectF pageSize = page.getSize();
        float lineWidth = 10f;
        area.left = Math.min(startPoint.x, endPoint.x);
        area.right = Math.max(startPoint.x, endPoint.x);
        area.top = Math.min(startPoint.y, endPoint.y);
        area.bottom = Math.max(startPoint.y, endPoint.y);
        area.left -= lineWidth * 2;
        area.top -= lineWidth * 2;
        area.right += lineWidth * 2;
        area.bottom += lineWidth * 2;
        area.set(page.convertRectToPage(false, pageSize.width(), pageSize.height(), area));
        startPoint.set(page.convertPointToPage(false, pageSize.width(), pageSize.height(), startPoint));
        endPoint.set(page.convertPointToPage(false, pageSize.width(), pageSize.height(), endPoint));
    }

    /**
     * Samples 4 : add circle shape annotation
     *
     * @param document
     */
    private void addCircleShape(CPDFDocument document) {
        // Add a circular annotation with a green border and blue fill
        CPDFPage page3 = document.pageAtIndex(2);
        CPDFCircleAnnotation circleAnnotation = (CPDFCircleAnnotation) page3.addAnnot(CPDFAnnotation.Type.CIRCLE);
        RectF insertRect = getConvertRect(page3, new RectF(50, 50, 150, 150));
        circleAnnotation.setRect(insertRect);
        // set border color
        circleAnnotation.setBorderColor(Color.parseColor("#3863F1"));
        // Set border to solid line
        CPDFBorderStyle borderStyle = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10, new float[]{8.0F, 0F});
        borderStyle.setBorderWidth(5F);
        circleAnnotation.setBorderStyle(borderStyle);
        circleAnnotation.setBorderAlpha(255);
        circleAnnotation.setFillColor(Color.parseColor("#31BC98"));
        circleAnnotation.setFillAlpha(255);
        circleAnnotation.updateAp();

        //Add a circular shape annotation with a dotted border.
        CPDFCircleAnnotation circleAnnotation2 = (CPDFCircleAnnotation) page3.addAnnot(CPDFAnnotation.Type.CIRCLE);
        RectF insertRect2 = getConvertRect(page3, new RectF(50, 200, 150, 300));
        circleAnnotation2.setRect(insertRect2);
        circleAnnotation2.setBorderColor(Color.parseColor("#3863F1"));
        CPDFBorderStyle borderStyle2 = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Dashed, 10, new float[]{8.0F, 4F});
        borderStyle2.setBorderWidth(5F);
        circleAnnotation2.setBorderStyle(borderStyle2);
        circleAnnotation2.setBorderAlpha(127);
        circleAnnotation2.setFillColor(Color.parseColor("#31BC98"));
        circleAnnotation2.setFillAlpha(127);
        circleAnnotation2.updateAp();
    }

    /**
     * Samples 5 : add square shape annotation
     *
     * @param document
     */
    private void addSquareShape(CPDFDocument document) {
        // Add a rectangle with a blue border and green fill
        CPDFPage page3 = document.pageAtIndex(2);
        CPDFSquareAnnotation squareAnnotation = (CPDFSquareAnnotation) page3.addAnnot(CPDFAnnotation.Type.SQUARE);
        RectF pageSize = page3.getSize();
        RectF insertRect = getConvertRect(page3, new RectF(50, 350, 300, 450));
        squareAnnotation.setRect(insertRect);
        squareAnnotation.setBorderColor(Color.parseColor("#3863F1"));
        CPDFBorderStyle borderStyle = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Solid, 10, new float[]{8.0F, 0F});
        borderStyle.setBorderWidth(10F);
        squareAnnotation.setBorderStyle(borderStyle);
        squareAnnotation.setBorderAlpha(255);
        squareAnnotation.setFillColor(Color.parseColor("#31BC98"));
        squareAnnotation.setFillAlpha(255);
        squareAnnotation.updateAp();

        // Add a rectangle with a blue dotted border and a green fill with 50% transparency
        CPDFSquareAnnotation squareAnnotation2 = (CPDFSquareAnnotation) page3.addAnnot(CPDFAnnotation.Type.SQUARE);
        RectF insertRect2 = new RectF(50, 500, 300, 600);
        insertRect2 = page3.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect2);
        squareAnnotation2.setRect(insertRect2);
        squareAnnotation2.setBorderColor(Color.parseColor("#3863F1"));
        CPDFBorderStyle borderStyle2 = new CPDFBorderStyle(CPDFBorderStyle.Style.Border_Dashed, 10, new float[]{8.0F, 4F});
        borderStyle2.setBorderWidth(10F);
        squareAnnotation2.setBorderStyle(borderStyle2);
        squareAnnotation2.setBorderAlpha(127);
        squareAnnotation2.setFillColor(Color.parseColor("#31BC98"));
        squareAnnotation2.setFillAlpha(127);
        squareAnnotation2.updateAp();
    }

    /**
     * Samples 6 : add high annotation
     * Here is a demonstration of searching out keywords in the search page and adding highlighted annotation
     *
     * @param document
     */
    private void addHighlight(CPDFDocument document) {
        //Also search for the `Page` keyword in the 3rd of the document
        CPDFPage pdfPage = document.pageAtIndex(3);
        //Then, add a highlight annotation for the specific area.
        RectF annotRect = new RectF(315, 258, 372, 288);
        annotRect = pdfPage.convertRectToPage(false, pdfPage.getSize().width(), pdfPage.getSize().height(), annotRect);
        CPDFHighlightAnnotation highlightAnnotation = (CPDFHighlightAnnotation) pdfPage.addAnnot(CPDFAnnotation.Type.HIGHLIGHT);
        highlightAnnotation.setColor(Color.YELLOW);
        highlightAnnotation.setAlpha((255 / 2));
        highlightAnnotation.setQuadRects(new RectF[]{annotRect});
        highlightAnnotation.setMarkedText("Page");
        highlightAnnotation.setRect(annotRect);
        highlightAnnotation.updateAp();
        outputListener.println(annotRect.toString());
    }


    /**
     * Samples 7 : add link annotation
     *
     * @param document
     */
    private void addLink(CPDFDocument document) {
        CPDFPage page = document.pageAtIndex(3);
        CPDFLinkAnnotation linkAnnotation = (CPDFLinkAnnotation) page.addAnnot(CPDFAnnotation.Type.LINK);
        RectF insertRect = getConvertRect(page, new RectF(50, 50, 150, 150));
        linkAnnotation.setRect(insertRect);
        float firstPageHeight = document.getPageSize(0).height();
        // Add page jump link action
        CPDFGoToAction goToAction = new CPDFGoToAction();
        CPDFDestination destination = new CPDFDestination(1, 0, firstPageHeight, 1f);
        goToAction.setDestination(document, destination);
        linkAnnotation.setLinkAction(goToAction);
        linkAnnotation.updateAp();
    }

    /**
     * Samples 8 : add note annotation
     *
     * @param document
     */
    private void addNote(CPDFDocument document) {
        CPDFPage page = document.pageAtIndex(3);
        CPDFTextAnnotation textAnnotation = (CPDFTextAnnotation) page.addAnnot(CPDFAnnotation.Type.TEXT);
        //get the actual size of the page you want to insert
        RectF insertRect = getConvertRect(page, new RectF(50, 200, 100, 250));
        textAnnotation.setRect(insertRect);
        textAnnotation.setContent("ComPDFKit");
        textAnnotation.updateAp();
    }

    /**
     * Samples 9 : add sound annotation
     *
     * @param document
     */
    private void addSound(CPDFDocument document) {
        CPDFPage page = document.pageAtIndex(3);
        CPDFSoundAnnotation soundAnnotation = (CPDFSoundAnnotation) page.addAnnot(CPDFAnnotation.Type.SOUND);
        RectF insertRect = getConvertRect(page, new RectF(50, 300, 100, 350));
        soundAnnotation.setRect(insertRect);
        soundAnnotation.setSoundPath(FileUtils.getAssetsTempFile(SampleApplication.getInstance(), "Bird.wav"));
        soundAnnotation.updateAp();
    }

    /**
     * Samples 10 : add stamp annotation
     *
     * @param document
     */
    private void addStamp(CPDFDocument document) {
        // add standard stamp annotation
        int yOffset = 50;
        float lastOffset = 0;
        for (int i = 0; i < CPDFStampAnnotation.StandardStamp.values().length; i++) {
            CPDFPage page = document.pageAtIndex(4);
            CPDFStampAnnotation.StandardStamp standardStamp = CPDFStampAnnotation.StandardStamp.values()[i];
            if (standardStamp == null || standardStamp == CPDFStampAnnotation.StandardStamp.UNKNOWN) {
                continue;
            }
            // add Standard stamp
            CPDFStampAnnotation standard = (CPDFStampAnnotation) page.addAnnot(CPDFAnnotation.Type.STAMP);
            if (standard == null) {
                continue;
            }
            standard.setStandardStamp(standardStamp);
            RectF pageSize = page.getSize();
            RectF insertRect = standard.getRect();
            insertRect.set(page.convertRectFromPage(false, pageSize.width(), pageSize.height(), insertRect));
            float defaultWidth = 100F;
            int x = 50;
            if (i == 10) {
                lastOffset = 50;
            }
            if (i >= 10) {
                x = 150;
            }
            yOffset = (int) lastOffset + 10;
            PointF vertex = new PointF(x, yOffset);
            insertRect.set(vertex.x, vertex.y, vertex.x + defaultWidth, vertex.y + defaultWidth * Math.abs(insertRect.height() / insertRect.width()));
            lastOffset = insertRect.bottom;
            standard.setRect(page.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect));
            standard.updateAp();
        }

        //add text stamp annotations
        CPDFPage page = document.pageAtIndex(4);
        CPDFStampAnnotation stampAnnotation = (CPDFStampAnnotation) page.addAnnot(CPDFAnnotation.Type.STAMP);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        stampAnnotation.setTextStamp(new CPDFStampAnnotation.TextStamp(
                "ComPDFKit", date, CPDFStampAnnotation.TextStampShape.TEXTSTAMP_RECT.id,
                CPDFStampAnnotation.TextStampColor.TEXTSTAMP_GREEN.id));
        RectF pageSize = page.getSize();
        RectF insertRect = stampAnnotation.getRect();
        insertRect.set(page.convertRectFromPage(false, pageSize.width(), pageSize.height(), insertRect));
        float defaultWidth = 150f;
        PointF vertex = new PointF(300, 50);
        insertRect.set(vertex.x, vertex.y, vertex.x + defaultWidth, vertex.y + defaultWidth * Math.abs(insertRect.height() / insertRect.width()));
        stampAnnotation.setRect(page.convertRectToPage(false, pageSize.width(), pageSize.height(), insertRect));
        stampAnnotation.updateAp();

        // add image stamp annotations
        CPDFStampAnnotation standard = (CPDFStampAnnotation) page.addAnnot(CPDFAnnotation.Type.STAMP);
        String imagePath = FileUtils.getAssetsTempFile(context, "ComPDFKit.png");
        float imageDefaultWidth = 100F;
        PointF imageVertex = new PointF(300, 300);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        RectF imageInsertRect = new RectF(imageVertex.x, imageVertex.y, imageVertex.x + imageDefaultWidth, imageVertex.y + imageDefaultWidth * options.outHeight / options.outWidth);
        RectF pageSize1 = page.getSize();
        imageInsertRect.set(page.convertRectToPage(false, pageSize1.width(), pageSize1.height(), imageInsertRect));
        standard.setRect(imageInsertRect);
        if (imagePath.endsWith("png")) {
            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            tmpOptions.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, tmpOptions);
            standard.updateApWithBitmap(bitmap);
            bitmap.recycle();
        } else {
            standard.setImageStamp(imagePath);
            standard.updateAp();
        }
    }

    private RectF getConvertRect(CPDFPage page, RectF rectF) {
        RectF size = page.getSize();
        return page.convertRectToPage(false, size.width(), size.height(), rectF);
    }

    /**
     * Samples 10 : print annotation list info
     */
    private void printAnnotationList() {
        printDividingLine();
        File sampleFile = new File(outputDir(), "AnnotationTest/CreateAnnotationTest.pdf");
        CPDFDocument document = new CPDFDocument(context);
        document.open(sampleFile.getAbsolutePath());
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFPage page = document.pageAtIndex(i);
            for (CPDFAnnotation annotation : page.getAnnotations()) {
                outputListener.println("Page: " + (i + 1));
                outputListener.println("Annot Type: " + annotation.getType().name());
                RectF widgetRect = annotation.getRect();
                RectF position = page.convertRectFromPage(false, document.getPageSize(i).width(),
                        document.getPageSize(i).height(), widgetRect);
                outputListener.println(String.format("Position: %d, %d, %d, %d", (int) position.left, (int) position.top,
                        (int) position.right, (int) position.bottom));
                printDividingLine();
            }
        }
        document.close();
    }

    /**
     * Samples 11 : delete annotation
     */
    private void deleteAnnotation() {
        File sampleFile = new File(outputDir(), "AnnotationTest/CreateAnnotationTest.pdf");
        CPDFDocument document = new CPDFDocument(context);
        document.open(sampleFile.getAbsolutePath());
        for (int i = 0; i < document.getPageCount(); i++) {
            CPDFPage page = document.pageAtIndex(i);
            List<CPDFAnnotation> annotations = page.getAnnotations();
            if (annotations != null && annotations.size() > 0) {
                page.deleteAnnotation(annotations.get(0));
                break;
            }
        }
        File file = new File(outputDir(), "AnnotationTest/DeleteAnnotationTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done.");
        outputListener.println("Done. Results saved in DeleteAnnotationTest.pdf");
    }
}
