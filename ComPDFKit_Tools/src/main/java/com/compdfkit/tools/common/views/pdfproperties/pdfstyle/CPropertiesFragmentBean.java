/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;

import androidx.annotation.StringRes;

import com.compdfkit.tools.common.views.pdfproperties.basic.CBasicPropertiesFragment;


public class CPropertiesFragmentBean {

    private Class<? extends CBasicPropertiesFragment> fragmentClass;

    private int titleResId;

    public CPropertiesFragmentBean(@StringRes int titleRes, Class<? extends CBasicPropertiesFragment> fragmentClass) {
        this.titleResId = titleRes;
        this.fragmentClass = fragmentClass;
    }

    public Class<? extends CBasicPropertiesFragment> getFragmentClass() {
        return fragmentClass;
    }

    public void setFragmentClass(Class<? extends CBasicPropertiesFragment> claz) {
        this.fragmentClass = claz;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public void setTitleResId(int titleResId) {
        this.titleResId = titleResId;
    }
}
