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

import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSelectContentProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.tools.common.pdf.CPDFApplyConfigUtil;
import com.compdfkit.tools.common.pdf.config.ContextMenuConfig;
import com.compdfkit.ui.reader.CPDFPageView;

import java.util.List;
import java.util.Map;

public class CSelectContentContextMenuView implements ContextMenuSelectContentProvider {

    @Override
    public View createSelectTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {

        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());

        Map<String, List<ContextMenuConfig.ContextMenuActionItem>> viewModeConfig = CPDFApplyConfigUtil.getInstance().getAnnotationModeContextMenuConfig();
        List<ContextMenuConfig.ContextMenuActionItem> textSelect = viewModeConfig.get("textSelect");

        if (textSelect == null) {
            return menuView;
        }

        for (ContextMenuConfig.ContextMenuActionItem contextMenuActionItem : textSelect) {
            switch (contextMenuActionItem.key) {
                case "copy":
                    menuView.addItem(R.string.tools_copy, v -> {
                        CPDFDocumentPermissionInfo permissionInfo = helper.getReaderView().getPDFDocument().getPermissionsInfo();
                        if (permissionInfo.isAllowsCopying()) {
                            pageView.operateSelections(CPDFPageView.SelectFuncType.COPY);
                            helper.dismissContextMenu();
                        } else {
                            helper.showInputOwnerPasswordDialog(ownerPassword -> {
                                pageView.operateSelections(CPDFPageView.SelectFuncType.COPY);
                                helper.dismissContextMenu();
                            });
                        }
                    });
                    break;
                case "highlight":
                    menuView.addItem(R.string.tools_annot_highlight, v -> {
                        pageView.operateSelections(CPDFPageView.SelectFuncType.HIGHLIGHT);
                    });
                    break;
                case "underline":
                    menuView.addItem(R.string.tools_annot_underline, v -> {
                        pageView.operateSelections(CPDFPageView.SelectFuncType.UNDERLINE);
                    });
                    break;
                case "strikeout":
                    menuView.addItem(R.string.tools_context_menu_strikethrough, v -> {
                        pageView.operateSelections(CPDFPageView.SelectFuncType.STRIKEOUT);
                    });
                    break;
                case "squiggly":
                    menuView.addItem(R.string.tools_annot_squiggly, v -> {
                        pageView.operateSelections(CPDFPageView.SelectFuncType.SQUIGGLY);
                    });
                    break;
            }
        }
        return menuView;
    }
}
