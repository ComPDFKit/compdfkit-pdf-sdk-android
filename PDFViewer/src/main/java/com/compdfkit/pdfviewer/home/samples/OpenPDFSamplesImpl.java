/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.samples;


import static com.compdfkit.tools.common.pdf.CPDFDocumentActivity.EXTRA_CONFIGURATION;
import static com.compdfkit.tools.common.pdf.CPDFDocumentActivity.EXTRA_FILE_PASSWORD;
import static com.compdfkit.tools.common.pdf.CPDFDocumentActivity.EXTRA_FILE_PATH;
import static com.compdfkit.tools.common.pdf.CPDFDocumentActivity.EXTRA_PAGE_INDEX;
import static com.compdfkit.tools.common.pdf.CPDFDocumentFragment.EXTRA_FILE_URI;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.compdfkit.pdfviewer.home.HomeFunBean;
import com.compdfkit.pdfviewer.home.datas.FunDatas;
import com.compdfkit.tools.common.pdf.CPDFDocumentActivity;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

public class OpenPDFSamplesImpl extends BasicFeaturesSamples {

    protected String filePath;

    protected Uri uri;

    protected String password;


    public OpenPDFSamplesImpl(Fragment fragment, HomeFunBean.FunType funType) {
        super(fragment, funType);
    }

    public void setPDFFile(String filePath, Uri uri, String password) {
        this.filePath = filePath;
        this.uri = uri;
        this.password = password;
    }

    @Override
    public void run() {
        startPDFActivity(filePath, uri, password);
    }

    protected void startPDFActivity(String filePath, Uri uri, String password) {
        Intent intent = new Intent(fragment.getContext(), CPDFDocumentActivity.class);
        intent.setData(uri);
        intent.putExtra(EXTRA_FILE_PASSWORD, password);
        intent.putExtra(EXTRA_CONFIGURATION, FunDatas.getConfiguration(fragment.getContext(), getPreviewMode()));
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.putExtra(EXTRA_PAGE_INDEX, 0);
        intent.putExtra(EXTRA_FILE_URI, uri);
        fragment.getContext().startActivity(intent);
    }

    protected CPreviewMode getPreviewMode() {
        switch (funType) {
            case Viewer:
                return CPreviewMode.Viewer;
            case Annotations:
                return CPreviewMode.Annotation;
            case Forms:
                return CPreviewMode.Form;
            case Signature:
                return CPreviewMode.Signature;
            case DocumentEditor:
                return CPreviewMode.PageEdit;
            case ContentEditor:
                return CPreviewMode.Edit;
            default:
                return CPreviewMode.Viewer;
        }
    }
}
