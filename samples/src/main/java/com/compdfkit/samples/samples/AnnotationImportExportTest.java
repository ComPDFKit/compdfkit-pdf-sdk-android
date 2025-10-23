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
import com.compdfkit.samples.SampleApplication;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;

import java.io.File;

public class AnnotationImportExportTest extends PDFSamples {

    public AnnotationImportExportTest(){
        setTitle(R.string.annotation_import_export_test_title);
        setDescription(R.string.annotation_import_export_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        exportAnnotation();
        importAnnotation();
        printFooter();
    }

    /**
     * Samples 1 : export pdf document annotations
     */
    private void exportAnnotation(){
        printDividingLine();
        // Open the pdf document containing comments that needs to be exported
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "Annotations.pdf"));
        // Set annotations export path, cache directory
        File exportFile = new File(outputDir(), "AnnotationImportExportTest/ExportAnnotationTest.xfdf");
        File cacheDir = new File(context.getCacheDir(), "AnnotationImportExportTest/");
        // To create a directory, please ensure that the path exists.
        exportFile.getParentFile().mkdirs();
        cacheDir.mkdirs();
        document.exportAnnotations(exportFile.getAbsolutePath(), cacheDir.getAbsolutePath());
        document.close();
        if (exportFile.exists()){
            getOutputFileList().add(exportFile.getAbsolutePath());
        }
        outputListener.println("Done.");
        outputListener.println("Done. Results saved in ExportAnnotationTest.xfdf");
    }

    /**
     * Samples 2 : Import a previously exported comment file into a blank document
     */
    private void importAnnotation(){
        // Open the pdf document containing comments that needs to be exported
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        // Get imported annotation files
        File importFile = new File(outputDir(), "AnnotationImportExportTest/ExportAnnotationTest.xfdf");
        File cacheDir = new File(SampleApplication.getInstance().getCacheDir(), "AnnotationImportExportTest/");
        cacheDir.mkdirs();
        document.importAnnotations(importFile.getAbsolutePath(), cacheDir.getAbsolutePath());
        File file = new File(outputDir(), "AnnotationImportExportTest/ImportAnnotationTest.pdf");
        saveSamplePDF(document, file, true);

        outputListener.println("Done.");
        outputListener.println("Done. Results saved in ImportAnnotationTest.pdf");
    }
}
