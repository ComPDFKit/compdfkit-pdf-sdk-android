/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties;

import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.pdf.config.FormsConfig;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import java.util.HashMap;
import java.util.Map;

public class CFormWidgetActionInterceptor {

    public static boolean intercept(CPDFWidget widget) {
        if (!shouldIntercept(widget)) {
            return false;
        }
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE,
                CPDFCustomEventType.INTERCEPT_WIDGET_DO_ACTION);
        extraMap.put(CPDFCustomEventField.WIDGET, widget);
        CPDFCustomEventCallbackHelper.getInstance().notifyClick("", extraMap);
        return true;
    }

    private static boolean shouldIntercept(CPDFWidget widget) {
        if (widget == null) {
            return false;
        }
        CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
        if (configuration == null || configuration.formsConfig == null) {
            return false;
        }
        FormsConfig formsConfig = configuration.formsConfig;
        return formsConfig.interceptAllFormWidgetActions
                || formsConfig.interceptFormWidgetActions.contains(widget.getWidgetType());
    }
}
