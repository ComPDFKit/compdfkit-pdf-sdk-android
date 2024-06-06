/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.view.span;

import android.os.Build;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;

import androidx.annotation.NonNull;


public class CUnderLineColorSpan  extends UnderlineSpan {

    private int color;

    public CUnderLineColorSpan(int color){
        this.color = color;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ds.underlineColor =  color;
            ds.underlineThickness = 3;
        }
        ds.setAntiAlias(true);
        ds.setUnderlineText(true);
    }
}
