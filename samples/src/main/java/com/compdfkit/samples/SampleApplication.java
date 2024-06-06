/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples;


import android.app.Application;

import com.compdfkit.samples.samples.AnnotationImportExportTest;
import com.compdfkit.samples.samples.AnnotationTest;
import com.compdfkit.samples.samples.BackgroundTest;
import com.compdfkit.samples.samples.BatesTest;
import com.compdfkit.samples.samples.BookmarkTest;
import com.compdfkit.samples.samples.DigitalSignaturesTest;
import com.compdfkit.samples.samples.DocumentInfoTest;
import com.compdfkit.samples.samples.EncryptTest;
import com.compdfkit.samples.samples.FlattenTest;
import com.compdfkit.samples.samples.HeaderFooterTest;
import com.compdfkit.samples.samples.ImageExtractTest;
import com.compdfkit.samples.samples.InteractiveFormsTest;
import com.compdfkit.samples.samples.OutlineTest;
import com.compdfkit.samples.samples.PDFATest;
import com.compdfkit.samples.samples.PDFPageTest;
import com.compdfkit.samples.samples.PDFRedactTest;
import com.compdfkit.samples.samples.PDFToImageTest;
import com.compdfkit.samples.samples.TextExtractTest;
import com.compdfkit.samples.samples.TextSearchTest;
import com.compdfkit.samples.samples.WatermarkTest;

import java.util.ArrayList;
import java.util.List;

public class SampleApplication extends Application {

    private static SampleApplication application;

    public List<PDFSamples> samplesList = new ArrayList<>();


    public static SampleApplication getInstance(){
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        samplesList.clear();
        samplesList.add(new DigitalSignaturesTest());
        samplesList.add(new BookmarkTest());
        samplesList.add(new OutlineTest());
        samplesList.add(new PDFToImageTest());
        samplesList.add(new TextSearchTest());
        samplesList.add(new AnnotationTest());
        samplesList.add(new AnnotationImportExportTest());
        samplesList.add(new InteractiveFormsTest());
        samplesList.add(new PDFPageTest());
        samplesList.add(new ImageExtractTest());
        samplesList.add(new TextExtractTest());
        samplesList.add(new DocumentInfoTest());
        samplesList.add(new WatermarkTest());
        samplesList.add(new BackgroundTest());
        samplesList.add(new HeaderFooterTest());
        samplesList.add(new BatesTest());
        samplesList.add(new PDFRedactTest());
        samplesList.add(new EncryptTest());
        samplesList.add(new PDFATest());
        samplesList.add(new FlattenTest());
    }
}
