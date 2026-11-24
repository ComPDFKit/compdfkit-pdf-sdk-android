package com.compdfkit.tools.forms.pdfproperties.pdfpushbutton;

import com.compdfkit.tools.forms.pdfproperties.CFormWidgetActionInterceptor;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;


public class CPushButtonWidgetImpl extends CPDFPushbuttonWidgetImpl {

    @Override
    protected boolean onInterceptButtonAction() {
        return CFormWidgetActionInterceptor.intercept(readerView, onGetAnnotation());
    }
}
