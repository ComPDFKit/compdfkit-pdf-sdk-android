package com.compdfkit.tools.annotation.pdfproperties.pdflink;


import android.util.Log;
import com.compdfkit.core.annotation.CPDFLinkAnnotation;
import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.ui.proxy.CPDFLinkAnnotImpl;
import java.util.Map;

public class CPDFCustomLinkAnnotImpl extends CPDFLinkAnnotImpl {


    @Override
    public void doAction(CPDFDocument document, CPDFLinkAnnotation linkAnnotation) {
        CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
        if (configuration != null && configuration.annotationsConfig != null && configuration.annotationsConfig.interceptLinkAction) {
            Map<String, Object> extraMap = new java.util.HashMap<>();
            extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.INTERCEPT_ANNOTATION_DO_ACTION);
            extraMap.put(CPDFCustomEventField.ANNOTATION, linkAnnotation);
            CPDFCustomEventCallbackHelper.getInstance().notifyClick("", extraMap);
            return;
        }
        super.doAction(document, linkAnnotation);
    }
}
