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

import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.core.page.CPDFPage;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSelectContentProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.ui.reader.CPDFPageView;

public class CSelectContentContextMenuView implements ContextMenuSelectContentProvider {

    @Override
    public View createSelectTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());
        CPDFDocumentPermissionInfo permissionInfo = helper.getReaderView().getPDFDocument().getPermissionsInfo();
        menuView.addItem(R.string.tools_copy, v -> {
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
        menuView.addItem(R.string.tools_annot_highlight, v -> {
            pageView.operateSelections(CPDFPageView.SelectFuncType.HIGHLIGHT);
        });
        menuView.addItem(R.string.tools_annot_underline, v -> {
            pageView.operateSelections(CPDFPageView.SelectFuncType.UNDERLINE);
        });
        menuView.addItem(R.string.tools_context_menu_strikethrough, v -> {
            pageView.operateSelections(CPDFPageView.SelectFuncType.STRIKEOUT);
        });
        menuView.addItem(R.string.tools_annot_squiggly, v -> {
            pageView.operateSelections(CPDFPageView.SelectFuncType.SQUIGGLY);
        });
        return menuView;
    }
}
