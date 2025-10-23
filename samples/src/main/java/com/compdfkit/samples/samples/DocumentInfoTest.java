/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.samples.samples;

import android.os.ParcelFileDescriptor;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.core.document.CPDFInfo;
import com.compdfkit.samples.PDFSamples;
import com.compdfkit.samples.R;
import com.compdfkit.samples.util.DateUtil;
import com.compdfkit.samples.util.FileUtils;
import com.compdfkit.samples.util.OutputListener;


public class DocumentInfoTest extends PDFSamples {

    public DocumentInfoTest(){
        setTitle(R.string.document_test_title);
        setDescription(R.string.document_test_desc);
    }

    @Override
    protected void run(OutputListener outputListener) {
        super.run(outputListener);
        printHead();
        CPDFDocument document = new CPDFDocument(context);
        document.open(FileUtils.getAssetsTempFile(context, "CommonFivePage.pdf"));
        outputListener.println();
        // Obtain basic information of pdf documents
        CPDFInfo cpdfInfo = document.getInfo();
        outputListener.println("FileName : " + document.getFileName());
        outputListener.println("FileSize : " + getDocumentSize(document));
        outputListener.println("Title : " + cpdfInfo.getTitle());
        outputListener.println("Author : " + cpdfInfo.getAuthor());
        outputListener.println("Subject : " + cpdfInfo.getSubject());
        outputListener.println("Keywords : " + cpdfInfo.getKeywords());

        outputListener.println("Version : " + document.getMajorVersion());
        outputListener.println("PageCount : " + document.getPageCount());
        outputListener.println("Creator : " + cpdfInfo.getCreator());
        outputListener.println("CreationDate : " + DateUtil.transformPDFDate(cpdfInfo.getCreationDate()));
        outputListener.println("ModificationDate : "+ DateUtil.transformPDFDate(cpdfInfo.getModificationDate()));

        // Get pdf document permission information
        CPDFDocumentPermissionInfo permissionInfo = document.getPermissionsInfo();
        outputListener.println("Printing : " + permissionInfo.isAllowsPrinting());
        outputListener.println("Content Copying : " + permissionInfo.isAllowsCopying());
        outputListener.println("Document Change : " + permissionInfo.isAllowsDocumentChanges());
        outputListener.println("Document Assembly : " + permissionInfo.isAllowsDocumentAssembly());
        outputListener.println("Document Commenting : " + permissionInfo.isAllowsCommenting());
        outputListener.println("Document Filling of form field : " + permissionInfo.isAllowsFormFieldEntry());
        document.close();
        printFooter();
    }


    private String getDocumentSize(CPDFDocument document) {
        final long MB = 1024 * 1024;
        final int KB = 1024;
        long fileSize = 0L;
        try {
            ParcelFileDescriptor p = document.getContext().getContentResolver().openFileDescriptor(document.getUri(), "r");
            fileSize = p.getStatSize();
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float size;
        String unit = " M";
        if (fileSize > MB) {
            size = ((float) fileSize / (1024 * 1024));
        } else if (fileSize > KB) {
            size = ((float) fileSize / 1024);
            unit = " KB";
        } else {
            size = fileSize;
            unit = " B";
        }
        return String.format("%.2f", size) + unit;
    }
}
