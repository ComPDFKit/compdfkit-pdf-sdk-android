/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.forms.pdfproperties.pdflistbox;


import androidx.fragment.app.FragmentActivity;

import com.compdfkit.core.annotation.form.CPDFListboxWidget;
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
        if (readerView.getContext() instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) readerView.getContext();
            selectDialogFragment.show(activity.getSupportFragmentManager(), "optionDialogFragment");
        }
    }
}
