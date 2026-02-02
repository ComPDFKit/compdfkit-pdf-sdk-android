/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
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
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuMarkupProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.annotation.CPDFAnnotationManager;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.tools.common.views.pdfproperties.CTypeUtil;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.proxy.CPDFMarkupAnnotImpl;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMarkupContextMenuView implements ContextMenuMarkupProvider {


    @Override
    public View createMarkupContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFMarkupAnnotImpl markupAnnotImpl) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> annotationModeConfig = CPDFApplyConfigUtil.getInstance().getAnnotationModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> markupContent = annotationModeConfig.get("markupContent");

        if (markupContent == null){
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : markupContent) {
            switch (contextMenuActionItem.key) {
                case "properties":
                    menuView.addItem(contextMenuActionItem, R.string.tools_context_menu_properties, v -> {
                        CStyleManager styleManager = new CStyleManager(markupAnnotImpl, pageView);
                        CAnnotStyle annotStyle = styleManager.getStyle(CTypeUtil.getStyleType(markupAnnotImpl.getAnnotType()));
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
                    menuView.addItem(contextMenuActionItem, R.string.tools_annot_note, v -> {
                        CPDFAnnotationManager annotationManager = new CPDFAnnotationManager();
                        annotationManager.editNote(helper.getReaderView(), pageView, markupAnnotImpl.onGetAnnotation());
                        helper.dismissContextMenu();
                    });
                    break;
                case "reply":
                    menuView.addItem(contextMenuActionItem, R.string.tools_reply, v -> {
                        new CPDFAnnotationManager().showAddReplyDialog(pageView, markupAnnotImpl, helper, true);
                        helper.dismissContextMenu();
                    });
                    break;
                case "viewReply":
                    menuView.addItem(contextMenuActionItem, R.string.tools_view_reply, v -> {
                        new CPDFAnnotationManager().showReplyDetailsDialog(pageView, markupAnnotImpl, helper);
                        helper.dismissContextMenu();
                    });
                    break;
                case "delete":
                    menuView.addItem(contextMenuActionItem, R.string.tools_delete, v -> {
                        pageView.deleteAnnotation(markupAnnotImpl);
                        helper.dismissContextMenu();
                    });
                    break;
                case "custom":
                    menuView.addItem(contextMenuActionItem, v -> {
                        Map<String, Object> extraMap = new HashMap<>();
                        extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.CONTEXT_MENU_ITEM_TAPPED);
                        extraMap.put(CPDFCustomEventField.ANNOTATION, markupAnnotImpl.onGetAnnotation());
                        CPDFCustomEventCallbackHelper.getInstance().notifyClick(contextMenuActionItem.identifier, extraMap);
                        helper.dismissContextMenu();
                    });
                    break;
            }
        }
        return menuView;
    }

}
