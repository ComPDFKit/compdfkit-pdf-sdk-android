/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuFreeTextProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.contextmenu.IContextMenuShowListener;
import com.compdfkit.ui.proxy.CPDFFreetextAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;

public class CFreeTextContextMenuView implements ContextMenuFreeTextProvider {

    @Override
    public View createFreeTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFFreetextAnnotImpl freetextAnnotImpl, IContextMenuShowListener.ContextMenuType contextMenuType) {
        if (contextMenuType == IContextMenuShowListener.ContextMenuType.Edit) {
            ContextMenuView menuView = new ContextMenuView(pageView.getContext());
            menuView.addItem(R.string.tools_context_menu_properties, v -> {
                CStyleManager styleManager = new CStyleManager(freetextAnnotImpl, pageView);
                CAnnotStyle annotStyle = styleManager.getStyle(CStyleType.ANNOT_FREETEXT);
                CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
                styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
                styleManager.setDialogHeightCallback(styleDialogFragment, helper.getReaderView());
                if (helper.getReaderView().getContext() instanceof FragmentActivity) {
                    styleDialogFragment.show(((FragmentActivity) helper.getReaderView().getContext()).getSupportFragmentManager(), "styleEditDialog");
                }
                helper.dismissContextMenu();
            });
            menuView.addItem(R.string.tools_edit, v -> {
                pageView.createInputBox(freetextAnnotImpl, IContextMenuShowListener.ContextMenuType.FreeTextEdit);
                helper.dismissContextMenu();
            });
            menuView.addItem(R.string.tools_delete, v -> {
                pageView.deleteAnnotation(freetextAnnotImpl);
                helper.dismissContextMenu();
            });
            return menuView;
        } else {
            return null;
        }
    }
}
