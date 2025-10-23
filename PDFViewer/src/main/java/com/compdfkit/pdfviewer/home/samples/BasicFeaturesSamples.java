/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.pdfviewer.home.samples;


import androidx.fragment.app.Fragment;

import com.compdfkit.pdfviewer.home.HomeFunBean;

public abstract class BasicFeaturesSamples implements FeaturesSamples {

    protected Fragment fragment;

    protected HomeFunBean.FunType funType;

    public BasicFeaturesSamples(Fragment fragment, HomeFunBean.FunType funType){
        this.fragment = fragment;
        this.funType = funType;
    }
}
