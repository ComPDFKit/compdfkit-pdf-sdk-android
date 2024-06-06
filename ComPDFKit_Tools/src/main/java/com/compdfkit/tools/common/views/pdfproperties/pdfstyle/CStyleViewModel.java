/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.views.pdfproperties.pdfstyle;

import androidx.lifecycle.ViewModel;


public class CStyleViewModel extends ViewModel {

    private CAnnotStyle annotStyle;

    public void setStyle(CAnnotStyle style) {
        this.annotStyle = style;
    }

    public CAnnotStyle getStyle() {
        return annotStyle;
    }

    public void addStyleChangeListener(CAnnotStyle.OnAnnotStyleChangeListener listener){
        if (annotStyle != null) {
            annotStyle.addStyleChangeListener(listener);
        }
    }

    public void removeStyleChangeListener(CAnnotStyle.OnAnnotStyleChangeListener styleChangeListener){
        if (annotStyle != null) {
            annotStyle.removeStyleChangeListener(styleChangeListener);
        }
    }
}
