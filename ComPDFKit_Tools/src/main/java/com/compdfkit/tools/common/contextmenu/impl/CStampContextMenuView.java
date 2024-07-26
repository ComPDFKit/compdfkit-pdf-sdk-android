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

import com.compdfkit.core.annotation.CPDFStampAnnotation;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuStampProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.ui.proxy.CPDFStampAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;

public class CStampContextMenuView implements ContextMenuStampProvider {

    @Override
    public View createStampContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFStampAnnotImpl stampAnnotImpl) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        CPDFStampAnnotation annotation = (CPDFStampAnnotation) stampAnnotImpl.onGetAnnotation();
        if (annotation.isStampSignature() && !annotation.isReadOnly()) {
            menuView.addItem(R.string.tools_context_menu_sign_here, v -> {
                annotation.setReadOnly(true);
                annotation.updateAp();
                helper.getReaderView().removeAllAnnotFocus();
                pageView.invalidate();
                helper.dismissContextMenu();
            });
        }
        if (!annotation.isStampSignature()){
            menuView.addItem(R.string.tools_annot_note, v -> {
                CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                annotationManager.editNote(helper.getReaderView(), pageView, stampAnnotImpl.onGetAnnotation());
                helper.dismissContextMenu();
            });
            menuView.addItem(R.string.tools_reply, v -> {
                new CPDFAnnotationManager().showAddReplyDialog(pageView, stampAnnotImpl, helper, true);
                helper.dismissContextMenu();
            });
            menuView.addItem(R.string.tools_view_reply, v -> {
                new CPDFAnnotationManager().showReplyDetailsDialog(pageView, stampAnnotImpl, helper);
                helper.dismissContextMenu();
            });
        }
        menuView.addItem(R.string.tools_delete, v -> {
            pageView.deleteAnnotation(stampAnnotImpl);
            helper.dismissContextMenu();
        });
        return menuView;
    }
}
