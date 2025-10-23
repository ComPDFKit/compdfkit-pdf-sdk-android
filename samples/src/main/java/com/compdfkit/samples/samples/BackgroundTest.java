/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.compdfkit.core.annotation.CPDFImageScaleType;
import com.compdfkit.core.document.CPDFBackground;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;

public class BackgroundTest extends PDFSamples {

    public BackgroundTest(){
        setTitle(R.string.background_test_title);
        setDescription(R.string.background_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        addColorBackground();
        addImageBackground();
        deleteBackground();
        printFooter();
    }


    private void addColorBackground(){
        outputListener.println();
        outputListener.println("Samples 1 : Set the document background color");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFBackground background = document.getBackground();
        background.setType(CPDFBackground.Type.Color);
        background.setColor(Color.RED);
        background.setOpacity(1F);
        background.setPages("0,1,2,3,4");
        printBackgroundInfo(background);
        background.update();
        background.release();
        File file = new File(outputDir(), "backgroundTest/AddColorBackgroundTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in AddColorBackgroundTest.pdf");
        printDividingLine();
    }

    private void addImageBackground(){
        outputListener.println("Samples 2 : Set the document background image");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        CPDFBackground background = document.getBackground();
        background.setType(CPDFBackground.Type.Image);
        Bitmap backgroundImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_foreground);
        background.setImage(backgroundImage, CPDFImageScaleType.SCALETYPE_center);
        background.setOpacity(1F);
        background.setPages("0,1,2,3,4");
        printBackgroundInfo(background);
        background.update();
        background.release();
        File file = new File(outputDir(), "backgroundTest/AddImageBackgroundTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in AddImageBackgroundTest.pdf");
        printDividingLine();
    }

    /**
     * Sample 3 : Delete document background
     */
    private void deleteBackground(){
        outputListener.println("Samples 3 : Delete document background");
        CPDFDocument document = new CPDFDocument(context);
        File sampleFile = new File(outputDir(),
                "backgroundTest/AddColorBackgroundTest.pdf");
        document.open(sampleFile.getAbsolutePath());
        CPDFBackground background = document.getBackground();
        //remove all pages background
        background.clear();
        //update pages
        //background.setPages("0,2,3,4");
        //background.update();
        //background.release();
        File file = new File(outputDir(), "backgroundTest/DeleteBackgroundTest.pdf");
        saveSamplePDF(document, file, true);
        outputListener.println("Done. Result saved in DeleteBackgroundTest.pdf");
    }

    private void printBackgroundInfo(CPDFBackground background){
        outputListener.println("Type : " + background.getType().name());
        if (background.getType() == CPDFBackground.Type.Color){
            outputListener.println(String.format("Color : red:%d, green:%d, blue:%d, alpha:%d",
                    Color.red(background.getColor()),
                    Color.green(background.getColor()),
                    Color.blue(background.getColor()),
                    Color.alpha(background.getColor())));
        }
        outputListener.println("Opacity : " + background.getOpacity());
        outputListener.println("Rotation : " + background.getRotation());
        outputListener.println("Vertalign : " + background.getVertalign().name());
        outputListener.println("Horizalign : " + background.getHorizalign().name());
        outputListener.println("VertOffset : " + background.getXOffset());
        outputListener.println("HorizOffset : " + background.getYOffset());
        outputListener.println("Pages : " + background.getPages());
    }
}
