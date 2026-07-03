/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfradiobutton;

import com.compdfkit.core.annotation.form.CPDFRadiobuttonWidget;
import com.compdfkit.tools.forms.pdfproperties.CFormWidgetActionInterceptor;
import com.compdfkit.ui.proxy.form.CPDFRadiobuttonWidgetImpl;

public class CustomRadioButtonWidgetImpl extends CPDFRadiobuttonWidgetImpl {
    @Override
    public void onRadioButtonFocused(CPDFRadiobuttonWidget radiobuttonWidget) {
        if (CFormWidgetActionInterceptor.intercept(onGetAnnotation())) {
            return;
        }
        super.onRadioButtonFocused(radiobuttonWidget);
    }
}
