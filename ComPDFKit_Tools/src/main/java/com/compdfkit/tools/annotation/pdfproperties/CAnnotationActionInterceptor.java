package com.compdfkit.tools.annotation.pdfproperties;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.common.pdf.CPDFActionContext;
import com.compdfkit.ui.reader.ReaderView;

public final class CAnnotationActionInterceptor {
    private CAnnotationActionInterceptor() {}

    public static boolean intercept(ReaderView reader, CPDFAnnotation annotation) {
        CPDFActionContext context = CPDFActionContext.get(reader);
        return context != null && !(annotation instanceof CPDFWidget) && context.intercept(annotation);
    }
}
