/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
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

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.watermark.CPDFWatermark;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class WatermarkTest extends PDFSamples {

    public WatermarkTest() {
        setTitle(R.string.watermark_test_title);
        setDescription(R.string.watermark_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        addTextWatermark();
        addImageWatermark();
        addTilesWatermark();
        deleteWatermark();
        printFooter();
    }

    /**
     * Samples 1 : Insert a text watermark in the center of all pages of the document
     */
    private void addTextWatermark() {
        printDividingLine();
        outputListener.println("Sample 1 : Insert Text Watermark");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFWatermark watermark = document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_TEXT);
        watermark.setText("ComPDFKit");
        watermark.setTextRGBColor(Color.RED);
        watermark.setFontSize(30);
        watermark.setOpacity(0.5F);
        watermark.setRotation(45);
        watermark.setVertalign(CPDFWatermark.Vertalign.WATERMARK_VERTALIGN_CENTER);
        watermark.setHorizalign(CPDFWatermark.Horizalign.WATERMARK_HORIZALIGN_CENTER);
        watermark.setVertOffset(0);
        watermark.setHorizOffset(0);
        watermark.setPages("0,1,2,3,4");
        printWatermarkInfo(watermark);
        watermark.update();
        watermark.release();
        File file = new File(outputDir(), "watermarkTest/AddTextWatermarkTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in AddTextWatermarkTest.pdf");
        printDividingLine();
    }

    /**
     * Samples 2 : Insert a picture watermark in the center of all pages of the document
     */
    private void addImageWatermark() {
        outputListener.println("Sample 2 : Insert Image Watermark");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFWatermark watermark = document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_IMG);
        Bitmap watermarkImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_foreground);
        watermark.setImage(watermarkImage, 100, 100);
        watermark.setOpacity(1F);
        watermark.setRotation(20);
        watermark.setVertalign(CPDFWatermark.Vertalign.WATERMARK_VERTALIGN_CENTER);
        watermark.setHorizalign(CPDFWatermark.Horizalign.WATERMARK_HORIZALIGN_CENTER);
        watermark.setVertOffset(0);
        watermark.setHorizOffset(0);
        watermark.setPages("0,1,2,3,4");
        printWatermarkInfo(watermark);
        watermark.update();
        watermark.release();
        File file = new File(outputDir(), "watermarkTest/AddImageWatermarkTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in AddImageWatermarkTest.pdf");
        printDividingLine();
    }

    /**
     * Samples 3 : Insert a tiled text watermark on all pages of the document
     */
    private void addTilesWatermark() {
        outputListener.println("Sample 3 : Insert Text Tiles Watermark");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFWatermark watermark = document.createWatermark(CPDFWatermark.Type.WATERMARK_TYPE_TEXT);
        watermark.setText("ComPDFKit");
        watermark.setTextRGBColor(Color.RED);
        watermark.setFontSize(30);
        watermark.setOpacity(0.5F);
        watermark.setRotation(45);
        watermark.setVertalign(CPDFWatermark.Vertalign.WATERMARK_VERTALIGN_CENTER);
        watermark.setHorizalign(CPDFWatermark.Horizalign.WATERMARK_HORIZALIGN_CENTER);
        watermark.setVertOffset(0);
        watermark.setHorizOffset(0);
        watermark.setPages("0,1,2,3,4");
        watermark.setFullScreen(true);
        watermark.setHorizontalSpacing(100);
        watermark.setVerticalSpacing(100);
        printWatermarkInfo(watermark);
        watermark.update();
        watermark.release();
        File file = new File(outputDir(), "watermarkTest/AddTilesWatermarkTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in AddTilesWatermarkTest.pdf");
        printDividingLine();
    }

    private void deleteWatermark() {
        outputListener.println("Sample 4 : Delete Watermark");
        CPDFDocument document = new CPDFDocument(context);
        File sampleFile = new File(outputDir(), "watermarkTest/AddTextWatermarkTest.pdf");
        document.open(sampleFile.getAbsolutePath());
        CPDFWatermark watermark = document.getWatermark(0);
        if (watermark != null) {
            //remove all page watermarks
            watermark.clear();

            // Remove the watermark on the second page
//            watermark.setPages("0,2,3,4");
//            watermark.update();
//            watermark.release();
        }
        File file = new File(outputDir(), "watermarkTest/DeleteWatermarkTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in DeleteWatermarkTest.pdf");
        printDividingLine();
    }

    private void printWatermarkInfo(CPDFWatermark watermark){
        if (watermark.getType() == CPDFWatermark.Type.WATERMARK_TYPE_TEXT){
            outputListener.println("Text : "+ watermark.getText());
            outputListener.println(String.format("Color : red:%d, green:%d, blue:%d, alpha:%d",
                    Color.red(watermark.getTextRGBColor()),
                    Color.green(watermark.getTextRGBColor()),
                    Color.blue(watermark.getTextRGBColor()),
                    Color.alpha(watermark.getTextRGBColor())));

            outputListener.println("FontSize : " + watermark.getFontSize());
        }
        outputListener.println("Opacity : " + watermark.getOpacity());
        outputListener.println("Rotation : " + watermark.getRotation());
        outputListener.println("Vertalign : " + watermark.getVertalign().name());
        outputListener.println("Horizalign : " + watermark.getHorizalign().name());
        outputListener.println("VertOffset : " + watermark.getVertOffset());
        outputListener.println("HorizOffset : " + watermark.getHorizOffset());
        outputListener.println("Pages : " + watermark.getPages());
        outputListener.println("VerticalSpacing : " + watermark.getVerticalSpacing());
        outputListener.println("HorizontalSpacing : " + watermark.getHorizontalSpacing());
    }
}
