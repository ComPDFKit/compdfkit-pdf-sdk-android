/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdfcombobox;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.form.CPDFComboboxWidget;
import com.compdfkit.tools.common.utils.viewutils.CViewUtils;
import com.compdfkit.tools.forms.pdfproperties.option.select.CFormOptionSelectDialogFragment;
import com.compdfkit.ui.proxy.form.CPDFComboboxWidgetImpl;


public class CustomComboBoxWidgetImpl extends CPDFComboboxWidgetImpl {


    @Override
    public void onComboboxFocused(CPDFComboboxWidget cpdfComboboxWidget) {
        CFormOptionSelectDialogFragment selectDialogFragment = CFormOptionSelectDialogFragment.newInstance();
        selectDialogFragment.setPdfWidgetItems(cpdfComboboxWidget);
        selectDialogFragment.setSelectOptionListener((selectedIndexs) -> {
            cpdfComboboxWidget.setSelectedIndexes(selectedIndexs);
            cpdfComboboxWidget.updateAp();
            refresh();
        });
        FragmentActivity fragmentActivity = CViewUtils.getFragmentActivity(readerView.getContext());
        if (fragmentActivity != null) {
            selectDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "optionDialogFragment");
        }
    }
}
