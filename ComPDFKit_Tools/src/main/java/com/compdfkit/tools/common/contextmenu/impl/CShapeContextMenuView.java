/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
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
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuShapeProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.views.pdfproperties.CTypeUtil;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.proxy.CPDFBaseAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;

public class CShapeContextMenuView implements ContextMenuShapeProvider {

    @Override
    public View createShapeContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFBaseAnnotImpl annotImpl) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());
        menuView.addItem(R.string.tools_context_menu_properties, v -> {
            CStyleManager styleManager = new CStyleManager(annotImpl, pageView);
            CAnnotStyle annotStyle = styleManager.getStyle(CTypeUtil.getStyleType(annotImpl.getAnnotType()));
            CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
            styleManager.setDialogHeightCallback(styleDialogFragment, helper.getReaderView());
            styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
            if (helper.getReaderView().getContext() instanceof FragmentActivity) {
                styleDialogFragment.show(((FragmentActivity) helper.getReaderView().getContext()).getSupportFragmentManager(), "noteEditDialog");
            }
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_annot_note, v -> {
            CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
            annotationManager.editNote(helper.getReaderView(), pageView, annotImpl.onGetAnnotation());
            helper.dismissContextMenu();
        });
        menuView.addItem(R.string.tools_delete, v -> {
            pageView.deleteAnnotation(annotImpl);
            helper.dismissContextMenu();
        });
        return menuView;
    }
}
