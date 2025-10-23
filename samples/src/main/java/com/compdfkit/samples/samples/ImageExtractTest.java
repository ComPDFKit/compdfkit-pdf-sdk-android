/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;


public class ImageExtractTest extends PDFSamples {

    public ImageExtractTest(){
        setTitle(R.string.image_extract_test_title);
        setDescription(R.string.image_extract_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        printDividingLine();
        outputListener.println("Samples 1: Extract all images in the document");
        outputListener.println("Opening the Samples PDF File");
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "ImageExtractTest.pdf"));

        File extractDir = new File(outputDir(), "imageExtract/ImageExtractTest");
        extractDir.delete();
        extractDir.mkdirs();
        document.extractImage(extractDir.getAbsolutePath(), (pageIndex, imageIndex, index) -> {
            //Customize the returned file name
            return document.getFileName() + "_" + pageIndex +"_" + imageIndex;
        });
        File[] images = extractDir.listFiles();
        if (images != null && images.length>0) {
            for (File image : images) {
                getOutputFileList().add(image.getAbsolutePath());
                outputListener.println(image.getName());
            }
        }
        document.close();
        printFooter();
    }
}
