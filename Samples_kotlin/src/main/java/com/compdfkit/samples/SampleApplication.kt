/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */
package com.compdfkit.samples

import android.app.Application
import com.compdfkit.samples.samples.AnnotationImportExportTest
import com.compdfkit.samples.samples.AnnotationReplyTest
import com.compdfkit.samples.samples.AnnotationTest
import com.compdfkit.samples.samples.BackgroundTest
import com.compdfkit.samples.samples.BatesTest
import com.compdfkit.samples.samples.BookmarkTest
import com.compdfkit.samples.samples.ContentEditorTest
import com.compdfkit.samples.samples.DigitalSignaturesTest
import com.compdfkit.samples.samples.DocumentInfoTest
import com.compdfkit.samples.samples.EncryptTest
import com.compdfkit.samples.samples.FlattenTest
import com.compdfkit.samples.samples.HeaderFooterTest
import com.compdfkit.samples.samples.ImageExtractTest
import com.compdfkit.samples.samples.InteractiveFormsTest
import com.compdfkit.samples.samples.OutlineTest
import com.compdfkit.samples.samples.PDFATest
import com.compdfkit.samples.samples.PDFPageTest
import com.compdfkit.samples.samples.PDFRedactTest
import com.compdfkit.samples.samples.PDFToImageTest
import com.compdfkit.samples.samples.TextExtractTest
import com.compdfkit.samples.samples.TextSearchTest
import com.compdfkit.samples.samples.WatermarkTest

class SampleApplication : Application() {

    @JvmField
    var samplesList: MutableList<PDFSamples> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        instance = this
        samplesList.clear()
        samplesList.add(DigitalSignaturesTest())
        samplesList.add(BookmarkTest())
        samplesList.add(OutlineTest())
        samplesList.add(PDFToImageTest())
        samplesList.add(TextSearchTest())
        samplesList.add(AnnotationTest())
        samplesList.add(AnnotationImportExportTest())
        samplesList.add(AnnotationReplyTest())
        samplesList.add(InteractiveFormsTest())
        samplesList.add(PDFPageTest())
        samplesList.add(ImageExtractTest())
        samplesList.add(TextExtractTest())
        samplesList.add(DocumentInfoTest())
        samplesList.add(WatermarkTest())
        samplesList.add(BackgroundTest())
        samplesList.add(HeaderFooterTest())
        samplesList.add(BatesTest())
        samplesList.add(PDFRedactTest())
        samplesList.add(EncryptTest())
        samplesList.add(PDFATest())
        samplesList.add(FlattenTest())
        samplesList.add(ContentEditorTest())
    }

    companion object {
        lateinit var instance: SampleApplication
            private set
    }
}