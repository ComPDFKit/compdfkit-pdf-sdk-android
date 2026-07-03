package com.compdfkit.tools.forms.pdfproperties.pdfpushbutton;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.forms.pdfproperties.CFormWidgetActionInterceptor;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;


public class CPushButtonWidgetImpl extends CPDFPushbuttonWidgetImpl {

    @Override
    public void doAction(CPDFDocument document) {
        if (CFormWidgetActionInterceptor.intercept(onGetAnnotation())) {
            return;
        }
        super.doAction(document);
    }
}
