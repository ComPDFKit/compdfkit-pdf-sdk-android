/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdflistbox;


import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.form.CPDFListboxWidget;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.forms.pdfproperties.option.select.CFormOptionSelectDialogFragment;
import com.compdfkit.ui.proxy.form.CPDFListboxWidgetImpl;
import java.util.HashMap;
import java.util.Map;

public class CustomListBoxWidgetImpl extends CPDFListboxWidgetImpl {


    @Override
    public void onListboxFocused(CPDFListboxWidget cpdfListboxWidget) {
        CPDFConfiguration configuration = CPDFApplyConfigUtil.getInstance().getConfiguration();
        if (configuration != null && configuration.formsConfig != null && configuration.formsConfig.interceptListBoxAction) {
            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.INTERCEPT_WIDGET_DO_ACTION);
            extraMap.put(CPDFCustomEventField.WIDGET, cpdfListboxWidget);
            CPDFCustomEventCallbackHelper.getInstance().notifyClick("", extraMap);
            return;
        }
         CFormOptionSelectDialogFragment selectDialogFragment = CFormOptionSelectDialogFragment.newInstance();
         selectDialogFragment.setPdfWidgetItems(cpdfListboxWidget);
         selectDialogFragment.setSelectOptionListener((selectedIndexs) -> {
             cpdfListboxWidget.setSelectedIndexes(selectedIndexs);
             cpdfListboxWidget.updateAp();
             refresh();
         });
         FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
         if (fragmentActivity != null) {
             selectDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "optionDialogFragment");
         }
    }
}
