/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.samples;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.compdfkit.pdfviewer.CPDFConfiguration;
import com.compdfkit.pdfviewer.CPDFConfigurationUtils;
import com.compdfkit.pdfviewer.MainActivity;
import com.compdfkit.pdfviewer.home.HomeFunBean;
import com.compdfkit.pdfviewer.home.datas.FunDatas;
import com.compdfkit.pdfviewer.home.datas.SettingDatas;
import com.compdfkit.tools.common.utils.CFileUtils;
import com.compdfkit.tools.common.utils.CLog;
import com.compdfkit.tools.common.views.pdfview.CPreviewMode;

import java.io.File;
import java.util.Arrays;

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
        Intent intent = new Intent(fragment.getContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_FILE_PATH, filePath);
        intent.setData(uri);
        intent.putExtra(MainActivity.EXTRA_FILE_PASSWORD, password);
        intent.putExtra(MainActivity.EXTRA_CONFIGURATION, FunDatas.getConfiguration(fragment.getContext(), getPreviewMode()));
        fragment.startActivity(intent);
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
