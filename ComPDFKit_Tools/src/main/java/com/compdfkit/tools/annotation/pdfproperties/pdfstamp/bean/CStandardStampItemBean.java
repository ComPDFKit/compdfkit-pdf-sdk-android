/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.annotation.pdfproperties.pdfstamp.bean;


import androidx.annotation.DrawableRes;

import com.compdfkit.core.annotation.CPDFStampAnnotation;

public class CStandardStampItemBean {

    private CPDFStampAnnotation.StandardStamp standardStamp;

    private int imageResId;

    public CStandardStampItemBean(CPDFStampAnnotation.StandardStamp standardStamp, @DrawableRes int imageResId){
        this.standardStamp = standardStamp;
        this.imageResId = imageResId;
    }

    public CPDFStampAnnotation.StandardStamp getStandardStamp() {
        return standardStamp;
    }

    public void setStandardStamp(CPDFStampAnnotation.StandardStamp standardStamp) {
        this.standardStamp = standardStamp;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
