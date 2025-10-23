/**
 * Copyright © 2014-2025 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;


import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuInkProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.proxy.CPDFInkAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;

public class CInkContextMenuView implements ContextMenuInkProvider {

    @Override
    public View createInkContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFInkAnnotImpl inkAnnotImpl) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> annotationModeConfig = CPDFApplyConfigUtil.getInstance().getAnnotationModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> inkContent = annotationModeConfig.get("inkContent");

        if (inkContent == null) {
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : inkContent) {
            switch (contextMenuActionItem.key) {
                case "properties":
                    menuView.addItem(R.string.tools_context_menu_properties, v -> {
                        CStyleManager styleManager = new CStyleManager(inkAnnotImpl, pageView);
                        CAnnotStyle annotStyle = styleManager.getStyle(CStyleType.ANNOT_INK);
                        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(annotStyle);
                        styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
                        styleManager.setDialogHeightCallback(styleDialogFragment, helper.getReaderView());
                        if (helper.getFragmentManager() != null) {
                            styleDialogFragment.show(helper.getFragmentManager(), "noteEditDialog");
                        }
                        helper.dismissContextMenu();
                    });
                    break;
                case "note":
                    menuView.addItem(R.string.tools_annot_note, v -> {
                        CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                        annotationManager.editNote(helper.getReaderView(), pageView, inkAnnotImpl.onGetAnnotation());
                        helper.dismissContextMenu();
                    });
                    break;
                case "reply":
                    menuView.addItem(R.string.tools_reply, v -> {
                        new CPDFAnnotationManager().showAddReplyDialog(pageView, inkAnnotImpl, helper, true);
                        helper.dismissContextMenu();
                    });
                    break;
                case "viewReply":
                    menuView.addItem(R.string.tools_view_reply, v -> {
                        new CPDFAnnotationManager().showReplyDetailsDialog(pageView, inkAnnotImpl, helper);
                        helper.dismissContextMenu();
                    });
                    break;
                case "delete":
                    menuView.addItem(R.string.tools_delete, v -> {
                        pageView.deleteAnnotation(inkAnnotImpl);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }
        return menuView;
    }
}
