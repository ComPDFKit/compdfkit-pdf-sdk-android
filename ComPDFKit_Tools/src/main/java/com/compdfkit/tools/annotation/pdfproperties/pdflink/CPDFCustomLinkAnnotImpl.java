package com.compdfkit.tools.annotation.pdfproperties.pdflink;


import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.ui.proxy.CPDFLinkAnnotImpl;

public class CPDFCustomLinkAnnotImpl extends CPDFLinkAnnotImpl {


    @Override
    public void doAction(CPDFDocument document, CPDFLinkAnnotation linkAnnotation) {
        if (com.compdfkit.tools.annotation.pdfproperties.CAnnotationActionInterceptor.intercept(readerView, linkAnnotation)) {
            return;
        }
        super.doAction(document, linkAnnotation);
    }
}
