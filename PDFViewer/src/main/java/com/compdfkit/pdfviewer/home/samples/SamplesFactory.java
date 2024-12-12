/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.samples;


import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.compdfkit.pdfviewer.home.HomeFunBean;

public class SamplesFactory {

    private String filePath;

    private Uri uri;

    private Fragment fragment;

    public SamplesFactory(Fragment fragment, String filePath, Uri uri){
        this.filePath = filePath;
        this.uri = uri;
        this.fragment = fragment;
    }

    public FeaturesSamples getImpl(HomeFunBean.FunType funType){
        switch (funType){
            case Security:
                DocumentEncryptionSamplesImpl documentEncryptionSamples = new DocumentEncryptionSamplesImpl(fragment, funType);
                documentEncryptionSamples.setPDFFile(filePath, uri, null);
                return documentEncryptionSamples;
            case Watermark:
                WatermarkSamplesImpl watermarkSamples = new WatermarkSamplesImpl(fragment, funType);
                watermarkSamples.setPDFFile(filePath, uri, null);
                return watermarkSamples;
            case Compress:
                CompressSamplesImpl compressSamples = new CompressSamplesImpl(fragment, funType);
                compressSamples.setPDFFile(filePath, uri, null);
                return compressSamples;
            default:
                // Viewer、Annotations、Forms、Signatures、DocumentEditor、ContentEditor
                // open pdf activity show pdf file
                OpenPDFSamplesImpl openPDFSamples = new OpenPDFSamplesImpl(fragment, funType);
                openPDFSamples.setPDFFile(filePath, uri, null);
                return openPDFSamples;
        }
    }

}
