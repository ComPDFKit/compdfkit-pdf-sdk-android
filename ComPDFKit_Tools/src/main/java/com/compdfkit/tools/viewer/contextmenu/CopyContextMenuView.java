/**
 * Copyright © 2014-2023 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.viewer.contextmenu;


import android.view.View;

import com.compdfkit.core.document.CPDFDocumentPermissionInfo;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.contextmenu.CPDFContextMenuHelper;
import com.compdfkit.tools.common.contextmenu.interfaces.ContextMenuSelectContentProvider;
import com.compdfkit.tools.common.contextmenu.provider.ContextMenuView;
import com.compdfkit.ui.reader.CPDFPageView;

public class CopyContextMenuView implements ContextMenuSelectContentProvider {

    @Override
    public View createSelectTextContentView(CPDFContextMenuHelper helper, CPDFPageView pageView) {
        ContextMenuView menuView = new ContextMenuView(helper.getReaderView().getContext());
        menuView.addItem(R.string.tools_copy, v -> {
            if (helper.isAllowsCopying()){
                pageView.operateSelections(CPDFPageView.SelectFuncType.COPY);
                helper.dismissContextMenu();
            }else {
                helper.showInputOwnerPasswordDialog(ownerPassword -> {
                    pageView.operateSelections(CPDFPageView.SelectFuncType.COPY);
                    helper.dismissContextMenu();
                });
            }
        });
        return menuView;
    }
}
