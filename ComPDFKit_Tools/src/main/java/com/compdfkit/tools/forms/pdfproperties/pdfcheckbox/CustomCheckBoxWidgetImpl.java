/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfcheckbox;

import com.compdfkit.core.annotation.form.CPDFCheckboxWidget;
import com.compdfkit.tools.forms.pdfproperties.CFormWidgetActionInterceptor;
import com.compdfkit.ui.proxy.form.CPDFCheckboxWidgetImpl;

public class CustomCheckBoxWidgetImpl extends CPDFCheckboxWidgetImpl {

    @Override
    public void onCheckBoxFocused(CPDFCheckboxWidget checkboxWidget) {
        if (CFormWidgetActionInterceptor.intercept(onGetAnnotation())) {
            return;
        }
        super.onCheckBoxFocused(checkboxWidget);
    }
}
