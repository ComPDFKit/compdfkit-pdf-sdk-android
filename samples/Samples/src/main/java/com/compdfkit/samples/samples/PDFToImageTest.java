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
import android.graphics.Color;
import android.graphics.RectF;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;
import com.compdfkit.ui.utils.CPDFBitmapUtils;

import java.io.File;

public class PDFToImageTest extends PDFSamples {

    public PDFToImageTest() {
        setTitle(R.string.pdf_to_image_test_title);
        setDescription(R.string.pdf_to_image_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        outputListener.println();
        CPDFDocument document = new CPDFDocument(context);
        String assetsName = "CommonFivePage.pdf";
        document.open(FileUtils.getAssetsTempFile(context, assetsName));
        // Render each page of the document as an image
        for (int i = 0; i < document.getPageCount(); i++) {
            // page size
            RectF size = document.getPageSize(i);
            Bitmap bitmap = Bitmap.createBitmap((int) size.width(), (int) size.height(), Bitmap.Config.RGB_565);
            boolean success = document.renderPageAtIndex(
                    bitmap,
                    i,
                    (int) size.width(),
                    (int) size.height(),
                    0,
                    0,
                    (int) size.width(),
                    (int) size.height(),
                    Color.WHITE,
                    255,
                    0,
                    true,
                    true
            );
            if (success) {
                File file = new File(outputDir(), "PDFToImageTest/" + FileUtils.getNameWithoutExtension(assetsName) + "/PDFToImageTest" + i + ".png");
                file.getParentFile().mkdirs();
                // save bitmap
                CPDFBitmapUtils.saveBitmap(bitmap, file.getAbsolutePath());
                outputListener.println("Done.");
                if (file.exists()) {
                    outputListener.println("Done. Results saved in " + file.getName());
                    addFileList(file.getAbsolutePath());
                }
            }
        }
        document.close();
        printFooter();
    }
}
