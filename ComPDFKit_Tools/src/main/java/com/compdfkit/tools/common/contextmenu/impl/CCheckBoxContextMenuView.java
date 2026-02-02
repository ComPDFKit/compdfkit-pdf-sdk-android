/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.contextmenu.impl;

import android.view.View;

import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuCheckBoxProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CAnnotStyle;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleDialogFragment;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.CStyleType;
import com.compdfkit.tools.common.views.pdfproperties.pdfstyle.manager.CStyleManager;
import com.compdfkit.ui.proxy.form.CPDFCheckboxWidgetImpl;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;


public class CCheckBoxContextMenuView implements ContextMenuCheckBoxProvider {

    @Override
    public View createCheckBoxContentView(CPDFContextMenuHelper helper, CPDFPageView pageView, CPDFCheckboxWidgetImpl checkboxWidgetImpl) {
        ContextMenuView menuView = new ContextMenuView(pageView.getContext());
        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> formModeConfig = CPDFApplyConfigUtil.getInstance().getFormsModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> checkBoxContent = formModeConfig.get("checkBox");

        if (checkBoxContent == null) {
            return menuView;
        }
        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : checkBoxContent) {
            switch (contextMenuActionItem.key) {
                case "properties":
                    menuView.addItem(contextMenuActionItem, R.string.tools_context_menu_properties, v -> {
                        CStyleManager styleManager = new CStyleManager(checkboxWidgetImpl, pageView);
                        CAnnotStyle style = styleManager.getStyle(CStyleType.FORM_CHECK_BOX);
                        CStyleDialogFragment styleDialogFragment = CStyleDialogFragment.newInstance(style);
                        styleManager.setAnnotStyleFragmentListener(styleDialogFragment);
                        styleManager.setDialogHeightCallback(styleDialogFragment, helper.getReaderView());
                        styleDialogFragment.show(helper.getFragmentManager());
                        helper.dismissContextMenu();
                    });
                    break;
                case "delete":
                    menuView.addItem(contextMenuActionItem, R.string.tools_delete, v -> {
                        pageView.deleteAnnotation(checkboxWidgetImpl);
                        helper.dismissContextMenu();
                    });
                    break;
                case "custom":
                    menuView.addItem(contextMenuActionItem, v -> {
                        Map<String, Object> extraMap = new java.util.HashMap<>();
                        extraMap.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, CPDFCustomEventType.CONTEXT_MENU_ITEM_TAPPED);
                        extraMap.put(CPDFCustomEventField.WIDGET, checkboxWidgetImpl.onGetAnnotation());
                        CPDFCustomEventCallbackHelper.getInstance().notifyClick(contextMenuActionItem.identifier, extraMap);
                        helper.dismissContextMenu();
                    });
                    break;
                default:break;
            }
        }
        return menuView;
    }
}
