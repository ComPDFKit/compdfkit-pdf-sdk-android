/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdflistbox;


import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.form.CPDFListboxWidget;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.forms.pdfproperties.option.select.CFormOptionSelectDialogFragment;
import com.compdfkit.ui.proxy.form.CPDFListboxWidgetImpl;

public class CustomListBoxWidgetImpl extends CPDFListboxWidgetImpl {


    @Override
    public void onListboxFocused(CPDFListboxWidget cpdfListboxWidget) {
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
