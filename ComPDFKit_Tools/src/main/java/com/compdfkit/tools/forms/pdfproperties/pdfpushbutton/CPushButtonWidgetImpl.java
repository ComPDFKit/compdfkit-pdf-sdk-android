package com.compdfkit.tools.forms.pdfproperties.pdfpushbutton;

import com.compdfkit.core.document.CPDFDocument;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.ui.proxy.form.CPDFPushbuttonWidgetImpl;
import java.util.HashMap;
import java.util.Map;


public class CPushButtonWidgetImpl extends CPDFPushbuttonWidgetImpl {

    @Override
    public void doAction(CPDFDocument document) {
        CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
        if (configuration != null && configuration.formsConfig != null && configuration.formsConfig.interceptPushButtonAction) {
            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.INTERCEPT_WIDGET_DO_ACTION);
            extraMap.put(CPDFCustomEventField.WIDGET, onGetAnnotation());
            CPDFCustomEventCallbackHelper.getInstance().notifyClick("", extraMap);
            return;
        }
        super.doAction(document);
    }
}
